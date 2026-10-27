package com.langly.langly_backend.config;

import com.langly.langly_backend.dto.AuthResponse;
import com.langly.langly_backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final String APP_REDIRECT_URI = "ourilang://auth";

    private final AuthService authService;

    public OAuth2SuccessHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        AuthResponse authResponse = authService.oauth2Login(email);

        String redirectUrl = APP_REDIRECT_URI
                + "?access=" + URLEncoder.encode(authResponse.getAccessToken(), StandardCharsets.UTF_8)
                + "&refresh=" + URLEncoder.encode(authResponse.getRefreshToken(), StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }
}