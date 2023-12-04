package de.cofinpro.sales.voucher.domain;

import de.cofinpro.sales.voucher.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String name;
    private String lastname;
    private String email;
    private Role role;
}
