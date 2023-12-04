package de.cofinpro.sales.voucher.model;

public enum Role {

    ADMIN,
    SALE,
    SUPPORT;

    public String getName() {
        return "ROLE_".concat(name());
    }
}
