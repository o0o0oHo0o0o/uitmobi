package com.example.potholeapp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PotholeReportRequest {
    private Double latitude;
    private Double longitude;
    private Integer severity;
    private Integer userId;
}
