package com.upc.comparasalud.dtos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String role;
    private Long userId;
    private String email;
}