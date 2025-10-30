package com.demo.welfaring.security;

import com.demo.welfaring.dto.AuthResponseDTO;
import com.demo.welfaring.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final AuthService authService;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            AuthResponseDTO authResponse = authService.createTokenResponse(
                com.demo.welfaring.domain.User.builder()
                    .id(userPrincipal.getId())
                    .email(userPrincipal.getEmail())
                    .name(userPrincipal.getName())
                    .picture(userPrincipal.getPicture())
                    .build()
            );
            
            // 토큰을 URL 파라미터로 리다이렉트
            String redirectUrl = String.format("/oauth-test.html?access_token=%s&refresh_token=%s", 
                authResponse.getAccessToken(), authResponse.getRefreshToken());
            
            response.sendRedirect(redirectUrl);
            
            log.info("OAuth2 로그인 성공: {}", userPrincipal.getEmail());
            
        } catch (Exception e) {
            log.error("OAuth2 로그인 후 처리 중 오류 발생", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"로그인 처리 중 오류가 발생했습니다.\"}");
        }
    }
}
