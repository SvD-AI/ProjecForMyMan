package com.glovodelivery.project.service;

import com.glovodelivery.project.dto.request.AuthenticationRequest;
import com.glovodelivery.project.dto.request.UserRegistrationRequest;
import com.glovodelivery.project.dto.response.AuthenticationResponse;

public interface AuthenticationService {
  AuthenticationResponse login(AuthenticationRequest request);
  AuthenticationResponse register(UserRegistrationRequest request);

}
