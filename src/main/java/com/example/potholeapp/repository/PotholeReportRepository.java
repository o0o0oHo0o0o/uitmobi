package com.example.potholeapp.repository;

import com.example.potholeapp.model.pothole.PotholeReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PotholeReportRepository extends JpaRepository<PotholeReport, Long> {

    // Tìm tất cả báo cáo của ổ gà
    List<PotholeReport> findByPotholeId(Long potholeId);

    List<PotholeReport> findAllByUserId(Integer userId);
}
