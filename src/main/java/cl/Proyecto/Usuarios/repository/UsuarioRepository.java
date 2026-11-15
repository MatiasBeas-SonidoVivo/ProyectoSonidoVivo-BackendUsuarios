package cl.Proyecto.Usuarios.repository;

import cl.Proyecto.Usuarios.model.Rol;
import cl.Proyecto.Usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRol(Rol rol);

    List<Usuario> findByRolOrderByIdAsc(Rol rol);

    List<Usuario> findAllByOrderByIdAsc();
}
