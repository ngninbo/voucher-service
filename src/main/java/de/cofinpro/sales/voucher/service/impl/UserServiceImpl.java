package de.cofinpro.sales.voucher.service.impl;

import de.cofinpro.sales.voucher.domain.RoleChangeRequest;
import de.cofinpro.sales.voucher.domain.UserDeletionResponse;
import de.cofinpro.sales.voucher.domain.UserDto;
import de.cofinpro.sales.voucher.domain.UserMapper;
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
    public User increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        user.setFailedAttempt(newFailAttempts);
        return userRepository.save(user);
    }

    @Override
    public void lock(User user) {
        user.setAccountNonLocked(false);
        userRepository.save(user);
    }

    @Override
    public void resetFailedAttempts(String email) {
        User user = findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getFailedAttempt() > 0) {
            userRepository.updateFailedAttempts(0, email);
        }
    }
}
