package com.meln.user;

import com.meln.common.error.CustomException;
import com.meln.common.error.ErrorMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserService {
    private final UserRepo userRepo;

    protected UserMe me(String email) {
        if (email == null || email.isBlank()) {
            throw new CustomException(Response.Status.UNAUTHORIZED, ErrorMessage.Auth.Code.USERNAME_MISSING);
        }

        User user = getByEmail(email);
        if (user == null) {
            throw new CustomException(Response.Status.UNAUTHORIZED,
                    ErrorMessage.Auth.Code.USER_NOT_FOUND,
                    ErrorMessage.Auth.Message.USER_NOT_FOUND(email));
        }

        return UserMe.from(user);
    }

    protected User getByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
