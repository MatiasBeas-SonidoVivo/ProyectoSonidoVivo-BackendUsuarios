package cl.Proyecto.Usuarios.mapper;

import cl.Proyecto.Usuarios.dto.UsuarioResponseDTO;
import cl.Proyecto.Usuarios.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static UsuarioResponseDTO toDTO(Usuario u) {
        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .email(u.getEmail())
                .telefono(u.getTelefono())
                .rol(u.getRol().name())
                .activo(u.isActivo())
                .fechaRegistro(u.getFechaRegistro())
                .build();
    }
}
