package cl.Proyecto.Usuarios.dto;

import cl.Proyecto.Usuarios.model.Rol;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambioRolDTO {

    @NotNull(message = "El rol es obligatorio (CLIENTE, VENDEDOR o ADMIN)")
    private Rol rol;
}
