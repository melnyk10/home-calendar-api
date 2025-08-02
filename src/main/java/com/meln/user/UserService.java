package com.meln.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserService {
    private final UserRepo userRepo;

    protected List<User> getAll() {
        return userRepo.listAll();
    }

    protected User getByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
