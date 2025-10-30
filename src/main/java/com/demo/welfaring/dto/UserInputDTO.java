package com.demo.welfaring.dto;

import lombok.Data;
import java.util.List;

public class UserInputDTO {
    private Integer age;
    private String region;
    private List<String> healthConditions;
    private Boolean disability;
    private String gender;
    private String insurance_type;
    private String description;
}
