package com.demo.welfaring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    
    private Integer age;
    private String gender;
    private String region;
    private String insuranceType;
    private Boolean isBasicRecipient;
    private Boolean isLowIncome;
    private Integer longTermCareGrade;
    private List<String> diseases;
    private List<String> chronicDiseases;
    private Boolean isDisabled;
    private Boolean isPregnant;
    private Boolean isHospitalized;
    private String hospitalType;
    private String occupation;
    private Integer income;
    private Integer propertyValue;
    private Integer familyMembers;
    private String dailyLifeDifficulty;
}
