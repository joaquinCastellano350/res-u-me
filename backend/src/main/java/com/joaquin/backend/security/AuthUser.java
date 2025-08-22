package com.joaquin.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class AuthUser {
    private AuthUser(){}

    public static UUID id(){
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("unauthenticated");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof UUID u){
            return u;
        }
        return UUID.fromString(principal.toString());
    }
}
