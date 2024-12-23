package com.example.potholeapp.service;

import com.example.potholeapp.model.pothole.Pothole;
import com.example.potholeapp.model.pothole.PotholeReport;
import com.example.potholeapp.model.user.User;
import com.example.potholeapp.repository.PotholeReportRepository;
import com.example.potholeapp.repository.PotholeRepository;
import com.example.potholeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class PotholeService {

    private final PotholeRepository potholeRepository;
    private final PotholeReportRepository potholeReportRepository;
    private final UserRepository userRepository;

    @Autowired
    public PotholeService(PotholeRepository potholeRepository, PotholeReportRepository potholeReportRepository, UserRepository userRepository) {
        this.potholeRepository = potholeRepository;
        this.potholeReportRepository = potholeReportRepository;
        this.userRepository = userRepository;
    }

    // Kiểm tra nếu ổ gà đã tồn tại gần đó, nếu có thì chỉ tăng số lượng báo cáo
    public boolean reportPothole(double latitude, double longitude, int severity, Integer userId) {
        // Lấy tất cả các ổ gà đã có trong cơ sở dữ liệu
        List<Pothole> existingPotholes = potholeRepository.findAll();

        for (Pothole pothole : existingPotholes) {
            // Tính khoảng cách giữa vị trí báo cáo và vị trí ổ gà hiện tại
            double distance = calculateDistance(latitude, longitude, pothole.getLatitude(), pothole.getLongitude());

            // Nếu khoảng cách nhỏ hơn 3m, coi như ổ gà đã tồn tại
            if (distance < 3) {
                // Cập nhật số lần báo cáo cho ổ gà hiện tại
                pothole.setReportCount(pothole.getReportCount() + 1);
                potholeRepository.save(pothole);

                // Lưu báo cáo của người dùng
                PotholeReport report = new PotholeReport(null, userId, pothole.getId(), LocalDateTime.now());
                potholeReportRepository.save(report);

                return true;
            }
        }

        // Nếu không tìm thấy ổ gà gần đó, tạo mới ổ gà
        Pothole newPothole = new Pothole(null, latitude, longitude, severity, 1, LocalDateTime.now());
        Pothole savedPothole = potholeRepository.save(newPothole);

        // Lưu báo cáo của người dùng
        PotholeReport report = new PotholeReport(null, userId, savedPothole.getId(), LocalDateTime.now());
        potholeReportRepository.save(report);

        return true;
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // Convert to meters
        return distance;
    }
    public List<Pothole> getPotholesByEmail(String email) {
        // Tìm User dựa trên email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Lấy danh sách báo cáo ổ gà của User
        List<PotholeReport> reports = potholeReportRepository.findAllByUserId(user.getId());

        // Lấy danh sách ổ gà từ danh sách báo cáo
        List<Pothole> potholes = reports.stream()
                .map(report -> potholeRepository.findById(report.getPotholeId())
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return potholes;
    }
}

