package com.atharva.nutricheckai.service;

import com.atharva.nutricheckai.dto.UserProfileRequest;
import com.atharva.nutricheckai.dto.UserProfileResponse;
import com.atharva.nutricheckai.entity.User;
import com.atharva.nutricheckai.entity.UserProfile;
import com.atharva.nutricheckai.repository.UserProfileRepository;
import com.atharva.nutricheckai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    private final UserProfileRepository userProfileRepository;

    public UserProfileResponse createProfile(UserProfileRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userProfileRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Profile already exists");
        }

        UserProfile profile = new UserProfile();

        profile.setUser(user);
        profile.setAge(request.getAge());
        profile.setGender(request.getGender());
        profile.setSkinType(request.getSkinType());
        profile.setDietPreference(request.getDietPreference());
        profile.setAllergies(request.getAllergies());
        profile.setMedicalConditions(request.getMedicalConditions());
        profile.setPregnant(request.getPregnant());

        UserProfile savedProfile = userProfileRepository.save(profile);

        return mapToResponse(savedProfile);
    }
    private UserProfileResponse mapToResponse(UserProfile profile) {

        UserProfileResponse response = new UserProfileResponse();

        response.setId(profile.getId());
        response.setAge(profile.getAge());
        response.setGender(profile.getGender());
        response.setSkinType(profile.getSkinType());
        response.setDietPreference(profile.getDietPreference());
        response.setAllergies(profile.getAllergies());
        response.setMedicalConditions(profile.getMedicalConditions());
        response.setPregnant(profile.getPregnant());

        return response;
    }
    public UserProfileResponse getMyProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        return mapToResponse(profile);
    }
    public UserProfileResponse updateProfile(UserProfileRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        profile.setAge(request.getAge());
        profile.setGender(request.getGender());
        profile.setSkinType(request.getSkinType());
        profile.setDietPreference(request.getDietPreference());
        profile.setAllergies(request.getAllergies());
        profile.setMedicalConditions(request.getMedicalConditions());
        profile.setPregnant(request.getPregnant());

        UserProfile updatedProfile = userProfileRepository.save(profile);

        return mapToResponse(updatedProfile);
    }
    public void deleteProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        userProfileRepository.delete(profile);
    }

}