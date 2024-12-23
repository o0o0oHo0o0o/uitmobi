package com.example.potholeapp.model.pothole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PotholeReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId; // ID người báo cáo
    private Integer potholeId; // ID ổ gà được báo cáo

    @Column(nullable = false, updatable = false)
    private LocalDateTime reportTime = LocalDateTime.now();
}
