package com.skyg0d.shop.shiny.util;

import com.skyg0d.shop.shiny.util.auth.UserDetailsImplCreator;
import com.skyg0d.shop.shiny.security.service.UserDetailsImpl;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;

public class MockUtils {

    public static void mockSecurityContextHolder(boolean nullPrincipal) {
        Authentication auth = Mockito.mock(Authentication.class);

        UserDetailsImpl userDetails = nullPrincipal ? null : UserDetailsImplCreator.createUserDetails();

        Mockito
                .when(auth.getPrincipal())
                .thenReturn(userDetails);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        Mockito
                .when(securityContext.getAuthentication())
                .thenReturn(auth);

        SecurityContextHolder.setContext(securityContext);
    }

    public static void mockSecurityContextHolder() {
        mockSecurityContextHolder(false);
    }

    public static HttpServletRequest mockUserMachineInfo() {
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);

        Mockito
                .when(httpServletRequest.getHeader(""))
                .thenReturn("");

        return httpServletRequest;
    }

}
