package com.example.potholeapp.controller;

import com.example.potholeapp.model.user.User;
import com.example.potholeapp.response.AuthenticationResponse;
import com.example.potholeapp.request.AuthenticationRequest;
import com.example.potholeapp.request.RegisterRequest;
import com.example.potholeapp.service.AuthenticationService;
import com.example.potholeapp.service.EmailService;
import com.example.potholeapp.service.JwtService;
import com.example.potholeapp.service.UserService;
import com.example.potholeapp.request.ChangePasswordRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;
  private final EmailService emailService;
  private final UserService userService;
  private final JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(service.register(request));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    service.refreshToken(request, response);
  }

  @PostMapping("/send-verification")
  public ResponseEntity<Map<String, Object>> sendVerificationEmail(@RequestBody Map<String, String> request) {
    String toEmail = request.get("toEmail");
    Map<String, Object> response = new HashMap<>();
    if (emailService.isCodeAlreadySent(toEmail)) {
      long ttl = emailService.getRemainingTTL(toEmail);
      response.put("message", "Verification email already sent.");
      response.put("ttl", ttl);
      return ResponseEntity.status(429).body(response); // HTTP 429: Too Many Requests
    }
    boolean isSent = emailService.sendVerificationEmail(toEmail);
    if (isSent) {
      response.put("message", "Verification email sent successfully!");
      return ResponseEntity.ok(response);
    } else {
      response.put("message", "Failed to send verification email!");
      return ResponseEntity.status(500).body(response);
    }
  }

  @PostMapping("/verifyCode")
  public ResponseEntity<Map<String, String>> verifyCode(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    String code = request.get("code");
    Map<String, String> response = new HashMap<>();
    if (emailService.verifyCode(email, code)) {
      response.put("message", "Verification successful!");
      return ResponseEntity.ok(response);
    } else {
      response.put("message", "Invalid verification code!");
      return ResponseEntity.status(400).body(response);
    }
  }

  @PostMapping("/change-password")
  public ResponseEntity<?> changePassword(
          @RequestBody ChangePasswordRequest request,
          @AuthenticationPrincipal UserDetails userDetails
  ) {
    if (userDetails == null) {
      Map<String, String> response = new HashMap<>();
      response.put("error", "User is not authenticated");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    try {
      userService.changePassword(request, userDetails.getUsername());
      Map<String, String> response = new HashMap<>();
      response.put("message", "Password changed successfully");
      return ResponseEntity.ok(response);
    } catch (IllegalStateException e) {
      Map<String, String> response = new HashMap<>();
      response.put("error", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }
  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request, Authentication authentication) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String jwt = authHeader.substring(7);
      jwtService.revokeToken(jwt);
      return ResponseEntity.ok().body("Đăng xuất thành công");
    }
    return ResponseEntity.badRequest().body("Token không hợp lệ");
  }
  @PostMapping("/forgot-password")
  public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    Map<String, String> response = new HashMap<>();

    // Kiểm tra email có tồn tại
    if (userService.findByEmail(email) == null) {
      response.put("message", "Email không tồn tại trong hệ thống.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Gửi email đặt lại mật khẩu
    boolean isSent = emailService.sendVerificationEmail(email);
    if (isSent) {
      response.put("message", "Email đặt lại mật khẩu đã được gửi!");
      return ResponseEntity.ok(response);
    } else {
      response.put("message", "Không thể gửi email đặt lại mật khẩu.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
  @PostMapping("/verify-reset-password")
  public ResponseEntity<Map<String, String>> verifyResetPassword(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    String code = request.get("code");
    Map<String, String> response = new HashMap<>();

    // Kiểm tra mã xác minh
    if (!emailService.verifyCode(email, code)) {
      response.put("message", "Mã xác minh không hợp lệ!");
      return ResponseEntity.status(400).body(response);
    }
    User user = userService.findByEmail(email);
    // Tạo access token sau khi xác minh thành công
    String accessToken = service.generateToken(user);

    response.put("message", "Xác minh thành công và đăng nhập thành công.");
    response.put("accessToken", accessToken);
    return ResponseEntity.ok(response);
  }
  @PostMapping("/reset-password")
  public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    String newPassword = request.get("newPassword");
    Map<String, String> response = new HashMap<>();


    // Đặt lại mật khẩu
    try {
      userService.updatePassword(email, newPassword);
      response.put("message", "Đặt lại mật khẩu thành công!");
      return ResponseEntity.ok(response);
    } catch (IllegalStateException e) {
      response.put("message", e.getMessage());
      return ResponseEntity.status(500).body(response);
    }
  }

}
