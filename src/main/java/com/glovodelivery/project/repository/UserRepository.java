package com.glovodelivery.project.repository;

import com.glovodelivery.project.entity.User;
import com.glovodelivery.project.entity.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Page<User> findByRolesContaining(Role role, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_COURIER'")
    java.util.List<User> findAllCouriers();
}
