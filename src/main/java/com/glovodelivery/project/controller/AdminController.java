package com.glovodelivery.project.controller;

import com.glovodelivery.project.dto.request.AdminCreateUserRequest;
import com.glovodelivery.project.dto.request.AdminUpdateUserRequest;
import com.glovodelivery.project.dto.response.UserResponse;
import com.glovodelivery.project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

  private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
  private final UserService userService;

  @PostMapping("/create-user")
  @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
  public ResponseEntity<UserResponse> createUserByAdmin(@RequestBody @Valid AdminCreateUserRequest request) {
    logger.debug("Received request to create user: {}", request);
    UserResponse createdUser = userService.createUserByAdmin(request);
    logger.info("User created successfully with ID: {}", createdUser.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @GetMapping("/users")
  public ResponseEntity<Page<UserResponse>> getAllUsers(@RequestParam(required = false) String role, Pageable pageable) {
    logger.debug("Fetching users. Role filter: {}", role);
    Page<UserResponse> users;
    if (role != null) {
      users = userService.getUsersByRole(role, pageable);
      logger.info("Fetched users by role '{}': {} users", role, users.getTotalElements());
    } else {
      users = userService.getAllUsers(pageable);
      logger.info("Fetched all users: {} users", users.getTotalElements());
    }
    return ResponseEntity.ok(users);
  }

  @GetMapping("/users/{id}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
    logger.debug("Fetching user by ID: {}", id);
    UserResponse user = userService.getUser(id);
    logger.info("Fetched user: {}", user.getId());
    return ResponseEntity.ok(user);
  }

  @PutMapping("/users/{id}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
      @RequestBody @Valid AdminUpdateUserRequest request) {
    logger.debug("Updating user ID {}: {}", id, request);
    UserResponse updatedUser = userService.updateUserByAdmin(id, request);
    logger.info("Updated user ID: {}", updatedUser.getId());
    return ResponseEntity.ok(updatedUser);
  }

  @DeleteMapping("/users/{id}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    logger.debug("Deleting user with ID: {}", id);
    userService.deleteUser(id);
    logger.info("Deleted user with ID: {}", id);
    return ResponseEntity.noContent().build();
  }

}
