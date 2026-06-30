package com.atharva.nutricheckai.dto;

import jakarta.validation.constraints.*;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProfileRequest {

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be greater than 0")
    @Max(value = 120, message = "Age must be less than or equal to 120")
    private Integer age;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Skin type is required")
    private String skinType;

    @NotBlank(message = "Diet preference is required")
    private String dietPreference;

    private String allergies;

    private String medicalConditions;

    @NotNull(message = "Pregnant field is required")
    private Boolean pregnant;


}