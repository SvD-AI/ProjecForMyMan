package com.glovodelivery.project.dto.request;

import com.glovodelivery.project.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AdminCreateUserRequest(
  @NotNull(message = "First name is required") String firstName,
  @NotNull(message = "Last name is required") String lastName,
  @NotNull(message = "Email is required") @Email String username,
  @NotNull(message = "Password is required") String password,
  @NotNull(message = "Phone number is required") String phoneNumber,
  @NotNull(message = "Role is required") RoleName role
) {}
