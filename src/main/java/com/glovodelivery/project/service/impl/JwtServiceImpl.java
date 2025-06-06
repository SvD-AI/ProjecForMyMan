package com.glovodelivery.project.service.impl;

import com.glovodelivery.project.config.properties.RsaKeyProperties;
import com.glovodelivery.project.dto.CustomUserDetails;
import com.glovodelivery.project.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

  private final RsaKeyProperties rsaKeyProperties;
  private final JwtEncoder encoder;

  @Override
  public String generateAccessToken(Authentication authentication) {
    Instant now = Instant.now();

    String authorities = authentication.getAuthorities().stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.joining(" "));

    CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

    JwtClaimsSet claims = JwtClaimsSet.builder()
      .issuer("self")
      .issuedAt(now)
      .expiresAt(now.plus(rsaKeyProperties.expirationInSeconds(), ChronoUnit.SECONDS))
      .subject(authentication.getName())
      .claim("id", user.getId())
      .claim("firstName", user.getFirstName())
      .claim("lastName", user.getLastName())
      .claim("authorities", authorities)
      .build();

    return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }
}
