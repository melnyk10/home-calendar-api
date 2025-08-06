package com.meln.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserService {
    private final UserRepo userRepo;

    protected User getByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
