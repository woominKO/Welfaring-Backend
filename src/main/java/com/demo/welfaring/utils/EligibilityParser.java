package com.demo.welfaring.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class EligibilityParser {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public Map<String, Object> parseTargetCriteria(String jsonCriteria) {
        try {
            return objectMapper.readValue(jsonCriteria, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("조건 파싱 실패: {}", e.getMessage());
            throw new RuntimeException("조건 파싱 실패: " + e.getMessage());
        }
    }
    
    public Map<String, Object> parseDataSource(String jsonDataSource) {
        try {
            return objectMapper.readValue(jsonDataSource, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("데이터 소스 파싱 실패: {}", e.getMessage());
            throw new RuntimeException("데이터 소스 파싱 실패: " + e.getMessage());
        }
    }
    
    public boolean hasCondition(Map<String, Object> criteria, String conditionKey) {
        return criteria.containsKey(conditionKey) && criteria.get(conditionKey) != null;
    }
    
    public boolean isConditionEmpty(Map<String, Object> criteria, String conditionKey) {
        Object value = criteria.get(conditionKey);
        if (value == null) return true;
        
        if (value instanceof String) {
            return ((String) value).trim().isEmpty();
        }
        
        if (value instanceof java.util.List) {
            return ((java.util.List<?>) value).isEmpty();
        }
        
        return false;
    }
}
