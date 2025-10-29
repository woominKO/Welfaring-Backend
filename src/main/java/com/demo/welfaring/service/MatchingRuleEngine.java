package com.demo.welfaring.service;

import com.demo.welfaring.domain.Benefit;
import com.demo.welfaring.domain.UserProfile;
import com.demo.welfaring.utils.ConditionEvaluator;
import com.demo.welfaring.utils.EligibilityParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingRuleEngine {
    
    private final ConditionEvaluator conditionEvaluator;
    private final EligibilityParser eligibilityParser;
    
    public List<Benefit> findMatchingBenefits(UserProfile userProfile, List<Benefit> allBenefits) {
        List<Benefit> matchedBenefits = new ArrayList<>();
        
        // 모든 혜택을 순회하면서 조건 체크
        for (Benefit benefit : allBenefits) {
            if (isEligible(userProfile, benefit)) {
                matchedBenefits.add(benefit);
                log.debug("혜택 매칭 성공: {}", benefit.getBenefitName());
            }
        }
        
        log.info("총 {}개 혜택 중 {}개 매칭", allBenefits.size(), matchedBenefits.size());
        return matchedBenefits;
    }
    
    private boolean isEligible(UserProfile userProfile, Benefit benefit) {
        try {
            // JSON 조건 파싱
            Map<String, Object> criteria = eligibilityParser.parseTargetCriteria(benefit.getTargetCriteria());
            
            // 각 조건별 순차적 평가 (하나라도 false면 false)
            if (!conditionEvaluator.evaluateAgeCondition(criteria, userProfile.getAge())) {
                log.debug("나이 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluateDiseaseCondition(criteria, userProfile.getDiseases())) {
                log.debug("질병 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluateChronicDiseasesCondition(criteria, userProfile.getChronicDiseases())) {
                log.debug("만성질병 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluateInsuranceTypeCondition(criteria, userProfile.getInsuranceType())) {
                log.debug("보험유형 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluateLongTermCareGradeCondition(criteria, userProfile.getLongTermCareGrade())) {
                log.debug("장기요양등급 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluateIncomeCondition(criteria, userProfile.getIncome())) {
                log.debug("소득 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluatePropertyValueCondition(criteria, userProfile.getPropertyValue())) {
                log.debug("재산 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluateFamilyMembersCondition(criteria, userProfile.getFamilyMembers())) {
                log.debug("가족구성원 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluateBasicRecipientCondition(criteria, userProfile.getIsBasicRecipient())) {
                log.debug("기초수급자 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluateLowIncomeCondition(criteria, userProfile.getIsLowIncome())) {
                log.debug("저소득층 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluateHospitalizedCondition(criteria, userProfile.getIsHospitalized())) {
                log.debug("입원여부 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluateHospitalTypeCondition(criteria, userProfile.getHospitalType())) {
                log.debug("병원유형 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluatePregnantCondition(criteria, userProfile.getIsPregnant())) {
                log.debug("임신여부 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluateDisabledCondition(criteria, userProfile.getIsDisabled())) {
                log.debug("장애여부 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluateRegionCondition(criteria, userProfile.getRegion())) {
                log.debug("지역 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluateGenderCondition(criteria, userProfile.getGender())) {
                log.debug("성별 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            if (!conditionEvaluator.evaluateDailyLifeDifficultyCondition(criteria, userProfile.getDailyLifeDifficulty())) {
                log.debug("일상생활 어려움 조건 불일치: {}", benefit.getBenefitName());
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            log.warn("혜택 {} 조건 평가 중 오류 발생: {}", benefit.getBenefitName(), e.getMessage());
            return false; // 오류 발생 시 해당 혜택은 제외
        }
    }
}
