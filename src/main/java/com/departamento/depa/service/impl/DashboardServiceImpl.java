package com.departamento.depa.service.impl;

import com.departamento.depa.entity.DashboardStats;
import com.departamento.depa.repository.*;
import com.departamento.depa.service.DashboardService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {

    @PersistenceContext
    private EntityManager entityManager;

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final StayRepository stayRepository;

    public DashboardServiceImpl(RoomRepository roomRepository,
                                UserRepository userRepository,
                                ReservationRepository reservationRepository,
                                StayRepository stayRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.stayRepository = stayRepository;
    }

    @Override
    @Cacheable(value = "dashboardStats", cacheManager = "cacheManager")
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        // 1. Conteos básicos
        stats.setTotalRooms(getFastCount("rooms"));
        stats.setTotalUsers(getFastCount("users"));
        stats.setTotalReservations(getFastCount("reservations"));
        stats.setTotalStays(getFastCount("stays"));

        // 2. Calcular ocupación real
        Long occupiedRooms = getOccupiedRoomsCount();
        Double occupancyRate = stats.getTotalRooms() > 0
                ? (occupiedRooms * 100.0 / stats.getTotalRooms())
                : 0.0;
        stats.setOccupancyRate(occupancyRate);

        // 3. ✅ TOTAL HISTÓRICO (usando stays, no reservations)
        stats.setTotalRevenue(getTotalRevenueFromStays());

        // 4. Estadísticas por tipo de habitación
        stats.setRoomTypeStats(getRoomTypeDistribution());

        // 5. ✅ Ingresos mensuales de los últimos 6 meses (usando stays)
        stats.setMonthlyRevenue(getLast6MonthsRevenueFromStays());

        return stats;
    }

    private Long getFastCount(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        return ((Number) entityManager.createNativeQuery(sql).getSingleResult()).longValue();
    }

    private Long getOccupiedRoomsCount() {
        String sql = "SELECT COUNT(*) FROM rooms WHERE estado = 'Ocupada'";
        return ((Number) entityManager.createNativeQuery(sql).getSingleResult()).longValue();
    }

    // ✅ NUEVO: Total histórico desde la tabla stays
    private BigDecimal getTotalRevenueFromStays() {
        String sql = """
            SELECT COALESCE(SUM(monto_total), 0)
            FROM stays
            WHERE estado = 'Finalizada' OR estado = 'En Curso'
        """;

        Object result = entityManager.createNativeQuery(sql).getSingleResult();
        return new BigDecimal(result.toString());
    }

    // Distribución de habitaciones por tipo
    private List<DashboardStats.RoomTypeStats> getRoomTypeDistribution() {
        String sql = """
            SELECT tipo, COUNT(*) as count 
            FROM rooms 
            GROUP BY tipo 
            ORDER BY count DESC
        """;

        List<Object[]> results = entityManager.createNativeQuery(sql).getResultList();
        List<DashboardStats.RoomTypeStats> stats = new ArrayList<>();

        for (Object[] row : results) {
            stats.add(new DashboardStats.RoomTypeStats(
                    (String) row[0],
                    ((Number) row[1]).longValue()
            ));
        }

        return stats.isEmpty() ?
                Arrays.asList(
                        new DashboardStats.RoomTypeStats("Individual", 0L),
                        new DashboardStats.RoomTypeStats("Doble", 0L),
                        new DashboardStats.RoomTypeStats("Suite", 0L)
                ) : stats;
    }

    // ✅ CORREGIDO: Ingresos mensuales desde stays (últimos 6 meses)
    private List<DashboardStats.MonthlyRevenue> getLast6MonthsRevenueFromStays() {
        List<DashboardStats.MonthlyRevenue> revenue = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            LocalDate date = today.minusMonths(i);
            YearMonth ym = YearMonth.of(date.getYear(), date.getMonth());
            LocalDate firstDay = ym.atDay(1);
            LocalDate lastDay = ym.atEndOfMonth();

            // ✅ Usamos fecha_entrada_real de stays en lugar de reservations
            String sql = """
                SELECT COALESCE(SUM(monto_total), 0)
                FROM stays s
                WHERE (s.estado = 'Finalizada' OR s.estado = 'En Curso')
                AND s.fecha_entrada_real >= :firstDay
                AND s.fecha_entrada_real <= :lastDay
            """;

            Object result = entityManager.createNativeQuery(sql)
                    .setParameter("firstDay", firstDay)
                    .setParameter("lastDay", lastDay)
                    .getSingleResult();

            // Formato corto del mes (MAR, ABR, MAY...)
            String monthName = date.getMonth().getDisplayName(
                    java.time.format.TextStyle.SHORT,
                    new Locale("es", "ES")
            ).toUpperCase();

            revenue.add(new DashboardStats.MonthlyRevenue(
                    monthName,
                    new BigDecimal(result.toString())
            ));
        }

        return revenue;
    }
}