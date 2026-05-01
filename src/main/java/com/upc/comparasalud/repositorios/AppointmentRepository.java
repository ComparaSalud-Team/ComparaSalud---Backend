package com.upc.comparasalud.repositorios;

import com.upc.comparasalud.entidades.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;

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
            "AND a.id <> :excludeId")   // excluir la cita que se está reprogramando
    boolean existsOverlappingAppointmentExcluding(
            @Param("providerId") Long providerId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId);
}