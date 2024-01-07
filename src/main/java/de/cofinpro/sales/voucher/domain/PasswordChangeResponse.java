package de.cofinpro.sales.voucher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordChangeResponse {

    public static final String DEFAULT_STATUS = "The password has been updated successfully";

    private String email;
    private String status;

    public static PasswordChangeResponse of(String email) {
        return new PasswordChangeResponse(email, DEFAULT_STATUS);
    }
}
