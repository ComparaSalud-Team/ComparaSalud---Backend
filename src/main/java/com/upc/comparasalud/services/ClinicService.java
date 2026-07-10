package com.upc.comparasalud.services;

import com.upc.comparasalud.dtos.ClinicDTO;
import com.upc.comparasalud.dtos.ClinicDashboardDTO;
import com.upc.comparasalud.dtos.ClinicMetricsDTO;
import com.upc.comparasalud.dtos.ClinicRecentReviewDTO;
import com.upc.comparasalud.dtos.ClinicRevenueChartDTO;
import com.upc.comparasalud.dtos.ClinicRevenuePointDTO;
import com.upc.comparasalud.dtos.ClinicSpecialtyPriceDTO;
import com.upc.comparasalud.dtos.ClinicStatsDTO;
import com.upc.comparasalud.dtos.ClinicTeamMemberDTO;
import com.upc.comparasalud.dtos.ClinicUpcomingAppointmentDTO;
import com.upc.comparasalud.dtos.DepartmentDTO;
import com.upc.comparasalud.dtos.MedicalServiceDTO;
import com.upc.comparasalud.dtos.ProviderDTO;
import com.upc.comparasalud.entidades.Appointment;
import com.upc.comparasalud.entidades.Clinic;
import com.upc.comparasalud.entidades.ClinicSpecialtyPrice;
import com.upc.comparasalud.entidades.ClinicStats;
import com.upc.comparasalud.entidades.Department;
import com.upc.comparasalud.entidades.Provider;
import com.upc.comparasalud.exceptions.BadRequestException;
import com.upc.comparasalud.exceptions.ResourceNotFoundException;
import com.upc.comparasalud.repositorios.AppointmentRepository;
import com.upc.comparasalud.repositorios.ClinicRepository;
import com.upc.comparasalud.repositorios.ClinicSpecialtyPriceRepository;
import com.upc.comparasalud.repositorios.ClinicStatsRepository;
import com.upc.comparasalud.repositorios.DepartmentRepository;
import com.upc.comparasalud.repositorios.ProviderRepository;
import com.upc.comparasalud.repositorios.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ClinicService {

    private static final String[] MESES_ES = {
            "enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
    };

    @Autowired private ClinicRepository clinicRepository;
    @Autowired private ProviderRepository providerRepository;
    @Autowired private MedicalServiceService medicalServiceService;
    @Autowired private ClinicSpecialtyPriceRepository clinicSpecialtyPriceRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private ClinicStatsRepository clinicStatsRepository;
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private ReviewRepository reviewRepository;

    @Transactional
    public ClinicDTO crear(ClinicDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BadRequestException("El nombre de la clínica es obligatorio");
        }
        if (clinicRepository.existsByName(dto.getName())) {
            throw new BadRequestException("Ya existe una clínica con ese nombre");
        }

        Clinic clinic = new Clinic();
        clinic.setName(dto.getName());
        clinic.setDescription(dto.getDescription());
        clinic.setPhone(dto.getPhone());
        clinic.setEmail(dto.getEmail());
        clinic.setStreet(dto.getStreet());
        clinic.setDistrict(dto.getDistrict());
        clinic.setCity(dto.getCity());
        clinic.setCountry(dto.getCountry());
        clinic.setIsActive(true);
        clinic.setRating(dto.getRating() != null ? dto.getRating() : BigDecimal.ZERO);
        clinic.setReviewCount(dto.getReviewCount() != null ? dto.getReviewCount() : 0);
        clinic.setEmergencia24h(bool(dto.getEmergencia24h()));
        clinic.setEstacionamiento(bool(dto.getEstacionamiento()));
        clinic.setFarmacia(bool(dto.getFarmacia()));
        clinic.setLaboratorio(bool(dto.getLaboratorio()));
        clinic.setImagenologia(bool(dto.getImagenologia()));
        clinic.setServicioAmbulancia(bool(dto.getServicioAmbulancia()));
        clinic.setUnidadCuidadosIntensivos(bool(dto.getUnidadCuidadosIntensivos()));
        clinic.setHospitalizacion(bool(dto.getHospitalizacion()));

        clinic.setClinicType(dto.getClinicType());
        clinic.setFoundedYear(dto.getFoundedYear());
        clinic.setBedsCount(dto.getBedsCount());
        clinic.setEmergencyPhone(dto.getEmergencyPhone());
        clinic.setWebsite(dto.getWebsite());
        clinic.setInsuranceAccepted(joinOrNull(dto.getInsuranceAccepted()));
        clinic.setCertifications(joinOrNull(dto.getCertifications()));
        clinic.setSchedule(joinOrNull(dto.getSchedule()));

        clinic = clinicRepository.save(clinic);
        return toDTO(clinic);
    }

    public List<ClinicDTO> listarTodas() {
        return clinicRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<ClinicDTO> listarActivas() {
        return clinicRepository.findByIsActiveTrue().stream().map(this::toDTO).toList();
    }

    public ClinicDTO buscarPorId(Long id) {
        return toDTO(obtenerOFallar(id));
    }

    @Transactional
    public ClinicDTO actualizar(Long id, ClinicDTO dto) {
        Clinic clinic = obtenerOFallar(id);

        clinic.setName(dto.getName());
        clinic.setDescription(dto.getDescription());
        clinic.setPhone(dto.getPhone());
        clinic.setEmail(dto.getEmail());
        clinic.setStreet(dto.getStreet());
        clinic.setDistrict(dto.getDistrict());
        clinic.setCity(dto.getCity());
        clinic.setCountry(dto.getCountry());
        if (dto.getIsActive() != null) {
            clinic.setIsActive(dto.getIsActive());
        }
        if (dto.getRating() != null) {
            clinic.setRating(dto.getRating());
        }
        if (dto.getReviewCount() != null) {
            clinic.setReviewCount(dto.getReviewCount());
        }
        if (dto.getEmergencia24h() != null) clinic.setEmergencia24h(dto.getEmergencia24h());
        if (dto.getEstacionamiento() != null) clinic.setEstacionamiento(dto.getEstacionamiento());
        if (dto.getFarmacia() != null) clinic.setFarmacia(dto.getFarmacia());
        if (dto.getLaboratorio() != null) clinic.setLaboratorio(dto.getLaboratorio());
        if (dto.getImagenologia() != null) clinic.setImagenologia(dto.getImagenologia());
        if (dto.getServicioAmbulancia() != null) clinic.setServicioAmbulancia(dto.getServicioAmbulancia());
        if (dto.getUnidadCuidadosIntensivos() != null) clinic.setUnidadCuidadosIntensivos(dto.getUnidadCuidadosIntensivos());
        if (dto.getHospitalizacion() != null) clinic.setHospitalizacion(dto.getHospitalizacion());

        if (dto.getClinicType() != null) clinic.setClinicType(dto.getClinicType());
        if (dto.getFoundedYear() != null) clinic.setFoundedYear(dto.getFoundedYear());
        if (dto.getBedsCount() != null) clinic.setBedsCount(dto.getBedsCount());
        if (dto.getEmergencyPhone() != null) clinic.setEmergencyPhone(dto.getEmergencyPhone());
        if (dto.getWebsite() != null) clinic.setWebsite(dto.getWebsite());
        if (dto.getInsuranceAccepted() != null) clinic.setInsuranceAccepted(joinOrNull(dto.getInsuranceAccepted()));
        if (dto.getCertifications() != null) clinic.setCertifications(joinOrNull(dto.getCertifications()));
        if (dto.getSchedule() != null) clinic.setSchedule(joinOrNull(dto.getSchedule()));

        clinic = clinicRepository.save(clinic);
        return toDTO(clinic);
    }

    @Transactional
    public void eliminar(Long id) {
        Clinic clinic = obtenerOFallar(id);
        clinicRepository.delete(clinic);
    }

    // HU nueva – El paciente ve los médicos que atienden en una clínica
    public List<ProviderDTO> proveedoresDeClinica(Long clinicId) {
        obtenerOFallar(clinicId); // valida que la clínica exista
        return providerRepository.findByClinics_Id(clinicId).stream()
                .map(this::toProviderDTO)
                .toList();
    }

    // HU nueva – El paciente ve los servicios de una clínica.
    public List<MedicalServiceDTO> serviciosDeClinica(Long clinicId) {
        obtenerOFallar(clinicId);
        return medicalServiceService.listarActivos();
    }

    // HU nueva – Comparador de proveedores: precios por especialidad de UNA clínica
    public List<ClinicSpecialtyPriceDTO> preciosDeClinica(Long clinicId) {
        obtenerOFallar(clinicId);
        return clinicSpecialtyPriceRepository.findByClinic_Id(clinicId).stream()
                .map(this::toPriceDTO)
                .toList();
    }

    // HU nueva – Comparador de precios: precios por especialidad de TODAS las
    // clínicas activas. El frontend agrupa por especialidad.
    public List<ClinicSpecialtyPriceDTO> compararPrecios() {
        return clinicSpecialtyPriceRepository.findByClinic_IsActiveTrue().stream()
                .map(this::toPriceDTO)
                .toList();
    }

    // Se ejecuta al registrar o editar un proveedor: garantiza que la
    // especialidad del proveedor tenga un precio cargado en cada clínica a la
    // que queda asociado, para que los comparadores reflejen datos reales sin
    // depender de una carga manual vía crearPrecio().
    @Transactional
    public void sincronizarPrecioDesdeProveedor(Provider provider) {
        if (provider.getSpecialty() == null || provider.getSpecialty().isBlank()) {
            return;
        }
        BigDecimal precio = provider.getPricePerAppointment() != null
                ? provider.getPricePerAppointment() : BigDecimal.ZERO;

        for (Clinic clinic : provider.getClinics()) {
            ClinicSpecialtyPrice entity = clinicSpecialtyPriceRepository
                    .findByClinic_IdAndSpecialtyIgnoreCase(clinic.getId(), provider.getSpecialty())
                    .orElseGet(() -> {
                        ClinicSpecialtyPrice nuevo = new ClinicSpecialtyPrice();
                        nuevo.setClinic(clinic);
                        nuevo.setSpecialty(provider.getSpecialty());
                        nuevo.setDurationMinutes(30);
                        nuevo.setIncludes(List.of("Consulta médica"));
                        return nuevo;
                    });
            entity.setPrice(precio);
            clinicSpecialtyPriceRepository.save(entity);
        }
    }

    // Admin/seed – cargar un precio de especialidad para una clínica, sin
    // necesidad de tocar la base de datos a mano.
    @Transactional
    public ClinicSpecialtyPriceDTO crearPrecio(Long clinicId, ClinicSpecialtyPriceDTO dto) {
        Clinic clinic = obtenerOFallar(clinicId);

        if (dto.getSpecialty() == null || dto.getSpecialty().isBlank()) {
            throw new BadRequestException("La especialidad es obligatoria");
        }
        if (dto.getPrice() == null) {
            throw new BadRequestException("El precio es obligatorio");
        }

        ClinicSpecialtyPrice entity = new ClinicSpecialtyPrice();
        entity.setClinic(clinic);
        entity.setSpecialty(dto.getSpecialty());
        entity.setPrice(dto.getPrice());
        entity.setDurationMinutes(dto.getDurationMinutes() != null ? dto.getDurationMinutes() : 50);
        entity.setIncludes(dto.getIncludes() != null ? dto.getIncludes() : List.of());

        entity = clinicSpecialtyPriceRepository.save(entity);
        return toPriceDTO(entity);
    }

    // ===================== Departamentos =====================

    public List<DepartmentDTO> departamentosDeClinica(Long clinicId) {
        obtenerOFallar(clinicId);
        return departmentRepository.findByClinic_Id(clinicId).stream()
                .map(this::toDepartmentDTO)
                .toList();
    }

    @Transactional
    public DepartmentDTO crearDepartamento(Long clinicId, DepartmentDTO dto) {
        Clinic clinic = obtenerOFallar(clinicId);

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BadRequestException("El nombre del departamento es obligatorio");
        }
        if (dto.getCapacity() == null || dto.getCapacity() <= 0) {
            throw new BadRequestException("La capacidad debe ser mayor a 0");
        }

        Department department = new Department();
        department.setClinic(clinic);
        department.setName(dto.getName());
        department.setCapacity(dto.getCapacity());
        department.setCurrentPatients(dto.getCurrentPatients() != null ? dto.getCurrentPatients() : 0);

        department = departmentRepository.save(department);
        return toDepartmentDTO(department);
    }

    @Transactional
    public DepartmentDTO actualizarDepartamento(Long departmentId, DepartmentDTO dto) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado con ID: " + departmentId));

        if (dto.getName() != null) department.setName(dto.getName());
        if (dto.getCapacity() != null) department.setCapacity(dto.getCapacity());
        if (dto.getCurrentPatients() != null) department.setCurrentPatients(dto.getCurrentPatients());

        department = departmentRepository.save(department);
        return toDepartmentDTO(department);
    }

    @Transactional
    public void eliminarDepartamento(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Departamento no encontrado con ID: " + departmentId);
        }
        departmentRepository.deleteById(departmentId);
    }

    // ===================== Estadísticas =====================

    // Devuelve las estadísticas de la clínica; si aún no existe un registro,
    // devuelve valores en cero en vez de fallar (para que el dashboard no rompa).
    public ClinicStatsDTO estadisticasDeClinica(Long clinicId) {
        obtenerOFallar(clinicId);
        return clinicStatsRepository.findByClinic_Id(clinicId)
                .map(this::toStatsDTO)
                .orElseGet(() -> {
                    ClinicStatsDTO vacio = new ClinicStatsDTO();
                    vacio.setClinicId(clinicId);
                    vacio.setBedOccupancyPct(BigDecimal.ZERO);
                    vacio.setSurgeriesCompletedPct(BigDecimal.ZERO);
                    vacio.setStaffAvailablePct(BigDecimal.ZERO);
                    vacio.setSatisfactionPct(BigDecimal.ZERO);
                    return vacio;
                });
    }

    @Transactional
    public ClinicStatsDTO actualizarEstadisticas(Long clinicId, ClinicStatsDTO dto) {
        Clinic clinic = obtenerOFallar(clinicId);

        ClinicStats stats = clinicStatsRepository.findByClinic_Id(clinicId)
                .orElseGet(() -> {
                    ClinicStats nuevo = new ClinicStats();
                    nuevo.setClinic(clinic);
                    return nuevo;
                });

        if (dto.getBedOccupancyPct() != null) stats.setBedOccupancyPct(dto.getBedOccupancyPct());
        if (dto.getSurgeriesCompletedPct() != null) stats.setSurgeriesCompletedPct(dto.getSurgeriesCompletedPct());
        if (dto.getStaffAvailablePct() != null) stats.setStaffAvailablePct(dto.getStaffAvailablePct());
        if (dto.getSatisfactionPct() != null) stats.setSatisfactionPct(dto.getSatisfactionPct());
        stats.setUpdatedAt(LocalDateTime.now());

        stats = clinicStatsRepository.save(stats);
        return toStatsDTO(stats);
    }
    public ClinicDTO obtenerPorEmail(String email) {
        Clinic clinic = clinicRepository.findByAuthUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Clínica no encontrada para este usuario"));
        return toDTO(clinic);
    }

    public ClinicDashboardDTO dashboardDeClinica(Long clinicId) {
        Clinic clinic = obtenerOFallar(clinicId);

        LocalDate hoy = LocalDate.now();
        YearMonth mesActual = YearMonth.from(hoy);
        YearMonth mesAnterior = mesActual.minusMonths(1);
        LocalDate inicioMes = mesActual.atDay(1);
        LocalDate finMes = mesActual.atEndOfMonth();
        LocalDate inicioMesAnterior = mesAnterior.atDay(1);
        LocalDate finMesAnterior = mesAnterior.atEndOfMonth();

        ClinicDashboardDTO dashboard = new ClinicDashboardDTO();
        dashboard.setMetrics(buildMetrics(clinic, hoy, inicioMes, finMes, inicioMesAnterior, finMesAnterior));
        dashboard.setUpcomingAppointments(buildUpcomingAppointments(clinicId, hoy));
        dashboard.setMedicalTeam(buildMedicalTeam(clinicId));
        dashboard.setDepartments(departamentosDeClinica(clinicId));
        dashboard.setStats(estadisticasDeClinica(clinicId));
        dashboard.setRevenueChart(buildRevenueChart(clinicId, mesActual, inicioMes, finMes, inicioMesAnterior, finMesAnterior));
        dashboard.setRecentReviews(buildRecentReviews(clinicId));
        return dashboard;
    }

    private ClinicMetricsDTO buildMetrics(Clinic clinic, LocalDate hoy,
                                          LocalDate inicioMes, LocalDate finMes,
                                          LocalDate inicioMesAnterior, LocalDate finMesAnterior) {
        long citasHoy = appointmentRepository.countByClinicAndDate(clinic.getId(), hoy);

        BigDecimal ingresosMes = appointmentRepository.sumAmountPaidByClinicAndDateRange(clinic.getId(), inicioMes, finMes);
        BigDecimal ingresosMesAnterior = appointmentRepository.sumAmountPaidByClinicAndDateRange(clinic.getId(), inicioMesAnterior, finMesAnterior);

        long pacientesMes = appointmentRepository.countDistinctPatientsByClinicAndDateRange(clinic.getId(), inicioMes, finMes);
        long pacientesMesAnterior = appointmentRepository.countDistinctPatientsByClinicAndDateRange(clinic.getId(), inicioMesAnterior, finMesAnterior);

        ClinicMetricsDTO metrics = new ClinicMetricsDTO();
        metrics.setDailyAppointments(citasHoy);
        metrics.setDailyAppointmentsDeltaPct(null); // sin base histórica diaria confiable todavía
        metrics.setMonthlyEarnings(ingresosMes);
        metrics.setEarningsDeltaPct(computeDeltaPct(ingresosMes, ingresosMesAnterior));
        metrics.setNewPatientsThisMonth(pacientesMes);
        metrics.setNewPatientsDeltaPct(computeDeltaPct(BigDecimal.valueOf(pacientesMes), BigDecimal.valueOf(pacientesMesAnterior)));
        metrics.setAverageRating(clinic.getRating());
        metrics.setReviewsCount(clinic.getReviewCount());
        return metrics;
    }

    private List<ClinicUpcomingAppointmentDTO> buildUpcomingAppointments(Long clinicId, LocalDate hoy) {
        return appointmentRepository.findUpcomingByClinicId(clinicId, hoy).stream()
                .limit(5)
                .map(a -> {
                    ClinicUpcomingAppointmentDTO dto = new ClinicUpcomingAppointmentDTO();
                    dto.setPatientName(a.getPatient() != null ? a.getPatient().getName() : "Paciente");
                    dto.setTime(a.getStartTime() != null ? a.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm a")) : "");
                    dto.setDoctorName(a.getProvider() != null ? a.getProvider().getFullName() : "");
                    dto.setSpecialty(a.getProvider() != null ? a.getProvider().getSpecialty() : "");
                    dto.setStatus(a.getStatus());
                    return dto;
                })
                .toList();
    }

    private List<ClinicTeamMemberDTO> buildMedicalTeam(Long clinicId) {
        return providerRepository.findByClinics_Id(clinicId).stream()
                .map(p -> {
                    ClinicTeamMemberDTO dto = new ClinicTeamMemberDTO();
                    dto.setName(p.getFullName());
                    dto.setSpecialty(p.getSpecialty());
                    dto.setPhotoUrl(p.getAuthUser() != null ? p.getAuthUser().getProfilePhotoUrl() : null);
                    dto.setOnline(false); // no hay tabla de presencia/sesión activa por ahora
                    return dto;
                })
                .toList();
    }

    private ClinicRevenueChartDTO buildRevenueChart(Long clinicId, YearMonth mesActual,
                                                    LocalDate inicioMes, LocalDate finMes,
                                                    LocalDate inicioMesAnterior, LocalDate finMesAnterior) {
        List<Appointment> citasDelMes = appointmentRepository.findByClinicAndDateRange(clinicId, inicioMes, finMes);

        Map<LocalDate, BigDecimal> montosPorDia = new LinkedHashMap<>();
        for (LocalDate d = inicioMes; !d.isAfter(finMes); d = d.plusDays(1)) {
            montosPorDia.put(d, BigDecimal.ZERO);
        }
        for (Appointment a : citasDelMes) {
            BigDecimal monto = a.getAmountPaid() != null ? a.getAmountPaid() : BigDecimal.ZERO;
            montosPorDia.merge(a.getDate(), monto, BigDecimal::add);
        }

        List<ClinicRevenuePointDTO> points = montosPorDia.entrySet().stream()
                .map(e -> new ClinicRevenuePointDTO(String.valueOf(e.getKey().getDayOfMonth()), e.getValue()))
                .toList();

        BigDecimal total = appointmentRepository.sumAmountPaidByClinicAndDateRange(clinicId, inicioMes, finMes);
        BigDecimal totalAnterior = appointmentRepository.sumAmountPaidByClinicAndDateRange(clinicId, inicioMesAnterior, finMesAnterior);

        ClinicRevenueChartDTO chart = new ClinicRevenueChartDTO();
        chart.setMonthLabel(capitalizar(MESES_ES[mesActual.getMonthValue() - 1]) + " " + mesActual.getYear());
        chart.setTotal(total);
        chart.setDeltaVsPreviousMonthPct(computeDeltaPct(total, totalAnterior));
        chart.setPoints(points);
        return chart;
    }

    private List<ClinicRecentReviewDTO> buildRecentReviews(Long clinicId) {
        LocalDateTime ahora = LocalDateTime.now();
        return reviewRepository.findByClinicIdOrderByCreatedAtDesc(clinicId).stream()
                .limit(5)
                .map(r -> {
                    ClinicRecentReviewDTO dto = new ClinicRecentReviewDTO();
                    dto.setPatientName(r.getPatient() != null ? r.getPatient().getName() : "Paciente");
                    dto.setRating(r.getRating());
                    dto.setComment(r.getComment());
                    dto.setRelativeDate(formatearFechaRelativa(r.getCreatedAt(), ahora));
                    return dto;
                })
                .toList();
    }

    private BigDecimal computeDeltaPct(BigDecimal actual, BigDecimal anterior) {
        if (actual == null || anterior == null || anterior.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return actual.subtract(anterior)
                .divide(anterior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) return texto;
        return texto.substring(0, 1).toUpperCase(Locale.ROOT) + texto.substring(1);
    }

    private String formatearFechaRelativa(LocalDateTime fecha, LocalDateTime ahora) {
        if (fecha == null) return "";
        Duration diff = Duration.between(fecha, ahora);
        long minutos = diff.toMinutes();
        if (minutos < 60) return "Hace " + Math.max(minutos, 1) + (minutos == 1 ? " minuto" : " minutos");
        long horas = diff.toHours();
        if (horas < 24) return "Hace " + horas + (horas == 1 ? " hora" : " horas");
        long dias = diff.toDays();
        if (dias < 30) return "Hace " + dias + (dias == 1 ? " día" : " días");
        long meses = dias / 30;
        return "Hace " + meses + (meses == 1 ? " mes" : " meses");
    }

    private DepartmentDTO toDepartmentDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setClinicId(department.getClinic().getId());
        dto.setName(department.getName());
        dto.setCurrentPatients(department.getCurrentPatients());
        dto.setCapacity(department.getCapacity());
        return dto;
    }

    private ClinicStatsDTO toStatsDTO(ClinicStats stats) {
        ClinicStatsDTO dto = new ClinicStatsDTO();
        dto.setClinicId(stats.getClinic().getId());
        dto.setBedOccupancyPct(stats.getBedOccupancyPct());
        dto.setSurgeriesCompletedPct(stats.getSurgeriesCompletedPct());
        dto.setStaffAvailablePct(stats.getStaffAvailablePct());
        dto.setSatisfactionPct(stats.getSatisfactionPct());
        return dto;
    }

    private ClinicSpecialtyPriceDTO toPriceDTO(ClinicSpecialtyPrice entity) {
        ClinicSpecialtyPriceDTO dto = new ClinicSpecialtyPriceDTO();
        dto.setId(entity.getId());
        dto.setClinicId(entity.getClinic().getId());
        dto.setClinicName(entity.getClinic().getName());
        dto.setDistrict(entity.getClinic().getDistrict());
        dto.setSpecialty(entity.getSpecialty());
        dto.setPrice(entity.getPrice());
        dto.setDurationMinutes(entity.getDurationMinutes());
        dto.setIncludes(entity.getIncludes());
        return dto;
    }

    private Boolean bool(Boolean value) {
        return value != null && value;
    }

    private Clinic obtenerOFallar(Long id) {
        return clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clínica no encontrada con ID: " + id));
    }

    private ClinicDTO toDTO(Clinic clinic) {
        ClinicDTO dto = new ClinicDTO();
        dto.setId(clinic.getId());
        dto.setName(clinic.getName());
        dto.setRuc(clinic.getRuc());
        dto.setDescription(clinic.getDescription());
        dto.setPhone(clinic.getPhone());
        dto.setEmail(clinic.getEmail());
        dto.setStreet(clinic.getStreet());
        dto.setDistrict(clinic.getDistrict());
        dto.setCity(clinic.getCity());
        dto.setCountry(clinic.getCountry());
        dto.setIsActive(clinic.getIsActive());
        dto.setProviderCount(clinic.getProviders() != null ? clinic.getProviders().size() : 0);
        dto.setSpecialties(
                clinic.getProviders() == null ? List.of() :
                        clinic.getProviders().stream()
                        .map(Provider::getSpecialty)
                        .filter(s -> s != null && !s.isBlank())
                        .distinct()
                        .toList()
        );
        dto.setRating(clinic.getRating());
        dto.setReviewCount(clinic.getReviewCount());
        dto.setEmergencia24h(clinic.getEmergencia24h());
        dto.setEstacionamiento(clinic.getEstacionamiento());
        dto.setFarmacia(clinic.getFarmacia());
        dto.setLaboratorio(clinic.getLaboratorio());
        dto.setImagenologia(clinic.getImagenologia());
        dto.setServicioAmbulancia(clinic.getServicioAmbulancia());
        dto.setUnidadCuidadosIntensivos(clinic.getUnidadCuidadosIntensivos());
        dto.setHospitalizacion(clinic.getHospitalizacion());

        dto.setClinicType(clinic.getClinicType());
        dto.setFoundedYear(clinic.getFoundedYear());
        dto.setBedsCount(clinic.getBedsCount());
        dto.setEmergencyPhone(clinic.getEmergencyPhone());
        dto.setWebsite(clinic.getWebsite());
        dto.setInsuranceAccepted(splitOrEmpty(clinic.getInsuranceAccepted()));
        dto.setCertifications(splitOrEmpty(clinic.getCertifications()));
        dto.setSchedule(splitOrEmpty(clinic.getSchedule()));

        return dto;
    }

    private ProviderDTO toProviderDTO(Provider provider) {
        ProviderDTO dto = new ProviderDTO();
        dto.setId(provider.getId());
        dto.setAuthUserId(provider.getAuthUser().getId());
        dto.setFullName(provider.getFullName());
        dto.setEmail(provider.getAuthUser().getEmail());
        dto.setPhone(provider.getPhone());
        dto.setSpecialty(provider.getSpecialty());
        dto.setDescription(provider.getDescription());
        dto.setRating(provider.getRating());
        dto.setIsValidated(provider.getIsValidated());
        dto.setPricePerAppointment(provider.getPricePerAppointment());
        dto.setAverageRating(provider.getAverageRating());
        dto.setExperienceYears(provider.getExperienceYears());
        dto.setStreet(provider.getStreet());
        dto.setDistrict(provider.getDistrict());
        dto.setCity(provider.getCity());
        dto.setCountry(provider.getCountry());
        dto.setLanguage(provider.getLanguage());
        dto.setModality(provider.getModality());
        dto.setDurationMinutes(provider.getDurationMinutes());
        dto.setPhotoUrl(provider.getAuthUser().getProfilePhotoUrl());
        dto.setClinicIds(provider.getClinics().stream().map(Clinic::getId).toList());
        dto.setClinicNames(provider.getClinics().stream().map(Clinic::getName).toList());
        return dto;
    }
    private List<String> splitOrEmpty(String value) {
        if (value == null || value.isBlank()) return List.of();
        return java.util.Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private String joinOrNull(List<String> list) {
        return (list == null || list.isEmpty()) ? null : String.join(",", list);
    }
}