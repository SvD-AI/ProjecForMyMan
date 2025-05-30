package com.glovodelivery.project.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleName {
    ROLE_USER ("role_user"), ROLE_ADMIN ("role_admin");

    private final String value;

    RoleName(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
