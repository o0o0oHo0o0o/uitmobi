package com.alibou.security.controller;

import com.alibou.security.response.AuthenticationResponse;
import com.alibou.security.request.AuthenticationRequest;
import com.alibou.security.request.RegisterRequest;
import com.alibou.security.service.AuthenticationService;
import com.alibou.security.service.EmailService;
import com.alibou.security.service.UserService;
import com.alibou.security.request.ChangePasswordRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;
  @Autowired
  private EmailService emailService;

  private UserService userService;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(service.register(request));
  }
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
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
          Principal principal
  ) {
    try {
      userService.changePassword(request, principal);
      Map<String, String> response = new HashMap<>();
      response.put("message", "Password changed successfully");
      return ResponseEntity.ok(response);
    } catch (IllegalStateException e) {
      Map<String, String> response = new HashMap<>();
      response.put("error", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
  }
}