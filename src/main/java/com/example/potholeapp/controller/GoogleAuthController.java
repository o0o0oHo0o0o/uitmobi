package com.example.potholeapp.controller;

import com.example.potholeapp.model.user.User;
import com.example.potholeapp.repository.UserRepository;
import com.example.potholeapp.request.GoogleAuthRequest;
import com.example.potholeapp.response.AuthenticationResponse;
import com.example.potholeapp.service.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class GoogleAuthController {

    @Autowired
    private UserRepository userRepository;
    private JwtService jwtService;

    private static final String CLIENT_ID = "240468386264-1asgrqr7ne0giuusaictohdordpuvkl3.apps.googleusercontent.com"; // Thay bằng Client ID của bạn

    @PostMapping("/google")
    public ResponseEntity<?> authenticateUserWithGoogle(@RequestBody GoogleAuthRequest request) {
        try {
            // Khởi tạo GoogleIdTokenVerifier
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();


            // Xác thực ID token
            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String fullName = (String) payload.get("name");

                Optional<User> optionalUser = userRepository.findByEmail(email);
                User user;
                if (optionalUser.isPresent()) {
                    user = optionalUser.get();
                } else {
                    user = new User();
                    user.setEmail(email);
                    user.setFullname(fullName);
                    user.setGoogle(true); // Đánh dấu là đăng nhập bằng Google
                    userRepository.save(user);
                }


                // Tạo JWT token
                String jwtToken = jwtService.generateToken(user);

                // Trả về token
                return ResponseEntity.ok(new AuthenticationResponse(
                        user.getId().toString(),
                        jwtToken,
                        jwtService.generateRefreshToken(user)
                ));

            } else {
                return ResponseEntity.badRequest().body("Invalid ID token.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error.");
        }
    }
}
