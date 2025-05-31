package com.glovodelivery.project.controller;

import com.glovodelivery.project.dto.CustomUserDetails;
import com.glovodelivery.project.dto.request.AuthenticationRequest;
import com.glovodelivery.project.dto.request.UserRegistrationRequest;
import com.glovodelivery.project.dto.response.AuthenticationResponse;
import com.glovodelivery.project.dto.response.UserResponse;
import com.glovodelivery.project.service.AuthenticationService;
import com.glovodelivery.project.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;

import java.time.Duration;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

  private final AuthenticationService authenticationService;
  private final UserService userService;


  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(
    @RequestBody @Valid AuthenticationRequest request,
    HttpServletRequest httpRequest,
    HttpServletResponse httpResponse
  ) {
    AuthenticationResponse response = authenticationService.login(request);

    // Створити сесію
    String sessionId = httpRequest.getSession().getId();

    // Створюємо HttpOnly cookie з JWT
    ResponseCookie cookie = ResponseCookie.from("jwt", response.getToken())
      .httpOnly(true)
      .secure(false) // використовуйте true на HTTPS
      .path("/")
      .maxAge(Duration.ofHours(1))
      .sameSite("Lax") // або Lax
      .build();

    // Додаємо cookie до відповіді
    httpResponse.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    log.info("Session ID: {}", sessionId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
    @RequestBody @Valid UserRegistrationRequest request,
    HttpServletRequest httpRequest,
    HttpServletResponse httpResponse
  ) {
    AuthenticationResponse response = authenticationService.register(request);

    ResponseCookie cookie = ResponseCookie.from("jwt", response.getToken())
      .httpOnly(true)
      .secure(false)
      .path("/")
      .maxAge(Duration.ofHours(1))
      .sameSite("Lax")
      .build();

    httpResponse.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    log.info("Registered session ID: {}", httpRequest.getSession().getId());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
    Jwt jwt = (Jwt) authentication.getPrincipal();
    String email = jwt.getClaimAsString("sub"); // email/username з JWT
    UserResponse userResponse = userService.getUserResponseByEmail(email);
    return ResponseEntity.ok(userResponse);
  }

  @GetMapping
  public Map<String, String> getSessionId(HttpServletRequest request) {
    return Map.of("sessionId", request.getSession().getId());
  }
}
