package com.demo.welfaring.controller;

import com.demo.welfaring.domain.UserProfile;
import com.demo.welfaring.dto.MatchingResponseDTO;
import com.demo.welfaring.dto.UserProfileDTO;
import com.demo.welfaring.service.MatchingService;
import com.demo.welfaring.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
@Slf4j
public class MatchController {
    
    private final MatchingService matchingService;
    private final OpenAIService openAIService;
    
    @PostMapping("/ai")
    public ResponseEntity<MatchingResponseDTO> matchBenefitsWithAI(@RequestBody UserProfileDTO userProfileDTO) {
        try {
            log.info("AI 매칭 요청 수신: 나이={}, 지역={}", userProfileDTO.getAge(), userProfileDTO.getRegion());
            
            // 1. UserProfileDTO를 UserProfile로 변환
            UserProfile userProfile = convertToUserProfile(userProfileDTO);
            
            // 2. 혜택 매칭 수행
            MatchingResponseDTO response = matchingService.findMatchingBenefits(userProfile);
            
            // 3. AI 요약문 생성 (매칭된 혜택이 있을 때만)
            if (!response.getMatchedBenefits().isEmpty()) {
                String aiSummary = openAIService.generateSummary(userProfile, response.getMatchedBenefits());
                response.setAiSummary(aiSummary);
            }
            
            log.info("AI 매칭 완료: {}개 혜택 매칭", response.getTotalCount());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("AI 매칭 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(MatchingResponseDTO.builder()
                            .matchedBenefits(java.util.Collections.emptyList())
                            .aiSummary("매칭 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                            .totalCount(0)
                            .build());
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Welfaring Backend is running!");
    }
    
    private UserProfile convertToUserProfile(UserProfileDTO dto) {
        return UserProfile.builder()
                .age(dto.getAge())
                .gender(dto.getGender())
                .region(dto.getRegion())
                .insuranceType(dto.getInsuranceType())
                .isBasicRecipient(dto.getIsBasicRecipient())
                .isLowIncome(dto.getIsLowIncome())
                .longTermCareGrade(dto.getLongTermCareGrade())
                .diseases(dto.getDiseases())
                .chronicDiseases(dto.getChronicDiseases())
                .isDisabled(dto.getIsDisabled())
                .isPregnant(dto.getIsPregnant())
                .isHospitalized(dto.getIsHospitalized())
                .hospitalType(dto.getHospitalType())
                .occupation(dto.getOccupation())
                .income(dto.getIncome())
                .propertyValue(dto.getPropertyValue())
                .familyMembers(dto.getFamilyMembers())
                .dailyLifeDifficulty(dto.getDailyLifeDifficulty())
                .build();
    }
}
