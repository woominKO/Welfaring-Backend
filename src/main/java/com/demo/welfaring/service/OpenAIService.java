package com.demo.welfaring.service;

import com.demo.welfaring.domain.UserProfile;
import com.demo.welfaring.dto.BenefitDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//추가
import com.demo.welfaring.dto.UserInputDTO;
import com.demo.welfaring.dto.UserProfileDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {

    @Qualifier("openAIWebClient")
    private final WebClient webClient;

    // ObjectMapper를 final로 선언하고 생성자 주입 또는 빈 등록을 권장하나, 여기서는 원본 코드를 유지합니다.
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public UserProfileDTO normalizeUserData(UserInputDTO input) {
        try {
            log.info("OpenAI에 사용자 데이터 정규화 요청 시작");

            // 1. [System] 메시지: AI의 역할 정의
            String systemMessage = buildNormalizationSystemPrompt();

            // 2. [User] 메시지: 원본 데이터와 목표 스키마 전달
            String userMessage = buildNormalizationUserPrompt(input);

            // 3. OpenAI API 호출
            String responseBody = callOpenAIFullResponse(systemMessage, userMessage);

            // 4. 응답에서 가공된 JSON 콘텐츠 추출 및 DTO로 변환
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String content = jsonNode.get("choices").get(0).get("message").get("content").asText();

            log.info("OpenAI 정규화 완료. 변환된 JSON: {}", content);

            // 최종 UserProfileDTO로 변환
            return objectMapper.readValue(content, UserProfileDTO.class);

        } catch (WebClientResponseException e) {
            log.error("OpenAI API 정규화 호출 실패: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("데이터 정규화 중 OpenAI API 오류 발생: " + e.getMessage());
        } catch (Exception e) {
            log.error("OpenAI 정규화 처리 중 오류 발생", e);
            throw new RuntimeException("데이터 정규화 처리 실패: " + e.getMessage());
        }
    }

    // ... (generateSummary 메서드 및 기존 로직은 그대로 유지)

    // --- 정규화 관련 헬퍼 함수 ---
    private String buildNormalizationSystemPrompt() throws JsonProcessingException {
        // 목표 DTO의 구조를 JSON 스키마로 변환하여 프롬프트에 포함 (자세한 스키마는 UserProfileDTO.java 참고)
        UserProfileDTO targetSchema = UserProfileDTO.builder().build(); // 빈 빌더로 구조만 참고
        String schemaJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(targetSchema);

        return """
            당신은 사용자로부터 받은 의료 관련 데이터를 정부 및 복지 혜택 심사에 적합한 JSON 형식으로 변환하는 전문가입니다.
            사용자가 제공한 원본 데이터와 'description' 필드의 서술 내용을 최대한 활용하여, 다음 스키마를 따르는 JSON 객체를 생성해야 합니다.

            [목표 JSON 스키마]
            %s
            
            [핵심 변환 규칙]
            1. 'region'은 '서울특별시 강북구'에서 **'서울특별시'**처럼 시/도 단위로 변환합니다.
            2. 'gender'는 '남성'을 **'M'**으로, '여성'을 **'F'**로 변환합니다.
            3. 원본 데이터에 없는 정보는 복지 심사에서 필수적이지 않은 한, **null** 또는 **false**를 사용하십시오.
            4. 'description'의 문장을 분석하여 'income', 'propertyValue', 'familyMembers' 등의 수치를 추정하여 채우십시오.
            5. 최종 결과는 반드시 **JSON 객체**만 포함해야 하며, 어떠한 설명이나 추가 텍스트도 포함해서는 안 됩니다.
            """.formatted(schemaJson);
    }

    private String buildNormalizationUserPrompt(UserInputDTO input) throws JsonProcessingException {
        return "원본 입력 데이터: " + objectMapper.writeValueAsString(input);
    }

    /**
     * OpenAI API 호출을 수행하고 전체 응답 문자열을 반환합니다.
     */
    private String callOpenAIFullResponse(String systemMessage, String userMessage) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        // messages 배열 구성
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", systemMessage),
                Map.of("role", "user", "content", userMessage)
        ));

        // JSON 응답 형식을 강제 (GPT-4o-mini 이상에서 사용 가능하며, gpt-3.5-turbo 대신 gpt-4o-mini를 사용하는 것이 좋습니다.)
        // application.properties의 모델 설정도 gpt-4o-mini로 변경을 권장합니다.
        requestBody.put("response_format", Map.of("type", "json_object"));

        requestBody.put("temperature", 0.0); // 데이터 정규화는 창의성보다 정확성이 중요

        return webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }




    public String generateSummary(UserProfile userProfile, List<BenefitDTO> matchedBenefits) {
        try {
            log.info("AI 요약문 생성 시작: 혜택 {}개", matchedBenefits.size());

            // 1. [System] 메시지 (AI의 역할과 규칙 정의)
            String systemMessage = """
                당신은 대한민국 복지 혜택 매칭 결과를 요약하는 AI 어시스턴트입니다.
                당신의 임무는 [사용자 상세 정보]와 [매칭된 혜택 목록]을 기반으로, '어떤 조건 때문에(Why)' '무엇을(What)' 받을 수 있는지 자연스러운 한국어 문장으로 설명하는 것입니다.

                [규칙]
                1. 요약문은 반드시 단 하나의 문장으로 완성합니다.
                2. [사용자 상세 정보](JSON)를 분석하여 매칭의 '핵심 조건'(예: 질병, 소득, 나이, 입원상태)을 찾아내 '이유'로 제시합니다.
                3. [매칭된 혜택 목록]에 있는 모든 `benefitName`을 명확히 나열합니다.
                4. '...[이유]... (으)로 ...[혜택]... 대상입니다.' 또는 '...[이유]... 하여 ...[혜택]... (을)를 받을 수 있습니다.'와 같이 인과관계가 명확하게 문장을 구성합니다.
                5. 매우 간결하고 이해하기 쉬운 문장으로 작성합니다.
                """;

            // 2. [User] 메시지 (AI에게 전달할 원본 데이터 JSON)
            String userMessage = buildUserMessage(userProfile, matchedBenefits);

            // 3. OpenAI API 호출
            String response = callOpenAI(systemMessage, userMessage);
            
            // API 호출 실패 또는 비어있는 응답일 경우 대체 요약 사용
            if (response == null || response.isBlank()) {
                log.warn("AI 응답이 비어있거나 null 입니다. 대체 요약을 사용합니다.");
                return generateFallbackSummary(userProfile, matchedBenefits);
            }
            
            log.info("AI 요약문 생성 완료");
            return response;

        } catch (Exception e) {
            log.error("AI 요약문 생성 중 오류 발생: {}", e.getMessage(), e);
            return generateFallbackSummary(userProfile, matchedBenefits); // 오류 시 대체 요약
        }
    }

    /**
     * AI에게 전달할 User 메시지(JSON 데이터 포함)를 생성합니다.
     */
    private String buildUserMessage(UserProfile userProfile, List<BenefitDTO> matchedBenefits) throws JsonProcessingException {
        // DTO를 JSON 문자열로 변환
        String userProfileJson = objectMapper.writeValueAsString(userProfile);
        String matchedBenefitsJson = objectMapper.writeValueAsString(matchedBenefits);

        // .formatted()를 사용하여 텍스트 블록에 JSON 삽입
        return """
            다음 데이터를 기반으로 최종 요약문을 생성해 주십시오.

            [사용자 상세 정보 (JSON)]
            %s

            [매칭된 혜택 목록 (JSON)]
            %s
            """.formatted(userProfileJson, matchedBenefitsJson);
    }

    /**
     * OpenAI API를 호출합니다.
     */
    private String callOpenAI(String systemMessage, String userMessage) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            
            // ★ System과 User 역할을 분리하여 전달
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", systemMessage),
                Map.of("role", "user", "content", userMessage)
            ));
            
            requestBody.put("max_tokens", 250); // 요약문이므로 넉넉하게
            requestBody.put("temperature", 0.2); // 요약은 일관성이 중요 (0.7은 너무 높음)

            String response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // 비동기 환경이 아니라면 block 사용 (실제로는 비동기 처리가 더 좋음)

            // JSON 응답에서 content 추출
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("choices").get(0).get("message").get("content").asText();

        } catch (WebClientResponseException e) {
            // 401 등 인증 실패 시에는 예외 전파 대신 null 반환하여 상위에서 대체 요약 처리
            if (e.getStatusCode() != null && e.getStatusCode().value() == 401) {
                log.warn("OpenAI API 인증 실패(401). 환경변수 OPENAI_API_KEY를 확인하세요. 응답: {}", e.getResponseBodyAsString());
                return null;
            }
            log.error("OpenAI API 호출 실패: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("OpenAI API 호출 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("OpenAI API 호출 중 오류: {}", e.getMessage());
            throw new RuntimeException("OpenAI API 호출 중 오류: " + e.getMessage());
        }
    }

    /**
     * AI 호출 실패 시 사용될 대체 요약문
     */
    private String generateFallbackSummary(UserProfile userProfile, List<BenefitDTO> matchedBenefits) {
        if (matchedBenefits == null || matchedBenefits.isEmpty()) {
            return "현재 조건에 맞는 혜택을 찾지 못했습니다.";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("현재 ");
        
        if (matchedBenefits.size() == 1) {
            summary.append(matchedBenefits.get(0).getBenefitName());
        } else {
            summary.append(matchedBenefits.get(0).getBenefitName()).append(" 외 ");
            summary.append(matchedBenefits.size() - 1).append("개");
        }
        summary.append("의 혜택을 받을 수 있습니다. 자세한 내용은 목록을 확인해주세요.");
        
        return summary.toString();
    }
}
