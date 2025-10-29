package com.demo.welfaring.service;

import com.demo.welfaring.domain.Benefit;
import com.demo.welfaring.domain.UserProfile;
import com.demo.welfaring.dto.BenefitDTO;
import com.demo.welfaring.dto.MatchingResponseDTO;
import com.demo.welfaring.repository.BenefitRepository;
import com.demo.welfaring.utils.EligibilityParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {
    
    private final BenefitRepository benefitRepository;
    private final MatchingRuleEngine matchingRuleEngine;
    private final EligibilityParser eligibilityParser;
    
    public MatchingResponseDTO findMatchingBenefits(UserProfile userProfile) {
        log.info("사용자 프로필 기반 혜택 매칭 시작: 나이={}, 지역={}", userProfile.getAge(), userProfile.getRegion());
        
        // 1. 모든 혜택 조회
        List<Benefit> allBenefits = benefitRepository.findAllBenefits();
        log.info("총 {}개 혜택 조회", allBenefits.size());
        
        // 2. 조건에 맞는 혜택 필터링
        List<Benefit> matchedBenefits = matchingRuleEngine.findMatchingBenefits(userProfile, allBenefits);
        
        // 3. 매칭 결과가 없으면 빈 결과 반환
        if (matchedBenefits.isEmpty()) {
            log.info("매칭된 혜택이 없습니다.");
            return MatchingResponseDTO.noMatch();
        }
        
        // 4. DTO로 변환하여 반환
        List<BenefitDTO> benefitDTOs = matchedBenefits.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        MatchingResponseDTO response = MatchingResponseDTO.builder()
                .matchedBenefits(benefitDTOs)
                .totalCount(benefitDTOs.size())
                .build();
        
        log.info("{}개 혜택 매칭 완료", benefitDTOs.size());
        return response;
    }
    
    private BenefitDTO convertToDTO(Benefit benefit) {
        try {
            return BenefitDTO.builder()
                    .benefitId(benefit.getBenefitId())
                    .benefitName(benefit.getBenefitName())
                    .category(benefit.getCategory())
                    .provider(benefit.getProvider())
                    .benefitDescription(benefit.getBenefitDescription())
                    .applicationMethod(benefit.getApplicationMethod())
                    .lawReference(benefit.getLawReference())
                    .lastUpdated(benefit.getLastUpdated())
                    .targetCriteria(eligibilityParser.parseTargetCriteria(benefit.getTargetCriteria()))
                    .dataSource(eligibilityParser.parseDataSource(benefit.getDataSource()))
                    .build();
        } catch (Exception e) {
            log.error("BenefitDTO 변환 중 오류 발생: {}", e.getMessage());
            // 기본값으로 DTO 생성
            return BenefitDTO.builder()
                    .benefitId(benefit.getBenefitId())
                    .benefitName(benefit.getBenefitName())
                    .category(benefit.getCategory())
                    .provider(benefit.getProvider())
                    .benefitDescription(benefit.getBenefitDescription())
                    .applicationMethod(benefit.getApplicationMethod())
                    .lawReference(benefit.getLawReference())
                    .lastUpdated(benefit.getLastUpdated())
                    .build();
        }
    }
}
