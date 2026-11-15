package cl.Proyecto.Usuarios.controller;

import cl.Proyecto.Usuarios.dto.AuthResponseDTO;
import cl.Proyecto.Usuarios.dto.LoginRequestDTO;
import cl.Proyecto.Usuarios.dto.RegistroRequestDTO;
import cl.Proyecto.Usuarios.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro e inicio de sesión")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar una cuenta nueva (Público). Siempre se crea con rol CLIENTE y devuelve el token.")
    @PostMapping("/registro")
    public ResponseEntity<AuthResponseDTO> registro(@Valid @RequestBody RegistroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(dto));
    }

    @Operation(summary = "Iniciar sesión (Público). Devuelve el token JWT y los datos del usuario.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
