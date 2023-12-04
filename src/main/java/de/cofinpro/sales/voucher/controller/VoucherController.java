package de.cofinpro.sales.voucher.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
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

    @GetMapping("/voucher")
    public String hello() {
        return "Hello, world!";
    }
}
