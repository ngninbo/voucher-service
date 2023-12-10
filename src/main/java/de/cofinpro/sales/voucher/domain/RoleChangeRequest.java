package de.cofinpro.sales.voucher.domain;

import de.cofinpro.sales.voucher.model.Role;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
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

    private String role;

    @AssertTrue(message = "Role must be SUPPORT or SALE")
    public boolean isValidRole() {
        return isNotAdminRole().test(role);
    }

    private Predicate<String> isNotAdminRole() {
        return role -> Role.SALE.name().equals(role) || Role.SUPPORT.name().equals(role);
    }
}
