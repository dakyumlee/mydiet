package com.mydiet.service;

import com.mydiet.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class OAuth2UserPrincipal implements OAuth2User {
    
    private final OAuth2User oauth2User;
    private final User user;
    
    public OAuth2UserPrincipal(OAuth2User oauth2User, User user) {
        this.oauth2User = oauth2User;
        this.user = user;
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }
    
    @Override
    public String getName() {
        return oauth2User.getName();
    }
    
    public User getUser() {
        return user;
    }
    
    public Long getUserId() {
        return user.getId();
    }
}