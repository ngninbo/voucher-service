package de.cofinpro.sales.voucher.service.impl;

import de.cofinpro.sales.voucher.domain.RoleChangeRequest;
import de.cofinpro.sales.voucher.domain.UserDeletionResponse;
import de.cofinpro.sales.voucher.exception.RoleUpdateException;
import de.cofinpro.sales.voucher.exception.UserAlreadyExistException;
import de.cofinpro.sales.voucher.exception.UserNotFoundException;
import de.cofinpro.sales.voucher.model.Role;
import de.cofinpro.sales.voucher.model.User;
import de.cofinpro.sales.voucher.persistence.UserRepository;
import de.cofinpro.sales.voucher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }


    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public User create(User user) {
        var count = userRepository.count();

        user.setPassword(encoder.encode(user.getPassword()));

        if (count <= 0) {
            user.setAccountNonLocked(true);
            user.setRole(Role.ADMIN);
        } else {
            var userFromRepo = userRepository.findByEmailIgnoreCase(user.getEmail());

            if (userFromRepo.isPresent()) {
                throw new UserAlreadyExistException("User already exist!");
            }

            user.setRole(Role.SUPPORT);

        }

        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(RoleChangeRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        if (user.isAdmin()) {
            throw new RoleUpdateException("User can have only one role");
        }

        if (user.getRole().equals(request.getRole())) {
            throw new UserAlreadyExistException("User already has the role");
        }

        user.setRole(request.getRole());
        return userRepository.save(user);
    }

    @Override
    public UserDeletionResponse remove(String email) {
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.delete(user);
        return UserDeletionResponse.builder().status(UserDeletionResponse.DEFAULT_STATUS).username(user.getName()).build();
    }
}
