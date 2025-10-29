package com.demo.welfaring.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
@Table(name = "benefits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Benefit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "benefit_id")
    private Long benefitId;
    
    @Column(name = "benefit_name", nullable = false)
    private String benefitName;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "provider")
    private String provider;
    
    @Column(name = "benefit_description", columnDefinition = "TEXT")
    private String benefitDescription;
    
    @Column(name = "application_method", columnDefinition = "TEXT")
    private String applicationMethod;
    
    @Column(name = "law_reference", columnDefinition = "TEXT")
    private String lawReference;
    
    @Column(name = "target_criteria", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String targetCriteria; // JSON 문자열로 저장
    
    @Column(name = "data_source", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String dataSource; // JSON 문자열로 저장
    
    @Column(name = "last_updated")
    private LocalDate lastUpdated;
}
