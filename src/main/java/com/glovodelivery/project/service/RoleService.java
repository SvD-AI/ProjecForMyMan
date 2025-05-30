package com.glovodelivery.project.service;

import com.glovodelivery.project.entity.Role;
import com.glovodelivery.project.enums.RoleName;

public interface RoleService {

    Role getRoleByName(RoleName roleName);
}
