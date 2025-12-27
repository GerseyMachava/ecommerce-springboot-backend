package com.ecommerce.backend.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserRole {

    ADMIN("admin"),
    USER("user"),
    CUSTOMER("customer");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @JsonCreator
    public static UserRole fromString(String value) {
        if (value == null) return null;
        for (UserRole r : values()) {
            if (r.name().equalsIgnoreCase(value) || r.role.equalsIgnoreCase(value)) {
                return r;
            }
        }
        // accept common misspelling
        if ("custumer".equalsIgnoreCase(value)) return CUSTOMER;
        throw new IllegalArgumentException("Invalid role: " + value);
    }

}
