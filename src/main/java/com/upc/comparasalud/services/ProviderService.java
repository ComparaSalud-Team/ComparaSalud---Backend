package com.upc.comparasalud.services;

import com.upc.comparasalud.dtos.AvailabilityDTO;
import com.upc.comparasalud.dtos.EducationDTO;
import com.upc.comparasalud.dtos.MedicalServiceDTO;
import com.upc.comparasalud.dtos.PriceItemDTO;
import com.upc.comparasalud.dtos.ProviderDTO;
import com.upc.comparasalud.dtos.ProviderDashboardDTO;
import com.upc.comparasalud.entidades.Appointment;

import com.upc.comparasalud.entidades.AuthUser;
import com.upc.comparasalud.entidades.Clinic;
import com.upc.comparasalud.entidades.MedicalService;
import com.upc.comparasalud.entidades.PriceItem;
import com.upc.comparasalud.entidades.Provider;
import com.upc.comparasalud.entidades.ProviderEducation;
import com.upc.comparasalud.exceptions.BadRequestException;
import com.upc.comparasalud.exceptions.ForbiddenException;
import com.upc.comparasalud.exceptions.ResourceNotFoundException;
import com.upc.comparasalud.repositorios.AppointmentRepository;
import com.upc.comparasalud.repositorios.AuthUserRepository;
import com.upc.comparasalud.repositorios.MedicalServiceRepository;
import com.upc.comparasalud.repositorios.ProviderRepository;
import com.upc.comparasalud.repositorios.ReviewRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.upc.comparasalud.dtos.ProviderSearchResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ProviderService {

    @Autowired private ProviderRepository providerRepository;
    @Autowired private AuthUserRepository authUserRepository;
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private MedicalServiceRepository medicalServiceRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ModelMapper modelMapper;
    @Autowired private ClinicService clinicService;

    public List<ProviderDTO> listarTodos() {
        return providerRepository.findAll().stream().map(this::toDTO).toList();
    }

    public ProviderDTO buscarPorId(Long id) {
        return toDTO(providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id)));
    }

    public List<ProviderDTO> buscarPorEspecialidad(String specialty) {
        return providerRepository.findBySpecialtyContainingIgnoreCase(specialty)
                .stream().map(this::toDTO).toList();
    }

    public List<ProviderDTO> buscarPorDireccion(String address) {
        List<Provider> providers = providerRepository.findByAddressContainingIgnoreCase(address);
        if (providers.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron proveedores en la dirección: " + address);
        }
        return providers.stream().map(this::toDTO).toList();
    }

    @Transactional
    public ProviderDTO actualizarProveedor(Long id, ProviderDTO dto, String callerEmail) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));

        if (!provider.getAuthUser().getEmail().equals(callerEmail)) {
            throw new ForbiddenException("No tienes permiso para modificar este perfil");
        }

        provider.setFullName(dto.getFullName());
        provider.setPhone(dto.getPhone());
        provider.setSpecialty(dto.getSpecialty());
        provider.setDescription(dto.getDescription());
        provider.setPricePerAppointment(dto.getPricePerAppointment());
        provider.setAverageRating(dto.getAverageRating());
        provider.setExperienceYears(dto.getExperienceYears());
        provider.setLanguage(dto.getLanguage());
        provider.setModality(dto.getModality());
        provider.setStreet(dto.getStreet());
        provider.setDistrict(dto.getDistrict());
        provider.setCity(dto.getCity());
        provider.setCountry(dto.getCountry());

        // El frontend de editar-perfil aún no envía este campo; solo se
        // actualiza si viene explícito, para no pisarlo con null.
        if (dto.getDurationMinutes() != null) {
            provider.setDurationMinutes(dto.getDurationMinutes());
        }

        provider.setCedulaProfesional(dto.getCedulaProfesional());
        provider.setRegistroMedico(dto.getRegistroMedico());

        if (dto.getAreasEnfoque() != null) {
            provider.setAreasEnfoque(dto.getAreasEnfoque());
        }
        if (dto.getCertificaciones() != null) {
            provider.setCertificaciones(dto.getCertificaciones());
        }
        if (dto.getHorario() != null) {
            provider.setHorario(dto.getHorario());
        }

        if (dto.getPrecios() != null) {
            provider.setPrecios(dto.getPrecios().stream()
                    .map(p -> new PriceItem(p.getNombre(), p.getMonto()))
                    .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new)));
        }

        if (dto.getEducacion() != null) {
            provider.getEducacion().clear();
            for (EducationDTO eduDto : dto.getEducacion()) {
                ProviderEducation edu = new ProviderEducation();
                edu.setTitulo(eduDto.getTitulo());
                edu.setInstitucion(eduDto.getInstitucion());
                edu.setPeriodo(eduDto.getPeriodo());
                edu.setDetalle(eduDto.getDetalle());
                edu.setProvider(provider);
                provider.getEducacion().add(edu);
            }
        }

        provider = providerRepository.save(provider);

        // Para que el comparador de precios/proveedores refleje esta edición
        // sin necesidad de cargar el precio a mano en cada clínica asociada.
        clinicService.sincronizarPrecioDesdeProveedor(provider);

        return toDTO(provider);
    }

    @Transactional
    public ProviderDTO actualizarServicios(Long id, List<Long> serviceIds, String callerEmail) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));

        if (!provider.getAuthUser().getEmail().equals(callerEmail)) {
            throw new ForbiddenException("No tienes permiso para modificar este perfil");
        }

        List<MedicalService> servicios = medicalServiceRepository.findAllById(serviceIds);
        provider.setServices(servicios);

        provider = providerRepository.save(provider);
        return toDTO(provider);
    }

    public ProviderDashboardDTO getDashboard(String callerEmail) {

        AuthUser authUser = authUserRepository.findByEmail(callerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Provider provider = providerRepository.findByAuthUser(authUser)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));

        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();

        LocalDate prevMonthDate = today.minusMonths(1);
        int prevMonth = prevMonthDate.getMonthValue();
        int prevYear = prevMonthDate.getYear();

        // ── Upcoming appointments ────────────────────────────────────
        List<Appointment> upcoming = appointmentRepository
                .findUpcomingByProvider(provider.getId(), today);

        List<ProviderDashboardDTO.UpcomingAppointmentDTO> upcomingDTOs = upcoming.stream()
                .limit(5)
                .map(a -> {
                    ProviderDashboardDTO.UpcomingAppointmentDTO dto =
                            new ProviderDashboardDTO.UpcomingAppointmentDTO();
                    dto.setAppointmentId(a.getId());
                    dto.setDate(a.getDate().toString());
                    dto.setTime(a.getStartTime().toString());
                    dto.setPatientName(a.getPatient().getName());
                    dto.setService(a.getServiceName());
                    dto.setStatus(a.getStatus());
                    return dto;
                }).toList();

        // ── Este mes vs mes anterior (para deltas) ────────────────────
        List<Appointment> thisMonth = appointmentRepository
                .findByProviderAndMonth(provider.getId(), month, year);
        List<Appointment> prevMonthAppointments = appointmentRepository
                .findByProviderAndMonth(provider.getId(), prevMonth, prevYear);

        long totalThisMonth = thisMonth.size();
        long totalPrevMonth = prevMonthAppointments.size();

        BigDecimal earnings = sumEarnings(thisMonth, provider);
        BigDecimal earningsPrev = sumEarnings(prevMonthAppointments, provider);

        long cancelled = thisMonth.stream()
                .filter(a -> "CANCELLED".equals(a.getStatus())).count();
        long completed = thisMonth.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus())).count();

        double cancellationRate = totalThisMonth > 0
                ? redondear((cancelled * 100.0) / totalThisMonth) : 0.0;

        double attendanceRate = (completed + cancelled) > 0
                ? redondear((completed * 100.0) / (completed + cancelled)) : 0.0;

        long pacientesUnicosMes = thisMonth.stream()
                .map(a -> a.getPatient().getId())
                .distinct()
                .count();
        long pacientesUnicosMesAnterior = prevMonthAppointments.stream()
                .map(a -> a.getPatient().getId())
                .distinct()
                .count();

        java.util.OptionalDouble duracionOpt = thisMonth.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()))
                .mapToLong(a -> java.time.Duration.between(a.getStartTime(), a.getEndTime()).toMinutes())
                .average();
        Double duracionPromedio = duracionOpt.isPresent() ? redondear(duracionOpt.getAsDouble()) : null;

        ProviderDashboardDTO.MetricsDTO metrics = new ProviderDashboardDTO.MetricsDTO();
        metrics.setTotalAppointmentsThisMonth(totalThisMonth);
        metrics.setAppointmentsDeltaPct(calcularDeltaPct(totalThisMonth, totalPrevMonth));

        metrics.setTotalEarningsThisMonth(earnings);
        metrics.setEarningsDeltaPct(calcularDeltaPct(earnings, earningsPrev));

        metrics.setUniquePatientsThisMonth(pacientesUnicosMes);
        metrics.setPatientsDeltaPct(calcularDeltaPct(pacientesUnicosMes, pacientesUnicosMesAnterior));

        metrics.setAverageRating(provider.getAverageRating() != null
                ? provider.getAverageRating() : BigDecimal.ZERO);
        metrics.setReviewsCount(reviewRepository.countByProviderId(provider.getId()));

        metrics.setCancellationRate(cancellationRate);
        metrics.setAttendanceRate(attendanceRate);
        metrics.setAverageConsultationMinutes(duracionPromedio);

        // ── Recent activity ──────────────────────────────────────────
        List<Appointment> recent = appointmentRepository
                .findRecentByProvider(provider.getId());

        List<ProviderDashboardDTO.RecentActivityDTO> recentDTOs = recent.stream()
                .limit(5)
                .map(a -> {
                    ProviderDashboardDTO.RecentActivityDTO dto =
                            new ProviderDashboardDTO.RecentActivityDTO();
                    if ("COMPLETED".equals(a.getStatus())) {
                        dto.setType("appointment_completed");
                        dto.setDescription("Consulta completada con " + a.getPatient().getName());
                    } else if ("CANCELLED".equals(a.getStatus())) {
                        dto.setType("appointment_cancelled");
                        dto.setDescription("Cita cancelada con " + a.getPatient().getName());
                    } else {
                        dto.setType("appointment_scheduled");
                        dto.setDescription("Nueva cita con " + a.getPatient().getName());
                    }
                    dto.setDate(a.getDate().toString());
                    return dto;
                }).toList();

        // ── Gráfico de ingresos (últimos 7 días) ──────────────────────
        ProviderDashboardDTO.RevenueChartDTO revenueChart = construirRevenueChart(provider, today);

        // ── Reseñas recientes ──────────────────────────────────────────
        List<ProviderDashboardDTO.ReviewDTO> reviewDTOs = reviewRepository
                .findByProviderIdOrderByCreatedAtDesc(provider.getId())
                .stream()
                .limit(5)
                .map(this::toReviewDTO)
                .toList();

        ProviderDashboardDTO dashboard = new ProviderDashboardDTO();
        dashboard.setUpcomingAppointments(upcomingDTOs);
        dashboard.setMetrics(metrics);
        dashboard.setRecentActivity(recentDTOs);
        dashboard.setRevenueChart(revenueChart);
        dashboard.setRecentReviews(reviewDTOs);
        return dashboard;
    }

    private BigDecimal sumEarnings(List<Appointment> appointments, Provider provider) {
        return appointments.stream()
                .filter(a -> !"CANCELLED".equals(a.getStatus()))
                .map(a -> a.getAmountPaid() != null
                        ? a.getAmountPaid()
                        : (provider.getPricePerAppointment() != null
                           ? provider.getPricePerAppointment() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Double calcularDeltaPct(long actual, long anterior) {
        if (anterior == 0) return actual > 0 ? 100.0 : 0.0;
        return redondear(((actual - anterior) * 100.0) / anterior);
    }

    private Double calcularDeltaPct(BigDecimal actual, BigDecimal anterior) {
        if (anterior == null || anterior.compareTo(BigDecimal.ZERO) == 0) {
            return actual != null && actual.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        double delta = actual.subtract(anterior)
                .divide(anterior, 4, java.math.RoundingMode.HALF_UP)
                .doubleValue() * 100.0;
        return redondear(delta);
    }

    private double redondear(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }

    private ProviderDashboardDTO.RevenueChartDTO construirRevenueChart(Provider provider, LocalDate today) {
        LocalDate hace6Dias = today.minusDays(6);
        LocalDate hace7Dias = today.minusDays(7);
        LocalDate hace13Dias = today.minusDays(13);

        List<Appointment> ultimos7 = appointmentRepository
                .findByProviderAndDateRangeOrdered(provider.getId(), hace6Dias, today);
        List<Appointment> previos7 = appointmentRepository
                .findByProviderAndDateRangeOrdered(provider.getId(), hace13Dias, hace7Dias);

        java.util.Map<LocalDate, BigDecimal> porDia = new java.util.LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            porDia.put(hace6Dias.plusDays(i), BigDecimal.ZERO);
        }
        for (Appointment a : ultimos7) {
            BigDecimal monto = a.getAmountPaid() != null
                    ? a.getAmountPaid()
                    : (provider.getPricePerAppointment() != null
                       ? provider.getPricePerAppointment() : BigDecimal.ZERO);
            porDia.merge(a.getDate(), monto, BigDecimal::add);
        }

        List<ProviderDashboardDTO.RevenuePointDTO> points = new java.util.ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (var entry : porDia.entrySet()) {
            ProviderDashboardDTO.RevenuePointDTO punto = new ProviderDashboardDTO.RevenuePointDTO();
            punto.setDate(entry.getKey().toString());
            punto.setLabel(nombreDiaCorto(entry.getKey().getDayOfWeek()));
            punto.setAmount(entry.getValue());
            points.add(punto);
            total = total.add(entry.getValue());
        }

        BigDecimal totalPrevio = previos7.stream()
                .map(a -> a.getAmountPaid() != null
                        ? a.getAmountPaid()
                        : (provider.getPricePerAppointment() != null
                           ? provider.getPricePerAppointment() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ProviderDashboardDTO.RevenueChartDTO chart = new ProviderDashboardDTO.RevenueChartDTO();
        chart.setPoints(points);
        chart.setTotalLast7Days(total);
        chart.setDeltaVsPreviousWeekPct(calcularDeltaPct(total, totalPrevio));
        return chart;
    }

    private String nombreDiaCorto(java.time.DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "Lun";
            case TUESDAY -> "Mar";
            case WEDNESDAY -> "Mié";
            case THURSDAY -> "Jue";
            case FRIDAY -> "Vie";
            case SATURDAY -> "Sáb";
            case SUNDAY -> "Dom";
        };
    }

    private ProviderDashboardDTO.ReviewDTO toReviewDTO(com.upc.comparasalud.entidades.Review review) {
        ProviderDashboardDTO.ReviewDTO dto = new ProviderDashboardDTO.ReviewDTO();
        dto.setId(review.getId());
        dto.setPatientName(review.getPatient().getName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setDate(review.getCreatedAt().toLocalDate().toString());
        dto.setRelativeDate(tiempoRelativo(review.getCreatedAt()));
        return dto;
    }

    private String tiempoRelativo(java.time.LocalDateTime fecha) {
        java.time.Duration diff = java.time.Duration.between(fecha, java.time.LocalDateTime.now());
        long minutos = diff.toMinutes();
        if (minutos < 60) return "Hace " + Math.max(minutos, 1) + (minutos == 1 ? " minuto" : " minutos");
        long horas = diff.toHours();
        if (horas < 24) return "Hace " + horas + (horas == 1 ? " hora" : " horas");
        long dias = diff.toDays();
        if (dias < 30) return "Hace " + dias + (dias == 1 ? " día" : " días");
        long meses = dias / 30;
        return "Hace " + meses + (meses == 1 ? " mes" : " meses");
    }

    private static final int SLOTS_PER_DAY = 10;

    public List<ProviderDTO> filtrarPorDisponibilidad(String periodo) {
        if (periodo == null || periodo.isBlank()) {
            throw new BadRequestException("Debe indicar un periodo de disponibilidad");
        }

        LocalDate hoy = LocalDate.now();
        LocalDate desde = hoy;
        LocalDate hasta;

        switch (periodo) {
            case "hoy":
                hasta = hoy;
                break;
            case "esta-semana":
                hasta = hoy.plusDays(6);
                break;
            case "este-mes":
                hasta = hoy.withDayOfMonth(hoy.lengthOfMonth());
                break;
            default:
                throw new BadRequestException("Periodo inválido. Use: hoy, esta-semana o este-mes");
        }

        long diasEnRango = java.time.temporal.ChronoUnit.DAYS.between(desde, hasta) + 1;
        long slotsTotales = diasEnRango * SLOTS_PER_DAY;

        List<Provider> providers = providerRepository.findAll();
        LocalDate finalDesde = desde;
        LocalDate finalHasta = hasta;

        List<Provider> disponibles = providers.stream()
                .filter(p -> {
                    long citasOcupadas = appointmentRepository
                            .countByProviderAndDateRange(p.getId(), finalDesde, finalHasta);
                    return citasOcupadas < slotsTotales;
                })
                .toList();

        return disponibles.stream().map(this::toDTO).toList();
    }

    private ProviderDTO toDTO(Provider provider) {
        ProviderDTO dto = modelMapper.map(provider, ProviderDTO.class);
        dto.setEmail(provider.getAuthUser().getEmail());
        dto.setAuthUserId(provider.getAuthUser().getId());
        dto.setPhotoUrl(provider.getAuthUser().getProfilePhotoUrl());
        dto.setClinicIds(provider.getClinics().stream().map(Clinic::getId).toList());
        dto.setClinicNames(provider.getClinics().stream().map(Clinic::getName).toList());

        dto.setEducacion(provider.getEducacion().stream()
                .map(e -> new EducationDTO(e.getId(), e.getTitulo(), e.getInstitucion(), e.getPeriodo(), e.getDetalle()))
                .toList());

        dto.setPrecios(provider.getPrecios().stream()
                .map(p -> new PriceItemDTO(p.getNombre(), p.getMonto()))
                .toList());

        dto.setServices(provider.getServices().stream()
                .map(s -> new MedicalServiceDTO(
                        s.getId(), s.getName(), s.getDescription(), s.getPrice(), s.getIsActive(),
                        s.getCategory() != null ? s.getCategory().getId() : null,
                        s.getCategory() != null ? s.getCategory().getName() : null))
                .toList());

        return dto;
    }

    public ProviderDTO buscarPorEmail(String email) {
        AuthUser authUser = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Provider provider = providerRepository.findByAuthUser(authUser)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
        return toDTO(provider);
    }

    public List<ProviderDTO> filtrarPorPrecio(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null || maxPrice == null) {
            throw new BadRequestException("Debe ingresar un precio mínimo y máximo");
        }
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new BadRequestException("El precio mínimo no puede ser mayor al máximo");
        }
        List<Provider> providers = providerRepository.findByPriceBetween(minPrice, maxPrice);
        if (providers.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron proveedores en ese rango de precio");
        }
        return providers.stream().map(this::toDTO).toList();
    }

    public List<ProviderDTO> filtrarPorRating(BigDecimal minRating) {
        if (minRating == null) {
            throw new BadRequestException("Debe ingresar un rating mínimo");
        }
        if (minRating.compareTo(BigDecimal.ONE) < 0 ||
                minRating.compareTo(new BigDecimal("5")) > 0) {
            throw new BadRequestException("El rating debe estar entre 1 y 5");
        }
        List<Provider> providers = providerRepository.findByMinRating(minRating);
        if (providers.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron proveedores");
        }
        return providers.stream().map(this::toDTO).toList();
    }

    public List<ProviderDTO> ordenarResultados(String sortBy, String order) {
        if (sortBy == null || (!sortBy.equals("price") && !sortBy.equals("rating"))) {
            throw new BadRequestException("El criterio de orden debe ser 'price' o 'rating'");
        }
        if (order == null || (!order.equals("asc") && !order.equals("desc"))) {
            throw new BadRequestException("El orden debe ser 'asc' o 'desc'");
        }

        List<Provider> providers;

        if (sortBy.equals("price") && order.equals("asc")) {
            providers = providerRepository.findAllOrderByPriceAsc();
        } else if (sortBy.equals("price") && order.equals("desc")) {
            providers = providerRepository.findAllOrderByPriceDesc();
        } else if (sortBy.equals("rating") && order.equals("asc")) {
            providers = providerRepository.findAllOrderByRatingAsc();
        } else {
            providers = providerRepository.findAllOrderByRatingDesc();
        }

        if (providers.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron proveedores");
        }
        return providers.stream().map(this::toDTO).toList();
    }

    public List<AvailabilityDTO> verDisponibilidad(Long providerId, String date) {
        if (date == null || date.isBlank()) {
            throw new BadRequestException("Debe ingresar una fecha");
        }

        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proveedor no encontrado con ID: " + providerId));

        LocalDate localDate = LocalDate.parse(date);
        String diaSemana = nombreDiaEnEspanol(localDate.getDayOfWeek());

        List<Appointment> citas = appointmentRepository
                .findByProviderAndDate(providerId, localDate);

        int duracionMinutos = provider.getDurationMinutes() != null
                ? provider.getDurationMinutes() : 60;

        List<AvailabilityDTO> slots = new java.util.ArrayList<>();
        List<String> horarios = provider.getHorario();

        if (horarios == null || horarios.isEmpty()) {
            return slots; // El proveedor no configuró horario -> sin disponibilidad
        }

        for (String bloque : horarios) {
            // "Lunes, Martes: 08:00 - 17:00" -> el primer ":" siempre separa
            // los días del rango de horas (los días no llevan ":").
            String[] partes = bloque.split(":", 2);
            if (partes.length < 2) continue;

            List<String> dias = java.util.Arrays.stream(partes[0].split(","))
                    .map(String::trim)
                    .toList();

            if (!dias.contains(diaSemana)) continue;

            String[] rango = partes[1].trim().split("-");
            if (rango.length < 2) continue;

            LocalTime inicio;
            LocalTime fin;
            try {
                inicio = LocalTime.parse(rango[0].trim());
                fin = LocalTime.parse(rango[1].trim());
            } catch (Exception e) {
                continue; // bloque mal formado, se ignora
            }

            LocalTime cursor = inicio;
            while (cursor.isBefore(fin)) {
                LocalTime slotEnd = cursor.plusMinutes(duracionMinutos);
                if (slotEnd.isAfter(fin)) break;

                final LocalTime slotStart = cursor;
                boolean ocupado = citas.stream().anyMatch(a ->
                        a.getStartTime().isBefore(slotEnd) &&
                                a.getEndTime().isAfter(slotStart));

                AvailabilityDTO slot = new AvailabilityDTO();
                slot.setDate(date);
                slot.setStartTime(slotStart.toString());
                slot.setEndTime(slotEnd.toString());
                slot.setIsAvailable(!ocupado);
                slots.add(slot);

                cursor = slotEnd;
            }
        }

        slots.sort(java.util.Comparator.comparing(AvailabilityDTO::getStartTime));
        return slots;
    }

    private String nombreDiaEnEspanol(java.time.DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }

    public ProviderSearchResponseDTO buscarPorNombre(String name, int page, int size) {

        if (name == null || name.isBlank()) {
            throw new BadRequestException("Debe ingresar un nombre para la búsqueda");
        }
        if (page < 1) {
            throw new BadRequestException("El número de página debe ser mayor o igual a 1");
        }
        if (size < 1 || size > 50) {
            throw new BadRequestException("El tamaño de página debe estar entre 1 y 50");
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Provider> pageResult = providerRepository
                .findByFullNameContainingIgnoreCase(name.trim(), pageable);

        if (pageResult.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron proveedores con ese nombre");
        }

        List<ProviderSearchResponseDTO.ProviderSummaryDTO> summaries = pageResult
                .getContent()
                .stream()
                .map(this::toSummaryDTO)
                .toList();

        ProviderSearchResponseDTO.PaginationDTO pagination =
                new ProviderSearchResponseDTO.PaginationDTO(
                        page,
                        pageResult.getTotalPages(),
                        pageResult.getTotalElements()
                );

        return new ProviderSearchResponseDTO(summaries, pagination);
    }

    private ProviderSearchResponseDTO.ProviderSummaryDTO toSummaryDTO(Provider provider) {
        ProviderSearchResponseDTO.AddressDTO address =
                new ProviderSearchResponseDTO.AddressDTO(
                        provider.getDistrict(),
                        provider.getCity()
                );

        return new ProviderSearchResponseDTO.ProviderSummaryDTO(
                provider.getId(),
                provider.getFullName(),
                provider.getSpecialty(),
                provider.getPricePerAppointment(),
                provider.getAverageRating(),
                provider.getExperienceYears(),
                address
        );
    }
}