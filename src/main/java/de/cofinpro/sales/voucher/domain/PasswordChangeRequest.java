package de.cofinpro.sales.voucher.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {

    @JsonProperty("new_password")
    @NotNull
    private String password;

    @AssertFalse(message = "The password is in the hacker's database!")
    @JsonIgnore
    public boolean isBreached() {
        return this.password != null && this.password.matches("Password.*");
    }

    @AssertTrue(message = "Password length must be 12 chars minimum!")
    @JsonIgnore
    public boolean hasValideLength() {
        return this.password != null && this.password.length() >= 12;
    }
}
