package com.glovodelivery.project.service.impl;

import com.glovodelivery.project.dto.request.AdminCreateUserRequest;
import com.glovodelivery.project.dto.request.AdminUpdateUserRequest;
import com.glovodelivery.project.dto.request.UserRegistrationRequest;
import com.glovodelivery.project.dto.response.UserResponse;
import com.glovodelivery.project.entity.Role;
import com.glovodelivery.project.entity.User;
import com.glovodelivery.project.enums.RoleName;
import com.glovodelivery.project.mapper.Mapper;
import com.glovodelivery.project.service.UserService;
import com.glovodelivery.project.repository.UserRepository;
import com.glovodelivery.project.service.RoleService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final RoleService roleService;

  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public UserResponse registerUser(UserRegistrationRequest userRegistrationRequest){

    if(userRepository.existsByUsername(userRegistrationRequest.email()))
        throw new EntityExistsException(String.format("Email %s already exists", userRegistrationRequest.email()));

    Role role = roleService.getRoleByName(RoleName.ROLE_CUSTOMER);

    if (role.getName() == RoleName.ROLE_CUSTOMER &&
      (userRegistrationRequest.address() == null || userRegistrationRequest.address().isBlank())) {
      throw new IllegalArgumentException("Address is required for clients.");
    }

    User user = User.builder()
              .firstName(userRegistrationRequest.firstName())
              .lastName(userRegistrationRequest.lastName())
              .username(userRegistrationRequest.email())
              .password(passwordEncoder.encode(userRegistrationRequest.password()))
              .phoneNumber(userRegistrationRequest.phoneNumber())
              .address(userRegistrationRequest.address())
              .roles(Collections.singletonList(role))
              .accountNonExpired(true)
              .accountNonLocked(true)
              .credentialsNonExpired(true)
              .enabled(true)
              .build();

      User savedUser = userRepository.save(user);

      return Mapper.toUserResponse(savedUser);
  }

  @Override
  @Transactional
  public UserResponse createUserByAdmin(AdminCreateUserRequest request) {

    if (userRepository.existsByUsername(request.username()))
      throw new EntityExistsException("User already exists with email: " + request.username());

    Role role = roleService.getRoleByName(request.role());

    User user = User.builder()
      .firstName(request.firstName())
      .lastName(request.lastName())
      .username(request.username())
      .password(passwordEncoder.encode(request.password()))
      .phoneNumber(request.phoneNumber())
      .roles(List.of(role))
      .accountNonExpired(true)
      .accountNonLocked(true)
      .credentialsNonExpired(true)
      .enabled(true)
      .build();

    User savedUser = userRepository.save(user);

    return Mapper.toUserResponse(savedUser);
  }


  @Override
  public Page<UserResponse> getAllUsers(Pageable pageable){
      User currentUser = getCurrentUser();
      if (hasRole(currentUser, com.glovodelivery.project.enums.RoleName.ROLE_MANAGER)) {
          return userRepository.findAllCouriers().stream()
                  .map(Mapper::toUserResponse)
                  .collect(java.util.stream.Collectors.collectingAndThen(
                          java.util.stream.Collectors.toList(),
                          list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                  ));
      }

      return userRepository.findAll(pageable).map(Mapper::toUserResponse);
  }

  private User getCurrentUser() {
      org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
      return userRepository.findByUsername(auth.getName())
              .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));
  }

  private boolean hasRole(User user, com.glovodelivery.project.enums.RoleName roleName) {
      return user.getRoles().stream().anyMatch(r -> r.getName().equals(roleName.name()));
  }

  @Override
  public UserResponse getUser(Long userId) {

      return userRepository.findById(userId).map(Mapper::toUserResponse)
              .orElseThrow(() -> new EntityNotFoundException("User not found"));
  }

  @Override
  public UserResponse getUserResponseByEmail(String email) {
    User user = userRepository.findByUsername(email)
      .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    return Mapper.toUserResponse(user);
  }

  @Override
  @Transactional
  public UserResponse updateUserByAdmin(Long userId, AdminUpdateUserRequest request) {
    User currentUser = getCurrentUser();
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new EntityNotFoundException("User not found"));

    ensureManagerCanModifyOnlyCouriers(currentUser, user);

    if (!user.getUsername().equals(request.username()) && userRepository.existsByUsername(request.username())) {
      throw new EntityExistsException("User already exists with email: " + request.username());
    }

    user.setFirstName(request.firstName());
    user.setLastName(request.lastName());
    user.setUsername(request.username());
    user.setPhoneNumber(request.phoneNumber());

    if (request.roles() != null && !request.roles().isEmpty()) {
      List<Role> newRoles = new ArrayList<>();
      for (RoleName roleName : request.roles()) {
        Role role = roleService.getRoleByName(roleName);
        newRoles.add(role);
      }
      user.setRoles(newRoles);
    }

    User updatedUser = userRepository.save(user);
    return Mapper.toUserResponse(updatedUser);
  }

  @Override
  @Transactional
  public void deleteUser(Long userId) {
    User currentUser = getCurrentUser();
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new EntityNotFoundException("User not found"));

    ensureManagerCanModifyOnlyCouriers(currentUser, user);

    userRepository.deleteById(userId);
  }

  private void ensureManagerCanModifyOnlyCouriers(User currentUser, User targetUser) {
    if (hasRole(currentUser, RoleName.ROLE_MANAGER) &&
        targetUser.getRoles().stream().noneMatch(role -> role.getName().equals(RoleName.ROLE_COURIER.name()))) {
        throw new org.springframework.security.access.AccessDeniedException("Managers can only modify couriers.");
    }
  }

  @Override
  public Page<UserResponse> getUsersByRole(String role, Pageable pageable) {
      if (role == null || role.isBlank()) {
          return userRepository.findAll(pageable)
                  .map(Mapper::toUserResponse);
      }

      RoleName roleName;
      try {
          roleName = RoleName.valueOf(role);
      } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException("Invalid role: " + role);
      }

      Role roleEntity = roleService.getRoleByName(roleName);
      Page<User> users = userRepository.findByRolesContaining(roleEntity, pageable);

      return users.map(Mapper::toUserResponse);
  }

}
