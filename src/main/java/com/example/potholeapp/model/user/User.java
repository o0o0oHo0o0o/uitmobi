package com.example.potholeapp.model.user;

import com.example.potholeapp.model.token.Token;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Sử dụng AUTO_INCREMENT
  private Integer id;

  @Column(nullable = false) // Không cho phép null
  private String fullname;

  @Column(nullable = false) // Email không được null và phải là duy nhất
  private String email;

  @Column(nullable = false) // Mật khẩu không được null
  private String password;

  private boolean isGoogle;
//  @Column(name = "share_location", nullable = false) // Sửa lại để Hibernate quản lý nullable
//  private Boolean shareLocation = true; // Đảm bảo giá trị mặc định trong Java

  @Enumerated(EnumType.STRING) // Lưu enum dưới dạng chuỗi
  @Column(nullable = false) // Role không được null
  private Role role;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Token> tokens;

  // Spring Security overrides...
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
