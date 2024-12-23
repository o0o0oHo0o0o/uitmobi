package com.example.potholeapp.controller;

import com.example.potholeapp.model.pothole.Pothole;
import com.example.potholeapp.repository.PotholeRepository;
import com.example.potholeapp.request.PotholeReportRequest;
import com.example.potholeapp.service.PotholeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/potholes")
public class PotholeController {

    private final PotholeService potholeService;
    private final PotholeRepository potholeRepository;

    @Autowired
    public PotholeController(PotholeService potholeService, PotholeRepository potholeRepository) {
        this.potholeService = potholeService;
        this.potholeRepository = potholeRepository;
    }

    // Báo cáo ổ gà mới hoặc tăng số lượng báo cáo nếu ổ gà đã tồn tại gần đó
    @PostMapping("/report")
    public ResponseEntity<String> reportPothole(@RequestBody PotholeReportRequest request) {
        // Kiểm tra nếu các trường bắt buộc bị thiếu
        if (request.getLatitude() == null || request.getLongitude() == null ||
                request.getSeverity() == null || request.getUserId() == null) {
            return ResponseEntity.badRequest().body("Vị trí của ổ gà (latitude và longitude) không được bỏ trống.");
        }

        boolean result = potholeService.reportPothole(request.getLatitude(),
                request.getLongitude(),
                request.getSeverity(),
                request.getUserId());
        if (result) {
            return ResponseEntity.ok("Báo cáo ổ gà thành công!");
        } else {
            return ResponseEntity.badRequest().body("Có lỗi xảy ra khi báo cáo ổ gà.");
        }
    }
    @GetMapping
    public ResponseEntity<List<Pothole>> getAllPotholes() {
        List<Pothole> potholes = potholeRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("count", potholes.size());
        response.put("data", potholes);
        return ResponseEntity.ok(potholes);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Pothole> getPotholeById(@PathVariable Integer id) {
        return potholeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<Pothole>> getPotholesBySeverity(@PathVariable Integer severity) {
        List<Pothole> potholes = potholeRepository.findAllBySeverity(severity);
        if (potholes.isEmpty()) {
            // Trả về 204 No Content (hoặc 200 kèm danh sách rỗng tuỳ chọn)
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(potholes);
    }
    @GetMapping("/by-email")
    public ResponseEntity<List<Pothole>> getPotholesByEmail(@RequestParam String email) {
        List<Pothole> potholes = potholeService.getPotholesByEmail(email);
        return ResponseEntity.ok(potholes);
    }
    @GetMapping("/by-date")
    public ResponseEntity<List<Pothole>> getPotholesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            startDate = startDate.trim();
            endDate = endDate.trim();
            // Chuyển đổi String sang LocalDate
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            // Thêm thời gian mặc định nếu cần
            LocalDateTime startDateTime = start.atStartOfDay();
            LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

            List<Pothole> potholes = potholeRepository.findAllByDateRange(startDateTime, endDateTime);
            if (potholes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(potholes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }


}
