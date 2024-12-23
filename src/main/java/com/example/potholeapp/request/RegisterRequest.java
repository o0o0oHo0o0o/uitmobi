package com.example.potholeapp.request;

import com.example.potholeapp.model.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  private String fullname;
  private String email;
  private String password;
  private Role role = Role.USER;
}
