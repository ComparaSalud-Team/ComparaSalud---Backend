package com.upc.comparasalud.dtos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChangePasswordRequestDTO {
    private Long authUserId;
    private String currentPassword;
    private String newPassword;
}
