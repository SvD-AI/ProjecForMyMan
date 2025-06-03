package com.glovodelivery.project.dto.request;

import com.glovodelivery.project.enums.RoleName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

import java.util.List;

public record AdminUpdateUserRequest(
  @NotNull(message = "First name is required") String firstName,
  @NotNull(message = "Last name is required") String lastName,
  @Email(message = "Email should be valid") String username,
  String phoneNumber,
  List<RoleName> roles
) {}
