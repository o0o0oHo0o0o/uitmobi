package com.example.potholeapp.repository;

import com.example.potholeapp.model.pothole.Pothole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PotholeRepository extends JpaRepository<Pothole, Integer> {

    // Tìm ổ gà dựa trên vị trí (vĩ độ và kinh độ)
    Optional<Pothole> findByLatitudeAndLongitude(Double latitude, Double longitude);

    List<Pothole> findAllBySeverity(Integer severity);

    @Query("SELECT p FROM Pothole p WHERE DATE(p.timestamp) = :date")
    List<Pothole> findByDay(@Param("date") String date);

    @Query("SELECT p FROM Pothole p WHERE p.timestamp BETWEEN :startDate AND :endDate")
    List<Pothole> findAllByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
