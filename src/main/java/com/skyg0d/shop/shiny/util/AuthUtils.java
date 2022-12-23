package com.skyg0d.shop.shiny.util;

import com.skyg0d.shop.shiny.exception.BadRequestException;
import com.skyg0d.shop.shiny.model.ERole;
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

    public boolean isOwnerOrStaff(String entityEmail) {
        UserDetailsImpl userDetails = getUserDetails();

        boolean isOwner = userDetails.getEmail().equals(entityEmail);

        boolean isStaff = userDetails.getAuthorities().stream().anyMatch((authority) ->
                authority.getAuthority().equals(ERole.ROLE_ADMIN.name()) || authority.getAuthority().equals(ERole.ROLE_MODERATOR.name())
        );

        return isOwner || isStaff;
    }

    public boolean isOwnerOrAdmin(String entityEmail) {
        UserDetailsImpl userDetails = getUserDetails();

        boolean isOwner = userDetails.getEmail().equals(entityEmail);

        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch((authority) ->
                authority.getAuthority().equals(ERole.ROLE_ADMIN.name())
        );

        return isOwner || isAdmin;
    }

}
