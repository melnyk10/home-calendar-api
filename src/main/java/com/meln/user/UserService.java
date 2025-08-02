package com.meln.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class UserService {
    @Inject
    UserRepo userRepo;

    protected List<User> getAll() {
        return userRepo.listAll();
    }
}
