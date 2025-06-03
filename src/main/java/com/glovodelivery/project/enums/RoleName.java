package com.glovodelivery.project.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleName {
  ROLE_USER("role_user"),
  ROLE_ADMIN("role_admin"),
  ROLE_MANAGER("role_manager"),
  ROLE_COURIER("role_courier"),
  ROLE_CUSTOMER("role_customer"); 

  private final String value;

  RoleName(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
