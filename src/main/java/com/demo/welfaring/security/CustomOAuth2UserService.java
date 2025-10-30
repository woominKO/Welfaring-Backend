package com.demo.welfaring.security;

import com.demo.welfaring.domain.User;
import com.demo.welfaring.domain.AuthProvider;
import com.demo.welfaring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final UserRepository userRepository;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            log.error("OAuth2 사용자 처리 중 오류 발생", ex);
            throw new OAuth2AuthenticationException("OAuth2 사용자 처리 실패");
        }
    }
    
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());
        
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());
        
        if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("이메일을 찾을 수 없습니다.");
        }
        
        User user = userRepository.findByEmail(userInfo.getEmail())
                .map(existingUser -> updateExistingUser(existingUser, userInfo, provider))
                .orElseGet(() -> registerNewUser(userInfo, provider));
        
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }
    
    private User updateExistingUser(User existingUser, OAuth2UserInfo userInfo, AuthProvider provider) {
        existingUser.setName(userInfo.getName());
        existingUser.setPicture(userInfo.getImageUrl());
        existingUser.setLastLoginAt(LocalDateTime.now());
        return userRepository.save(existingUser);
    }
    
    private User registerNewUser(OAuth2UserInfo userInfo, AuthProvider provider) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .picture(userInfo.getImageUrl())
                .provider(provider)
                .providerId(userInfo.getId())
                .build();
        
        return userRepository.save(user);
    }
}
