//package com.example.potholeapp.controller;
//
//import com.example.potholeapp.model.user.User;
//import com.example.potholeapp.model.user.UserLocation;
//import com.example.potholeapp.service.UserLocationService;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Builder
//@AllArgsConstructor
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/api/location")
//public class UserLocationController {
//
//    @Autowired
//    private UserLocationService userLocationService;
//
//    @PostMapping("/update")
//    public ResponseEntity<?> updateLocation(@RequestBody LocationRequest locationRequest, Authentication authentication) {
//        User user = (User) authentication.getPrincipal();
//        userLocationService.updateUserLocation(user, locationRequest.getLatitude(), locationRequest.getLongitude());
//        return ResponseEntity.ok("Location updated successfully.");
//    }
//
//    @GetMapping("/others")
//    public ResponseEntity<List<UserLocationResponse>> getOthersLocations(Authentication authentication) {
//        User user = (User) authentication.getPrincipal();
//        List<UserLocation> locations = userLocationService.getOtherUsersLocations(user);
//        // Convert to response DTO
//        List<UserLocationResponse> response = locations.stream().map(loc -> new UserLocationResponse(
//                loc.getUser().getId(),
//                loc.getUser().getUsername(),
//                loc.getLatitude(),
//                loc.getLongitude(),
//                loc.getTimestamp()
//        )).toList();
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<UserLocationResponse> getUserLocation(@PathVariable Long userId, Authentication authentication) {
//        // Kiểm tra quyền truy cập nếu cần
//        // ...
//        // Lấy vị trí người dùng cụ thể
//         Optional<UserLocation> locationOpt = userLocationService.getUserLocation(userId);
//         if (locationOpt.isPresent()) {
//             UserLocation loc = locationOpt.get();
//             UserLocationResponse response = new UserLocationResponse(
//                 loc.getUser().getId(),
//                 loc.getUser().getUsername(),
//                 loc.getLatitude(),
//                 loc.getLongitude(),
//                 loc.getTimestamp()
//             );
//             return ResponseEntity.ok(response);
//         } else {
//             return ResponseEntity.notFound().build();
//         }
//    }
//
//    // Các lớp DTO
//    public static class LocationRequest {
//        private Double latitude;
//        private Double longitude;
//        // Getters and Setters
//
//        public Double getLatitude() {
//            return latitude;
//        }
//
//        public void setLatitude(Double latitude) {
//            this.latitude = latitude;
//        }
//
//        public Double getLongitude() {
//            return longitude;
//        }
//
//        public void setLongitude(Double longitude) {
//            this.longitude = longitude;
//        }
//    }
//
//    public static class UserLocationResponse {
//        private int userId;
//        private String username;
//        private Double latitude;
//        private Double longitude;
//        private LocalDateTime timestamp;
//
//        public UserLocationResponse() {}
//
//        public UserLocationResponse(Integer userId, String username, Double latitude, Double longitude, LocalDateTime timestamp) {
//            this.userId = userId;
//            this.username = username;
//            this.latitude = latitude;
//            this.longitude = longitude;
//            this.timestamp = timestamp;
//        }
//
//        // Getters and Setters
//    }
//}
