package com.meln.user;

import com.meln.common.error.CustomException;
import com.meln.common.error.ErrorMessage;
import com.meln.common.user.UserClient;
import com.meln.common.user.UserDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserService implements UserClient {
    private final UserRepo userRepo;

    protected UserMe me(String email) {
        if (email == null || email.isBlank()) {
            throw new CustomException(Response.Status.UNAUTHORIZED, ErrorMessage.Auth.Code.USERNAME_MISSING);
        }

        try {
            //todo: move to filter
            User user = userRepo.findByEmail(email);
            if (user == null) {
                throw new CustomException(Response.Status.NOT_FOUND,
                        ErrorMessage.User.Code.USER_NOT_FOUND,
                        ErrorMessage.User.Message.USER_NOT_FOUND(email));
            }
            return UserConverter.toUserMe(user);
        } catch (Exception e) {
            throw new CustomException(Response.Status.UNAUTHORIZED,
                    ErrorMessage.Auth.Code.USER_NOT_FOUND,
                    ErrorMessage.Auth.Message.USER_NOT_FOUND(email));
        }
    }

    @Override
    public UserDto getByEmail(String email) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new CustomException(Response.Status.NOT_FOUND,
                    ErrorMessage.User.Code.USER_NOT_FOUND,
                    ErrorMessage.User.Message.USER_NOT_FOUND(email));
        }
        return UserConverter.toUserDto(user);
    }

}
