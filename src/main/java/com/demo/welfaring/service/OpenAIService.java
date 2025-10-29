package com.demo.welfaring.service;

import com.demo.welfaring.domain.UserProfile;
import com.demo.welfaring.dto.BenefitDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public String generateSummary(UserProfile userProfile, List<BenefitDTO> matchedBenefits) {
        try {
            log.info("AI 요약문 생성 시작: {}개 혜택", matchedBenefits.size());
            
            String prompt = buildPrompt(userProfile, matchedBenefits);
            String response = callOpenAI(prompt);
            
            log.info("AI 요약문 생성 완료");
            return response;
            
        } catch (Exception e) {
            log.error("AI 요약문 생성 중 오류 발생: {}", e.getMessage(), e);
            return generateFallbackSummary(userProfile, matchedBenefits);
        }
    }
    
    private String buildPrompt(UserProfile userProfile, List<BenefitDTO> matchedBenefits) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음 사용자 정보와 매칭된 혜택들을 바탕으로 간단하고 이해하기 쉬운 요약문을 작성해주세요.\n\n");
        
        // 사용자 정보
        prompt.append("사용자 정보:\n");
        prompt.append("- 나이: ").append(userProfile.getAge() != null ? userProfile.getAge() + "세" : "미입력").append("\n");
        prompt.append("- 성별: ").append(userProfile.getGender() != null ? userProfile.getGender() : "미입력").append("\n");
        prompt.append("- 지역: ").append(userProfile.getRegion() != null ? userProfile.getRegion() : "미입력").append("\n");
        prompt.append("- 보험유형: ").append(userProfile.getInsuranceType() != null ? userProfile.getInsuranceType() : "미입력").append("\n");
        
        if (userProfile.getDiseases() != null && !userProfile.getDiseases().isEmpty()) {
            prompt.append("- 질병: ").append(String.join(", ", userProfile.getDiseases())).append("\n");
        }
        
        if (userProfile.getIsBasicRecipient() != null && userProfile.getIsBasicRecipient()) {
            prompt.append("- 기초수급자: 예\n");
        }
        
        if (userProfile.getIsLowIncome() != null && userProfile.getIsLowIncome()) {
            prompt.append("- 저소득층: 예\n");
        }
        
        if (userProfile.getIsHospitalized() != null && userProfile.getIsHospitalized()) {
            prompt.append("- 입원중: 예\n");
        }
        
        prompt.append("\n매칭된 혜택들:\n");
        for (int i = 0; i < matchedBenefits.size(); i++) {
            BenefitDTO benefit = matchedBenefits.get(i);
            prompt.append(i + 1).append(". ").append(benefit.getBenefitName()).append("\n");
            prompt.append("   - 제공기관: ").append(benefit.getProvider()).append("\n");
            prompt.append("   - 설명: ").append(benefit.getBenefitDescription()).append("\n");
            prompt.append("   - 신청방법: ").append(benefit.getApplicationMethod()).append("\n\n");
        }
        
        prompt.append("위 정보를 바탕으로 사용자가 받을 수 있는 혜택에 대해 친근하고 이해하기 쉬운 요약문을 작성해주세요. (100자 이내)");
        
        return prompt.toString();
    }
    
    private String callOpenAI(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("max_tokens", 200);
            requestBody.put("temperature", 0.7);
            
            String response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
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
    
    private String generateFallbackSummary(UserProfile userProfile, List<BenefitDTO> matchedBenefits) {
        StringBuilder summary = new StringBuilder();
        
        if (userProfile.getAge() != null) {
            summary.append(userProfile.getAge()).append("세");
        }
        
        if (userProfile.getGender() != null) {
            summary.append(" ").append(userProfile.getGender());
        }
        
        if (userProfile.getRegion() != null) {
            summary.append(" ").append(userProfile.getRegion()).append(" 거주");
        }
        
        summary.append(" 사용자는 ");
        
        if (matchedBenefits.size() == 1) {
            summary.append(matchedBenefits.get(0).getBenefitName()).append(" 혜택을 받을 수 있습니다.");
        } else {
            summary.append(matchedBenefits.size()).append("개의 혜택을 받을 수 있습니다.");
        }
        
        return summary.toString();
    }
}
