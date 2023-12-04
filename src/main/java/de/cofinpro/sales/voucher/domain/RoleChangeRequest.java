package de.cofinpro.sales.voucher.domain;

import de.cofinpro.sales.voucher.model.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RoleChangeRequest {

    @NotEmpty(message = "Email must not be empty")
    @Pattern(regexp = ".*@company\\.com", message = "Email from given domain not allowed")
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @AssertTrue(message = "Role must be SUPPORT or SALE")
    public boolean isValidRole() {
        return isNotAdminRole().test(role);
    }

    private Predicate<Role> isNotAdminRole() {
        return role -> Role.SALE.equals(role) || Role.SUPPORT.equals(role);
    }
}
