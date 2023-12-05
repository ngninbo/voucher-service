package de.cofinpro.sales.voucher.security;

import de.cofinpro.sales.voucher.model.User;
import de.cofinpro.sales.voucher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureEventListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final UserService userService;

    @Autowired
    public AuthenticationFailureEventListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {

        String username = (String) event.getAuthentication().getPrincipal();
        var user = userService.findByEmail(username);

        if (user.isPresent()) {

            User currentUser = user.get();

            if (currentUser.isAdmin()) {
                return;
            }

            if (currentUser.isEnabled() && currentUser.isAccountNonLocked()) {
                currentUser = userService.increaseFailedAttempts(currentUser);
            }

            if (currentUser.getFailedAttempt() >= UserService.MAX_FAILED_ATTEMPTS) {
                userService.lock(currentUser);
            }
        }
    }
}
