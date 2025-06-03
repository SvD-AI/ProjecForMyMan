package com.glovodelivery.project.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserRegistrationRequest(
        @NotNull(message = "First name is required")
        String firstName,

        @NotNull(message = "Last name is required")
        String lastName,

        @Email
        @NotNull(message = "Email is required")
        String email,

        @NotNull(message = "Password is required")
        String password,

        @NotNull(message = "Phone number is required")
        String phoneNumber,

        String address

        )
{

}
