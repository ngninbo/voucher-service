package de.cofinpro.sales.voucher.service;

import de.cofinpro.sales.voucher.domain.RoleChangeRequest;
import de.cofinpro.sales.voucher.domain.UserDeletionResponse;
import de.cofinpro.sales.voucher.domain.UserDto;
import de.cofinpro.sales.voucher.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    int MAX_FAILED_ATTEMPTS = 5;

    Optional<User> findByEmail(String email);

    UserDto create(User user);

    List<UserDto> findAll();

    UserDto update(RoleChangeRequest request);

    UserDeletionResponse remove(String email);

    User increaseFailedAttempts(User currentUser);

    void lock(User user);

    void resetFailedAttempts(String email);

    default void handleFailedAttempt(String email) {
        findByEmail(email).ifPresent(this::handleFailedAttempt);
    }

    default void handleFailedAttempt(User user) {

        if (user.isAdmin()) {
            return;
        }

        if (user.isEnabled() && user.isAccountNonLocked()) {
            user = increaseFailedAttempts(user);
        }

        if (user.getFailedAttempt() >= UserService.MAX_FAILED_ATTEMPTS) {
            lock(user);
        }
    }
}
