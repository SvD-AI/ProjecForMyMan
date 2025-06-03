package com.glovodelivery.project.service.impl;

import com.glovodelivery.project.config.properties.RsaKeyProperties;
import com.glovodelivery.project.dto.request.AuthenticationRequest;
import com.glovodelivery.project.dto.response.AuthenticationResponse;
import com.glovodelivery.project.dto.request.UserRegistrationRequest;

import com.glovodelivery.project.service.AuthenticationService;
import com.glovodelivery.project.service.JwtService;
import com.glovodelivery.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RsaKeyProperties rsaKeyProperties;
  private final UserService userService;


  @Override
  @Transactional
  public AuthenticationResponse login(AuthenticationRequest request) {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.email(),
        request.password()
      )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String token = jwtService.generateAccessToken(authentication);

    return new AuthenticationResponse(
      token,
      "Bearer",
      rsaKeyProperties.expirationInSeconds()
    );
  }

  @Override
  @Transactional
  public AuthenticationResponse register(UserRegistrationRequest request) {
    userService.registerUser(request);

    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(request.email(), request.password())
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String accessToken = jwtService.generateAccessToken(authentication);

    return new AuthenticationResponse(
      accessToken,
      "Bearer",
      rsaKeyProperties.expirationInSeconds()
    );
  }
}
