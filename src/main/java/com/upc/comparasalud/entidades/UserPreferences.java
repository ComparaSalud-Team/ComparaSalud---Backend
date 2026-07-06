package com.upc.comparasalud.entidades;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "auth_user_id", nullable = false, unique = true)
    private AuthUser authUser;

    private String idioma = "Español (España)";
    private String zonaHoraria = "America/Lima";

    private String formatoFecha = "DD/MM/YYYY";
    private String formatoHora = "24h";

    private Boolean notifCorreo = true;
    private Boolean notifPush = true;
    private Boolean notifSms = false;

    private Boolean notifRecordatoriosCitas = true;
    private Boolean notifNuevosMensajes = true;
    private Boolean notifActualizacionesSistema = false;
    private Boolean notifCorreosMarketing = false;
    private Boolean notifReportesSemanales = true;

    private String noMolestarDesde;
    private String noMolestarHasta;
}
