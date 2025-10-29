package com.demo.welfaring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BenefitDTO {
    
    private Long benefitId;
    private String benefitName;
    private String category;
    private String provider;
    private String benefitDescription;
    private String applicationMethod;
    private String lawReference;
    private Map<String, Object> targetCriteria;  // JSON 파싱된 조건
    private Map<String, Object> dataSource;      // JSON 파싱된 데이터 소스
    private LocalDate lastUpdated;
}
