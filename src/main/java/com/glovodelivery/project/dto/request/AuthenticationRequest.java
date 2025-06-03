package com.glovodelivery.project.dto.request;

import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest(
        @NotNull(message = "Email is required")
        String email,

        @NotNull(message = "Password is required")
        String password
)
{
   
}
