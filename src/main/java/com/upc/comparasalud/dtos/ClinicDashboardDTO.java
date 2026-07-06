package com.upc.comparasalud.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClinicDashboardDTO {
    private ClinicMetricsDTO metrics;
    private List<ClinicUpcomingAppointmentDTO> upcomingAppointments;
    private List<ClinicTeamMemberDTO> medicalTeam;
    private List<DepartmentDTO> departments;
    private ClinicStatsDTO stats;
    private ClinicRevenueChartDTO revenueChart;
    private List<ClinicRecentReviewDTO> recentReviews;
}