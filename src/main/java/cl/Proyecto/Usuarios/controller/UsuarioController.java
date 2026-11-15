package cl.Proyecto.Usuarios.controller;

import cl.Proyecto.Usuarios.dto.CambioPasswordDTO;
import cl.Proyecto.Usuarios.dto.CambioRolDTO;
import cl.Proyecto.Usuarios.dto.UsuarioResponseDTO;
import cl.Proyecto.Usuarios.dto.UsuarioUpdateDTO;
import cl.Proyecto.Usuarios.model.Rol;
import cl.Proyecto.Usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Perfil propio y administración de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // ---------- Perfil propio (cualquier usuario logueado) ----------

    @Operation(summary = "Ver mi perfil")
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> miPerfil(Authentication auth) {
        return ResponseEntity.ok(usuarioService.obtenerPerfil(auth.getName()));
    }

    @Operation(summary = "Actualizar mi perfil (nombre, apellido, teléfono)")
    @PutMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> actualizarMiPerfil(Authentication auth,
                                                                 @Valid @RequestBody UsuarioUpdateDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(auth.getName(), dto));
    }

    @Operation(summary = "Cambiar mi contraseña")
    @PatchMapping("/me/password")
    public ResponseEntity<Void> cambiarPassword(Authentication auth, @Valid @RequestBody CambioPasswordDTO dto) {
        usuarioService.cambiarPassword(auth.getName(), dto);
        return ResponseEntity.noContent().build();
    }

    // ---------- Administración (solo ADMIN) ----------

    @Operation(summary = "Listar usuarios, opcionalmente filtrando por rol (Requerido: ADMIN)")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar(@RequestParam(required = false) Rol rol) {
        return ResponseEntity.ok(usuarioService.listar(rol));
    }

    @Operation(summary = "Obtener usuario por ID (Requerido: ADMIN)")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @Operation(summary = "Cambiar el rol de un usuario (Requerido: ADMIN)")
    @PatchMapping("/{id}/rol")
    public ResponseEntity<UsuarioResponseDTO> cambiarRol(@PathVariable Long id,
                                                         @Valid @RequestBody CambioRolDTO dto,
                                                         Authentication auth) {
        return ResponseEntity.ok(usuarioService.cambiarRol(id, dto.getRol(), auth.getName()));
    }

    @Operation(summary = "Activar o desactivar una cuenta (Requerido: ADMIN)")
    @PatchMapping("/{id}/activo")
    public ResponseEntity<UsuarioResponseDTO> cambiarActivo(@PathVariable Long id,
                                                            @RequestParam boolean activo,
                                                            Authentication auth) {
        return ResponseEntity.ok(usuarioService.cambiarActivo(id, activo, auth.getName()));
    }

    @Operation(summary = "Eliminar un usuario (Requerido: ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, Authentication auth) {
        usuarioService.eliminar(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
