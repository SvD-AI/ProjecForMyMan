package com.glovodelivery.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

  @NotNull(message = "First name is required")
  private String firstName;

  @NotNull(message = "Last name is required")
  private String lastName;

  @Column(nullable = true)
  private String address;

  @NotNull(message = "Phone is required")
  private String phoneNumber;

  @JsonIgnore
  private String password;

  @NotNull(message = "Email is required")
  private String username;

  private boolean accountNonExpired;

  private boolean accountNonLocked;

  private boolean credentialsNonExpired;

  private boolean enabled;

  private boolean verified;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_role", joinColumns = {
          @JoinColumn(name = "user_id", referencedColumnName = "id"),
  }, inverseJoinColumns = {
          @JoinColumn(name = "role_id", referencedColumnName = "id")
  })
  private List<Role> roles = new ArrayList<>();
}
