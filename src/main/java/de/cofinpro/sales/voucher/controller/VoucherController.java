package de.cofinpro.sales.voucher.controller;

import de.cofinpro.sales.voucher.service.VoucherService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/voucher-service", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "Voucher endpoints")
public class VoucherController {

    private final VoucherService voucherService;

    @Autowired
    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping("/voucher")
    public String hello() {
        return voucherService.getMessage();
    }
}
