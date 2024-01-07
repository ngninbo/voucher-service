package de.cofinpro.sales.voucher.service.impl;

import de.cofinpro.sales.voucher.domain.*;
import de.cofinpro.sales.voucher.exception.PasswordUpdateException;
import de.cofinpro.sales.voucher.exception.RoleUpdateException;
import de.cofinpro.sales.voucher.exception.UserAlreadyExistException;
import de.cofinpro.sales.voucher.exception.UserNotFoundException;
import de.cofinpro.sales.voucher.model.Role;
import de.cofinpro.sales.voucher.model.User;
import de.cofinpro.sales.voucher.persistence.UserRepository;
import de.cofinpro.sales.voucher.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder encoder,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.userMapper = userMapper;
    }


    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public UserDto create(User user) {
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

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> findAll() {
        return userMapper.toList(userRepository.findAll());
    }

    @Override
    public UserDto update(RoleChangeRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        if (user.isAdmin()) {
            throw new RoleUpdateException("User can have only one role");
        }

        if (user.getRole().name().equals(request.getRole())) {
            throw new UserAlreadyExistException("User already has the role");
        }

        user.setRole(Role.valueOf(request.getRole()));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDeletionResponse remove(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User by email %s not found", email)));
        userRepository.delete(user);
        return UserDeletionResponse.builder().status(UserDeletionResponse.DEFAULT_STATUS).email(user.getEmail()).build();
    }

    @Override
    @Transactional
    public User increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        user.setFailedAttempt(newFailAttempts);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void lock(User user) {
        user.setAccountNonLocked(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetFailedAttempts(String email) {
        User user = findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getFailedAttempt() > 0) {
            userRepository.updateFailedAttempts(0, email);
        }
    }


    @Override
    public PasswordChangeResponse changePass(PasswordChangeRequest request, UserDetails userDetails) {

        final String newPassword = request.getPassword();

        if (encoder.matches(newPassword, userDetails.getPassword())) {
            throw new PasswordUpdateException("The password must be different!");
        }

        return updateUserBy(userDetails.getUsername(), newPassword);
    }

    @Override
    public PasswordChangeResponse updateUserBy(String email, String newPassword) {
        User user = findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(encoder.encode(newPassword));

        user = userRepository.save(user);

        return PasswordChangeResponse.of(user.getEmail());
    }
}
