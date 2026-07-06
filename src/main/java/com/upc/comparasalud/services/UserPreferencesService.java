package com.upc.comparasalud.services;

import com.upc.comparasalud.dtos.UserPreferencesDTO;
import com.upc.comparasalud.entidades.AuthUser;
import com.upc.comparasalud.entidades.UserPreferences;
import com.upc.comparasalud.exceptions.ForbiddenException;
import com.upc.comparasalud.exceptions.ResourceNotFoundException;
import com.upc.comparasalud.repositorios.AuthUserRepository;
import com.upc.comparasalud.repositorios.UserPreferencesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPreferencesService {

    @Autowired private UserPreferencesRepository preferencesRepository;
    @Autowired private AuthUserRepository authUserRepository;

    // Evita que un usuario autenticado lea/edite las preferencias de otro
    // con solo cambiar el authUserId en la URL.
    private void verificarPropietario(Long authUserId, String callerEmail) {
        AuthUser user = authUserRepository.findById(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        if (!user.getEmail().equals(callerEmail)) {
            throw new ForbiddenException("No tienes permiso para acceder a estas preferencias");
        }
    }

    public UserPreferencesDTO obtener(Long authUserId, String callerEmail) {
        verificarPropietario(authUserId, callerEmail);
        UserPreferences prefs = preferencesRepository.findByAuthUserId(authUserId)
                .orElseGet(() -> crearDefecto(authUserId));
        return toDTO(prefs);
    }

    @Transactional
    public UserPreferencesDTO guardar(Long authUserId, UserPreferencesDTO dto, String callerEmail) {
        verificarPropietario(authUserId, callerEmail);
        UserPreferences prefs = preferencesRepository.findByAuthUserId(authUserId)
                .orElseGet(() -> crearDefecto(authUserId));

        prefs.setIdioma(dto.getIdioma());
        prefs.setZonaHoraria(dto.getZonaHoraria());
        prefs.setFormatoFecha(dto.getFormatoFecha());
        prefs.setFormatoHora(dto.getFormatoHora());

        if (dto.getNotifCorreo() != null)         prefs.setNotifCorreo(dto.getNotifCorreo());
        if (dto.getNotifPush() != null)           prefs.setNotifPush(dto.getNotifPush());
        if (dto.getNotifSms() != null)            prefs.setNotifSms(dto.getNotifSms());

        if (dto.getNotifRecordatoriosCitas() != null)   prefs.setNotifRecordatoriosCitas(dto.getNotifRecordatoriosCitas());
        if (dto.getNotifNuevosMensajes() != null)       prefs.setNotifNuevosMensajes(dto.getNotifNuevosMensajes());
        if (dto.getNotifActualizacionesSistema() != null) prefs.setNotifActualizacionesSistema(dto.getNotifActualizacionesSistema());
        if (dto.getNotifCorreosMarketing() != null)     prefs.setNotifCorreosMarketing(dto.getNotifCorreosMarketing());
        if (dto.getNotifReportesSemanales() != null)    prefs.setNotifReportesSemanales(dto.getNotifReportesSemanales());

        prefs.setNoMolestarDesde(dto.getNoMolestarDesde());
        prefs.setNoMolestarHasta(dto.getNoMolestarHasta());

        return toDTO(preferencesRepository.save(prefs));
    }

    @Transactional
    public UserPreferencesDTO restablecerDefecto(Long authUserId, String callerEmail) {
        verificarPropietario(authUserId, callerEmail);

        UserPreferences prefs = preferencesRepository.findByAuthUserId(authUserId)
                .orElseGet(() -> {
                    AuthUser user = authUserRepository.findById(authUserId)
                            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
                    UserPreferences nuevo = new UserPreferences();
                    nuevo.setAuthUser(user);
                    return nuevo;
                });

        prefs.setIdioma("Español (España)");
        prefs.setZonaHoraria("America/Lima");
        prefs.setFormatoFecha("DD/MM/YYYY");
        prefs.setFormatoHora("24h");
        prefs.setNotifCorreo(true);
        prefs.setNotifPush(true);
        prefs.setNotifSms(false);
        prefs.setNotifRecordatoriosCitas(true);
        prefs.setNotifNuevosMensajes(true);
        prefs.setNotifActualizacionesSistema(false);
        prefs.setNotifCorreosMarketing(false);
        prefs.setNotifReportesSemanales(true);
        prefs.setNoMolestarDesde(null);
        prefs.setNoMolestarHasta(null);

        return toDTO(preferencesRepository.save(prefs));
    }

    private UserPreferences crearDefecto(Long authUserId) {
        AuthUser user = authUserRepository.findById(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        UserPreferences prefs = new UserPreferences();
        prefs.setAuthUser(user);
        prefs.setIdioma("Español (España)");
        prefs.setZonaHoraria("America/Lima");
        prefs.setFormatoFecha("DD/MM/YYYY");
        prefs.setFormatoHora("24h");
        prefs.setNotifCorreo(true);
        prefs.setNotifPush(true);
        prefs.setNotifSms(false);
        prefs.setNotifRecordatoriosCitas(true);
        prefs.setNotifNuevosMensajes(true);
        prefs.setNotifActualizacionesSistema(false);
        prefs.setNotifCorreosMarketing(false);
        prefs.setNotifReportesSemanales(true);
        return preferencesRepository.save(prefs);
    }

    private UserPreferencesDTO toDTO(UserPreferences p) {
        UserPreferencesDTO dto = new UserPreferencesDTO();
        dto.setId(p.getId());
        dto.setIdioma(p.getIdioma());
        dto.setZonaHoraria(p.getZonaHoraria());
        dto.setFormatoFecha(p.getFormatoFecha());
        dto.setFormatoHora(p.getFormatoHora());
        dto.setNotifCorreo(p.getNotifCorreo());
        dto.setNotifPush(p.getNotifPush());
        dto.setNotifSms(p.getNotifSms());
        dto.setNotifRecordatoriosCitas(p.getNotifRecordatoriosCitas());
        dto.setNotifNuevosMensajes(p.getNotifNuevosMensajes());
        dto.setNotifActualizacionesSistema(p.getNotifActualizacionesSistema());
        dto.setNotifCorreosMarketing(p.getNotifCorreosMarketing());
        dto.setNotifReportesSemanales(p.getNotifReportesSemanales());
        dto.setNoMolestarDesde(p.getNoMolestarDesde());
        dto.setNoMolestarHasta(p.getNoMolestarHasta());
        return dto;
    }
}