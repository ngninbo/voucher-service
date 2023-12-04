package de.cofinpro.sales.voucher.domain;

import de.cofinpro.sales.voucher.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public List<UserDto> toList(List<User> users) {
        return users.isEmpty() ? List.of() : users.stream().map(this::toDto).collect(Collectors.toList());
    }
}
