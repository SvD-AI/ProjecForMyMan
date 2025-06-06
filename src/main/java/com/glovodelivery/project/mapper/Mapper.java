package com.glovodelivery.project.mapper;

import com.glovodelivery.project.dto.CustomUserDetails;
import com.glovodelivery.project.dto.response.RoleResponse;
import com.glovodelivery.project.dto.response.UserResponse;
import com.glovodelivery.project.entity.Role;
import com.glovodelivery.project.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

public class Mapper {

    public static CustomUserDetails toCustomUserDetails(User user){

        CustomUserDetails customUserDetails = new CustomUserDetails();
        BeanUtils.copyProperties(user, customUserDetails);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        user.getRoles().forEach(r -> {
            authorities.add(new SimpleGrantedAuthority(r.getName().getValue()));

            r.getPermissions().forEach(p ->  {
                if(user.isVerified()){
                    authorities.add(new SimpleGrantedAuthority(p.getName()));
                }else if(!p.isRequiresVerification()){
                    authorities.add(new SimpleGrantedAuthority(p.getName()));
                }
            });
        });

        customUserDetails.setAuthorities(authorities);
        return customUserDetails;
    }

    public static UserResponse toUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse, "roles");

        List<RoleResponse> roles = new ArrayList<>();

        user.getRoles().forEach(role -> roles.add(toRoleResponse(role)));

        userResponse.setRoles(roles);
        return userResponse;
    }

    public static RoleResponse toRoleResponse(Role role) {
        RoleResponse roleResponse = new RoleResponse();
        BeanUtils.copyProperties(role, roleResponse, "permissions");

        List<String> permissions = new ArrayList<>();
        role.getPermissions().forEach(p -> permissions.add(p.getName()));
        roleResponse.setPermissions(permissions);

        return roleResponse;
    }
}
