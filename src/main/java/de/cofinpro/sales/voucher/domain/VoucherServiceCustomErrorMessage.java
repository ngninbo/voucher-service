package de.cofinpro.sales.voucher.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class VoucherServiceCustomErrorMessage {

    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
