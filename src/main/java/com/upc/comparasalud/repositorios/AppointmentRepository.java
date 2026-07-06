package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
            "WHERE a.provider.id = :providerId " +
            "AND a.date = :date " +
            "AND ((a.startTime < :endTime AND a.endTime > :startTime)) " +
            "AND a.status NOT IN ('CANCELLED')")
    boolean existsOverlappingAppointment(@Param("providerId") Long providerId,
                                         @Param("date") LocalDate date,
                                         @Param("startTime") LocalTime startTime,
                                         @Param("endTime") LocalTime endTime);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
            "WHERE a.provider.id = :providerId " +
            "AND a.date = :date " +
            "AND ((a.startTime < :endTime AND a.endTime > :startTime)) " +
            "AND a.status NOT IN ('CANCELLED') " +
            "AND a.id <> :excludeId")
    boolean existsOverlappingAppointmentExcluding(
            @Param("providerId") Long providerId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.provider.id = :providerId " +
            "AND a.date >= :today " +
            "AND a.status NOT IN ('CANCELLED') " +
            "ORDER BY a.date ASC, a.startTime ASC")
    List<Appointment> findUpcomingByProvider(@Param("providerId") Long providerId,
                                             @Param("today") LocalDate today);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.provider.id = :providerId " +
            "AND MONTH(a.date) = :month AND YEAR(a.date) = :year")
    List<Appointment> findByProviderAndMonth(@Param("providerId") Long providerId,
                                             @Param("month") int month,
                                             @Param("year") int year);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.provider.id = :providerId " +
            "ORDER BY a.createdAt DESC")
    List<Appointment> findRecentByProvider(@Param("providerId") Long providerId);
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.provider.id = :providerId " +
            "AND a.date = :date " +
            "AND a.status NOT IN ('CANCELLED') " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findByProviderAndDate(@Param("providerId") Long providerId,
                                            @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.provider.id = :providerId " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "AND a.status NOT IN ('CANCELLED')")
    long countByProviderAndDateRange(@Param("providerId") Long providerId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patient.authUser.id = :userId " +
            "AND a.status IN ('COMPLETED', 'CANCELLED') " +
            "ORDER BY a.date DESC, a.startTime DESC")
    List<Appointment> findHistoryByPatientUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patient.authUser.id = :userId " +
            "AND a.date >= CURRENT_DATE " +
            "AND a.status NOT IN ('CANCELLED', 'COMPLETED') " +
            "ORDER BY a.date ASC, a.startTime ASC")
    List<Appointment> findUpcomingByPatientUserId(@Param("userId") Long userId);
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.provider.id = :providerId " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "AND a.status NOT IN ('CANCELLED') " +
            "ORDER BY a.date ASC")
    List<Appointment> findByProviderAndDateRangeOrdered(@Param("providerId") Long providerId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.provider.id = :providerId " +
            "AND a.status IN ('COMPLETED', 'CANCELLED') " +
            "ORDER BY a.date DESC, a.startTime DESC")
    List<Appointment> findHistoryByProviderId(@Param("providerId") Long providerId);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.provider.id = :providerId " +
            "AND a.date >= CURRENT_DATE " +
            "AND a.status NOT IN ('CANCELLED', 'COMPLETED') " +
            "ORDER BY a.date ASC, a.startTime ASC")
    List<Appointment> findUpcomingByProviderId(@Param("providerId") Long providerId);


    @Query("SELECT a FROM Appointment a JOIN a.provider p JOIN p.clinics c " +
            "WHERE c.id = :clinicId " +
            "AND a.date >= :today " +
            "AND a.status NOT IN ('CANCELLED') " +
            "ORDER BY a.date ASC, a.startTime ASC")
    List<Appointment> findUpcomingByClinicId(@Param("clinicId") Long clinicId,
                                             @Param("today") LocalDate today);

    @Query("SELECT a FROM Appointment a JOIN a.provider p JOIN p.clinics c " +
            "WHERE c.id = :clinicId " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "AND a.status NOT IN ('CANCELLED') " +
            "ORDER BY a.date ASC")
    List<Appointment> findByClinicAndDateRange(@Param("clinicId") Long clinicId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(a) FROM Appointment a JOIN a.provider p JOIN p.clinics c " +
            "WHERE c.id = :clinicId " +
            "AND a.date = :date " +
            "AND a.status NOT IN ('CANCELLED')")
    long countByClinicAndDate(@Param("clinicId") Long clinicId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(DISTINCT a.patient.id) FROM Appointment a JOIN a.provider p JOIN p.clinics c " +
            "WHERE c.id = :clinicId " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "AND a.status NOT IN ('CANCELLED')")
    long countDistinctPatientsByClinicAndDateRange(@Param("clinicId") Long clinicId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(a.amountPaid), 0) FROM Appointment a JOIN a.provider p JOIN p.clinics c " +
            "WHERE c.id = :clinicId " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "AND a.status NOT IN ('CANCELLED')")
    BigDecimal sumAmountPaidByClinicAndDateRange(@Param("clinicId") Long clinicId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
}