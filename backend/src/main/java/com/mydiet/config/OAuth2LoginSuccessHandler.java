package com.mydiet.config;

import com.mydiet.service.OAuth2UserPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        try {
            OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
            log.info("Login successful for user: {}", principal.getEmail());
            
            request.getSession().setAttribute("userId", principal.getUserId());
            request.getSession().setAttribute("userEmail", principal.getEmail());
            request.getSession().setAttribute("userNickname", principal.getNickname());
            
            String redirectUrl = request.getContextPath() + "/dashboard.html";
            log.info("Redirecting to: {}", redirectUrl);
            
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            
        } catch (Exception e) {
            log.error("Login success handler error: ", e);
            response.sendRedirect("/auth.html?error=login_failed");
        }
    }
}