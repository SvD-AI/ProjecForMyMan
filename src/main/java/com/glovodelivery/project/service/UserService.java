package com.glovodelivery.project.service;

import com.glovodelivery.project.dto.request.UserRegistrationRequest;
import com.glovodelivery.project.dto.request.AdminCreateUserRequest;
import com.glovodelivery.project.dto.request.AdminUpdateUserRequest;
import com.glovodelivery.project.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse registerUser(UserRegistrationRequest userRegistrationRequest);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse getUser(Long userId);

    UserResponse createUserByAdmin(AdminCreateUserRequest request);

    UserResponse getUserResponseByEmail(String email);

    UserResponse updateUserByAdmin(Long userId, AdminUpdateUserRequest request);

    void deleteUser(Long userId);


}
