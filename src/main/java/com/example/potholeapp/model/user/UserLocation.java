//package com.example.potholeapp.model.user;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Data
//@Builder
//@RequiredArgsConstructor
//@AllArgsConstructor
//@Entity
//public class UserLocation {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    private Double latitude;
//
//    private Double longitude;
//
//    private LocalDateTime timestamp;
//
//    // Getters and Setters
//}
