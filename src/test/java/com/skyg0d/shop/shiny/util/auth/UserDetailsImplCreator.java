package com.skyg0d.shop.shiny.util.auth;

import com.skyg0d.shop.shiny.util.user.UserCreator;
import com.skyg0d.shop.shiny.security.service.UserDetailsImpl;

public class UserDetailsImplCreator {

    public static UserDetailsImpl createUserDetails() {
        return UserDetailsImpl.build(UserCreator.createUser());
    }

}
