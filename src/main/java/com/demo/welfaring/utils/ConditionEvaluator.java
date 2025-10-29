package com.demo.welfaring.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ConditionEvaluator {
    
    public boolean evaluateAgeCondition(Map<String, Object> criteria, Integer userAge) {
        if (userAge == null) return true; // 나이 정보가 없으면 조건 통과
        
        Integer minAge = getIntegerValue(criteria, "age_min");
        Integer maxAge = getIntegerValue(criteria, "age_max");
        
        if (minAge != null && userAge < minAge) return false;
        if (maxAge != null && userAge > maxAge) return false;
        
        return true;
    }
    
    public boolean evaluateDiseaseCondition(Map<String, Object> criteria, List<String> userDiseases) {
        List<String> requiredDiseases = getStringListValue(criteria, "diseases");
        if (requiredDiseases == null || requiredDiseases.isEmpty()) return true;
        
        if (userDiseases == null || userDiseases.isEmpty()) return false;
        
        return userDiseases.stream().anyMatch(disease -> requiredDiseases.contains(disease));
    }
    
    public boolean evaluateChronicDiseasesCondition(Map<String, Object> criteria, List<String> userChronicDiseases) {
        List<String> requiredChronicDiseases = getStringListValue(criteria, "chronic_diseases");
        if (requiredChronicDiseases == null || requiredChronicDiseases.isEmpty()) return true;
        
        if (userChronicDiseases == null || userChronicDiseases.isEmpty()) return false;
        
        return userChronicDiseases.stream().anyMatch(disease -> requiredChronicDiseases.contains(disease));
    }
    
    public boolean evaluateInsuranceTypeCondition(Map<String, Object> criteria, String userInsuranceType) {
        List<String> allowedTypes = getStringListValue(criteria, "insurance_type");
        if (allowedTypes == null || allowedTypes.isEmpty()) return true;
        
        if (userInsuranceType == null) return false;
        
        return allowedTypes.contains(userInsuranceType);
    }
    
    public boolean evaluateLongTermCareGradeCondition(Map<String, Object> criteria, Integer userLongTermCareGrade) {
        List<Object> allowedGrades = getListValue(criteria, "long_term_care_grade");
        if (allowedGrades == null || allowedGrades.isEmpty()) return true;
        
        if (userLongTermCareGrade == null) return false;
        
        return allowedGrades.stream().anyMatch(grade -> {
            if (grade instanceof Integer) {
                return grade.equals(userLongTermCareGrade);
            } else if (grade instanceof String) {
                return grade.toString().equals(userLongTermCareGrade.toString());
            }
            return false;
        });
    }
    
    public boolean evaluateIncomeCondition(Map<String, Object> criteria, Integer userIncome) {
        Integer maxIncome = getIntegerValue(criteria, "income_max");
        if (maxIncome == null) return true;
        
        if (userIncome == null) return false;
        
        return userIncome <= maxIncome;
    }
    
    public boolean evaluatePropertyValueCondition(Map<String, Object> criteria, Integer userPropertyValue) {
        Integer maxPropertyValue = getIntegerValue(criteria, "property_value_max");
        if (maxPropertyValue == null) return true;
        
        if (userPropertyValue == null) return false;
        
        return userPropertyValue <= maxPropertyValue;
    }
    
    public boolean evaluateFamilyMembersCondition(Map<String, Object> criteria, Integer userFamilyMembers) {
        Integer requiredFamilyMembers = getIntegerValue(criteria, "family_members");
        if (requiredFamilyMembers == null) return true;
        
        if (userFamilyMembers == null) return false;
        
        return userFamilyMembers >= requiredFamilyMembers;
    }
    
    public boolean evaluateBasicRecipientCondition(Map<String, Object> criteria, Boolean userIsBasicRecipient) {
        Boolean requiredBasicRecipient = getBooleanValue(criteria, "is_basic_recipient");
        if (requiredBasicRecipient == null) return true;
        
        if (userIsBasicRecipient == null) return false;
        
        return userIsBasicRecipient.equals(requiredBasicRecipient);
    }
    
    public boolean evaluateLowIncomeCondition(Map<String, Object> criteria, Boolean userIsLowIncome) {
        Boolean requiredLowIncome = getBooleanValue(criteria, "is_low_income");
        if (requiredLowIncome == null) return true;
        
        if (userIsLowIncome == null) return false;
        
        return userIsLowIncome.equals(requiredLowIncome);
    }
    
    public boolean evaluateHospitalizedCondition(Map<String, Object> criteria, Boolean userIsHospitalized) {
        Boolean requiredHospitalized = getBooleanValue(criteria, "is_hospitalized");
        if (requiredHospitalized == null) return true;
        
        if (userIsHospitalized == null) return false;
        
        return userIsHospitalized.equals(requiredHospitalized);
    }
    
    public boolean evaluateHospitalTypeCondition(Map<String, Object> criteria, String userHospitalType) {
        List<String> allowedHospitalTypes = getStringListValue(criteria, "hospital_type");
        if (allowedHospitalTypes == null || allowedHospitalTypes.isEmpty()) return true;
        
        if (userHospitalType == null) return false;
        
        return allowedHospitalTypes.contains(userHospitalType);
    }
    
    public boolean evaluatePregnantCondition(Map<String, Object> criteria, Boolean userIsPregnant) {
        Boolean requiredPregnant = getBooleanValue(criteria, "is_pregnant");
        if (requiredPregnant == null) return true;
        
        if (userIsPregnant == null) return false;
        
        return userIsPregnant.equals(requiredPregnant);
    }
    
    public boolean evaluateDisabledCondition(Map<String, Object> criteria, Boolean userIsDisabled) {
        Boolean requiredDisabled = getBooleanValue(criteria, "is_disabled");
        if (requiredDisabled == null) return true;
        
        if (userIsDisabled == null) return false;
        
        return userIsDisabled.equals(requiredDisabled);
    }
    
    public boolean evaluateRegionCondition(Map<String, Object> criteria, String userRegion) {
        List<String> allowedRegions = getStringListValue(criteria, "region");
        if (allowedRegions == null || allowedRegions.isEmpty()) return true;
        
        if (userRegion == null) return false;
        
        return allowedRegions.stream().anyMatch(region -> userRegion.contains(region));
    }
    
    public boolean evaluateGenderCondition(Map<String, Object> criteria, String userGender) {
        List<String> allowedGenders = getStringListValue(criteria, "gender");
        if (allowedGenders == null || allowedGenders.isEmpty()) return true;
        
        if (userGender == null) return false;
        
        return allowedGenders.contains(userGender);
    }
    
    public boolean evaluateDailyLifeDifficultyCondition(Map<String, Object> criteria, String userDailyLifeDifficulty) {
        String requiredDifficulty = getStringValue(criteria, "daily_life_difficulty");
        if (requiredDifficulty == null || requiredDifficulty.isEmpty()) return true;
        
        if (userDailyLifeDifficulty == null) return false;
        
        return userDailyLifeDifficulty.contains(requiredDifficulty);
    }
    
    // Helper methods
    private Integer getIntegerValue(Map<String, Object> criteria, String key) {
        Object value = criteria.get(key);
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof String) {
            try { 
                return Integer.parseInt((String) value); 
            } catch (NumberFormatException e) { 
                return null; 
            }
        }
        return null;
    }
    
    private String getStringValue(Map<String, Object> criteria, String key) {
        Object value = criteria.get(key);
        if (value instanceof String) return (String) value;
        if (value != null) return value.toString();
        return null;
    }
    
    private Boolean getBooleanValue(Map<String, Object> criteria, String key) {
        Object value = criteria.get(key);
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }
    
    private List<String> getStringListValue(Map<String, Object> criteria, String key) {
        Object value = criteria.get(key);
        if (value instanceof List) {
            return ((List<?>) value).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private List<Object> getListValue(Map<String, Object> criteria, String key) {
        Object value = criteria.get(key);
        if (value instanceof List) {
            return (List<Object>) value;
        }
        return null;
    }
}
