package com.upc.comparasalud.dtos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChangeEmailRequestDTO {
    private Long authUserId;
    private String currentPassword;
    private String newEmail;
}
