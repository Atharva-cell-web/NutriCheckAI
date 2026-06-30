package com.atharva.nutricheckai.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProfileResponse {

    private Long id;

    private Integer age;

    private String gender;

    private String skinType;

    private String dietPreference;

    private String allergies;

    private String medicalConditions;

    private Boolean pregnant;
}