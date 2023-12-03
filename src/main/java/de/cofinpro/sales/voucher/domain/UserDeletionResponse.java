package de.cofinpro.sales.voucher.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDeletionResponse {

    private String username;
    private String status;

    public static final String DEFAULT_STATUS = "Deleted successfully!";
}
