package de.cofinpro.sales.voucher.model;

public enum Role {

    ROLE_SALE("SALE"),
    ROLE_ADMINISTRATOR("ADMINISTRATOR"),
    ROLE_SUPPORT("SUPPORT");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
