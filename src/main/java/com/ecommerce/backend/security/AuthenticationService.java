package com.ecommerce.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationService {

    public String getLoggedUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated() ){
            return authentication.getName();
        }
        return null;
    }

}
