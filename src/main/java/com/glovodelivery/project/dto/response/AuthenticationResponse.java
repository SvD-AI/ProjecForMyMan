package com.glovodelivery.project.dto.response;

public record AuthenticationResponse(
        String accessToken,

        String tokenType,

        Integer expiresIn
)
{
  public String getToken() {
    return accessToken;
  }
}
