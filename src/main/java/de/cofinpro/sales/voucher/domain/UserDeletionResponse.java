package de.cofinpro.sales.voucher.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDeletionResponse {

    @JsonProperty("user")
    private String email;
    private String status;

    public static final String DEFAULT_STATUS = "Deleted successfully!";
}
