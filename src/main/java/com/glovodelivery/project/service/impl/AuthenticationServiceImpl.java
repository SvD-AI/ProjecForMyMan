package com.glovodelivery.project.service.impl;

import com.glovodelivery.project.config.properties.RsaKeyProperties;
import com.glovodelivery.project.dto.request.AuthenticationRequest;
import com.glovodelivery.project.dto.response.AuthenticationResponse;
import com.glovodelivery.project.service.AuthenticationService;
import com.glovodelivery.project.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RsaKeyProperties rsaKeyProperties;

    @Override
    @Transactional
    public AuthenticationResponse login(AuthenticationRequest request) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
        ));

        return new AuthenticationResponse(
                jwtService.generateAccessToken(authentication),
                "bearer",
                rsaKeyProperties.expirationInSeconds()
        );
    }
}
