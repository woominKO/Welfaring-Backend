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

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {

    @Qualifier("openAIWebClient")
    private final WebClient webClient;

    @Qualifier("openAIModel")
    private final String model;
    
    // ObjectMapper를 final로 선언하고 생성자 주입 또는 빈 등록을 권장하나, 여기서는 원본 코드를 유지합니다.
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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
            requestBody.put("model", model);
            
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
