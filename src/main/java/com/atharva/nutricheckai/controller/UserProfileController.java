package com.atharva.nutricheckai.controller;

import com.atharva.nutricheckai.dto.UserProfileRequest;
import com.atharva.nutricheckai.dto.UserProfileResponse;
import com.atharva.nutricheckai.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    public UserProfileResponse createProfile(
            @Valid @RequestBody UserProfileRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return userProfileService.createProfile(request, email);
    }
    @GetMapping("/me")
    public UserProfileResponse getMyProfile(Authentication authentication) {

        String email = authentication.getName();

        return userProfileService.getMyProfile(email);
    }
    @PutMapping
    public UserProfileResponse updateProfile(
            @Valid @RequestBody UserProfileRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return userProfileService.updateProfile(request, email);
    }
    @DeleteMapping
    public String deleteProfile(Authentication authentication) {

        String email = authentication.getName();

        userProfileService.deleteProfile(email);

        return "Profile deleted successfully";
    }
}