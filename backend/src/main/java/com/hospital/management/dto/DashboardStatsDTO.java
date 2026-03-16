package com.hospital.management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsDTO {
    private long totalPatients;
    private long totalDoctors;
    private long appointmentsToday;
    private long availableDoctors;
}
