package com.departamento.depa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private Long totalRooms;
    private Long totalUsers;
    private Long totalReservations;
    private Long totalStays;
    private BigDecimal totalRevenue;
    private Double occupancyRate;
    private List<MonthlyRevenue> monthlyRevenue;
    private List<RoomTypeStats> roomTypeStats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyRevenue {
        private String month;
        private BigDecimal amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomTypeStats {
        private String type;
        private Long count;
    }
}