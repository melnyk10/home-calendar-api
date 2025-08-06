package com.meln.user;

import java.util.Optional;

public record UserMe(String firstName, String lastName, String email) {
    public static UserMe from(User entity) {
        return Optional.ofNullable(entity)
                .map(user -> new UserMe(entity.getFirstName(), entity.getLastName(), entity.getEmail()))
                .orElse(null);
    }
}
