package com.upc.comparasalud.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProviderDashboardDTO {
    private List<UpcomingAppointmentDTO> upcomingAppointments;
    private MetricsDTO metrics;
    private List<RecentActivityDTO> recentActivity;
    private RevenueChartDTO revenueChart;
    private List<ReviewDTO> recentReviews;

    @Getter
    @Setter
    public static class UpcomingAppointmentDTO {
        private Long appointmentId;
        private String date;
        private String time;
        private String patientName;
        private String service;
        private String status;
    }

    @Getter
    @Setter
    public static class MetricsDTO {
        private long totalAppointmentsThisMonth;
        private Double appointmentsDeltaPct;

        private BigDecimal totalEarningsThisMonth;
        private Double earningsDeltaPct;

        private long uniquePatientsThisMonth;
        private Double patientsDeltaPct;

        private BigDecimal averageRating;
        private long reviewsCount;

        private double cancellationRate;
        private double attendanceRate;
        private Double averageConsultationMinutes;
    }

    @Getter
    @Setter
    public static class RecentActivityDTO {
        private String type;
        private String description;
        private String date;
    }

    @Getter
    @Setter
    public static class RevenueChartDTO {
        private List<RevenuePointDTO> points;
        private BigDecimal totalLast7Days;
        private Double deltaVsPreviousWeekPct;
    }

    @Getter
    @Setter
    public static class RevenuePointDTO {
        private String label;
        private String date;
        private BigDecimal amount;
    }

    @Getter
    @Setter
    public static class ReviewDTO {
        private Long id;
        private String patientName;
        private Integer rating;
        private String comment;
        private String relativeDate;
        private String date;
    }
}