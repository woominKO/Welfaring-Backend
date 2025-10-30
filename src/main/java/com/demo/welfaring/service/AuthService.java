package com.demo.welfaring.service;

import com.demo.welfaring.domain.RefreshToken;
import com.demo.welfaring.domain.User;
import com.demo.welfaring.dto.AuthResponseDTO;
import com.demo.welfaring.dto.UserInfoDTO;
import com.demo.welfaring.repository.RefreshTokenRepository;
import com.demo.welfaring.repository.UserRepository;
import com.demo.welfaring.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Transactional
    public AuthResponseDTO createTokenResponse(User user) {
        String accessToken = jwtTokenProvider.createToken(user.getEmail(), user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        
        // 리프레시 토큰 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .userId(user.getId())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        
        refreshTokenRepository.save(refreshTokenEntity);
        
        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400) // 24시간
                .build();
    }
    
    @Transactional
    public AuthResponseDTO refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰");
        }
        
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("리프레시 토큰을 찾을 수 없습니다"));
        
        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new RuntimeException("리프레시 토큰이 만료되었습니다");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        // 새로운 토큰 생성
        String newAccessToken = jwtTokenProvider.createToken(user.getEmail(), user.getId());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        
        // 기존 리프레시 토큰 삭제
        refreshTokenRepository.delete(storedToken);
        
        // 새로운 리프레시 토큰 저장
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .token(newRefreshToken)
                .userId(user.getId())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        
        refreshTokenRepository.save(newRefreshTokenEntity);
        
        return AuthResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(86400)
                .build();
    }
    
    @Transactional
    public void logout(String accessToken) {
        Long userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        refreshTokenRepository.deleteByUserId(userId);
    }
    
    public UserInfoDTO getCurrentUser(String accessToken) {
        Long userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        return UserInfoDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .picture(user.getPicture())
                .provider(user.getProvider().name())
                .build();
    }
}
