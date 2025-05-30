package com.glovodelivery.project.service.impl;

import com.glovodelivery.project.enums.RoleName;
import com.glovodelivery.project.entity.Role;
import com.glovodelivery.project.repository.RoleRepository;
import com.glovodelivery.project.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getRoleByName(RoleName roleName) {

        return roleRepository.findByName(roleName).orElseThrow(() -> new
                EntityNotFoundException("Role not found"));
    }
}
