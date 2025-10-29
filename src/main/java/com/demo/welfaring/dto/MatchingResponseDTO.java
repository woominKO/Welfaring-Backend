package com.demo.welfaring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingResponseDTO {
    
    private List<BenefitDTO> matchedBenefits;
    private String aiSummary;
    private int totalCount;
    
    // 매칭 결과가 없을 때
    public static MatchingResponseDTO noMatch() {
        return MatchingResponseDTO.builder()
                .matchedBenefits(new ArrayList<>())
                .aiSummary("입력하신 조건에 일치하는 혜택이 없습니다. 다른 조건으로 다시 검색해보시거나 관련 기관에 문의해주세요.")
                .totalCount(0)
                .build();
    }
}
