package com.skyg0d.shop.shiny.util;

import com.skyg0d.shop.shiny.exception.BadRequestException;
import com.skyg0d.shop.shiny.security.service.UserDetailsImpl;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    public UserDetailsImpl getUserDetails() throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (authentication instanceof AnonymousAuthenticationToken || principal == null) {
            throw new BadRequestException("You are not logged in.");
        }

        return (UserDetailsImpl) principal;
    }

}
