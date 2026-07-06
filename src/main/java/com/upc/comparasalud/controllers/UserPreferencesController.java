package com.upc.comparasalud.controllers;

import com.upc.comparasalud.dtos.UserPreferencesDTO;
import com.upc.comparasalud.services.UserPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/preferences")
public class UserPreferencesController {

    @Autowired private UserPreferencesService preferencesService;

    @GetMapping("/{authUserId}")
    public ResponseEntity<UserPreferencesDTO> obtener(@PathVariable Long authUserId, Principal principal) {
        return ResponseEntity.ok(preferencesService.obtener(authUserId, principal.getName()));
    }

    @PutMapping("/{authUserId}")
    public ResponseEntity<UserPreferencesDTO> guardar(
            @PathVariable Long authUserId,
            @RequestBody UserPreferencesDTO dto,
            Principal principal) {
        return ResponseEntity.ok(preferencesService.guardar(authUserId, dto, principal.getName()));
    }

    @PostMapping("/{authUserId}/reset")
    public ResponseEntity<UserPreferencesDTO> restablecer(@PathVariable Long authUserId, Principal principal) {
        return ResponseEntity.ok(preferencesService.restablecerDefecto(authUserId, principal.getName()));
    }
}
