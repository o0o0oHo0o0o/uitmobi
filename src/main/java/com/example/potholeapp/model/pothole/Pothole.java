package com.example.potholeapp.model.pothole;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pothole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double latitude;    // Vĩ độ
    private Double longitude;   // Kinh độ
    private Integer severity = 1;  // Mức độ nghiêm trọng
    private Integer reportCount = 1; // Số lần báo cáo

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}
