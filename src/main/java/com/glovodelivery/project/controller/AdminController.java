package com.glovodelivery.project.controller;

import com.glovodelivery.project.dto.request.AdminCreateUserRequest;
import com.glovodelivery.project.dto.request.AdminUpdateUserRequest;
import com.glovodelivery.project.dto.response.UserResponse;
import com.glovodelivery.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

  private final UserService userService;

  @PostMapping("/create-user")
  public ResponseEntity<UserResponse> createUserByAdmin(@RequestBody @Valid AdminCreateUserRequest request) {
    UserResponse createdUser = userService.createUserByAdmin(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @GetMapping("/users")
  public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
    return ResponseEntity.ok(userService.getAllUsers(pageable));
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUser(id));
  }

  @PutMapping("/users/{id}")
  public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                 @RequestBody @Valid AdminUpdateUserRequest request) {
    UserResponse updatedUser = userService.updateUserByAdmin(id, request);
    return ResponseEntity.ok(updatedUser);
  }

  @DeleteMapping("/users/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

}

