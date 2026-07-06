package com.upc.comparasalud.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesDTO {

    private Long id;
    private String idioma;
    private String zonaHoraria;
    private String formatoFecha;
    private String formatoHora;

    private Boolean notifCorreo;
    private Boolean notifPush;
    private Boolean notifSms;

    private Boolean notifRecordatoriosCitas;
    private Boolean notifNuevosMensajes;
    private Boolean notifActualizacionesSistema;
    private Boolean notifCorreosMarketing;
    private Boolean notifReportesSemanales;

    private String noMolestarDesde;
    private String noMolestarHasta;
}
