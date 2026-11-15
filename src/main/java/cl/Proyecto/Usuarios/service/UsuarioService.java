package cl.Proyecto.Usuarios.service;

import cl.Proyecto.Usuarios.dto.CambioPasswordDTO;
import cl.Proyecto.Usuarios.dto.UsuarioResponseDTO;
import cl.Proyecto.Usuarios.dto.UsuarioUpdateDTO;
import cl.Proyecto.Usuarios.exception.ResourceNotFoundException;
import cl.Proyecto.Usuarios.mapper.UsuarioMapper;
import cl.Proyecto.Usuarios.model.Rol;
import cl.Proyecto.Usuarios.model.Usuario;
import cl.Proyecto.Usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // ---------- Perfil propio (cualquier usuario logueado) ----------

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPerfil(String email) {
        return UsuarioMapper.toDTO(buscarPorEmail(email));
    }

    @Transactional
    public UsuarioResponseDTO actualizarPerfil(String email, UsuarioUpdateDTO dto) {
        Usuario usuario = buscarPorEmail(email);
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(AuthService.limpiarOpcional(dto.getApellido()));
        usuario.setTelefono(AuthService.limpiarOpcional(dto.getTelefono()));
        return UsuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void cambiarPassword(String email, CambioPasswordDTO dto) {
        Usuario usuario = buscarPorEmail(email);
        if (!passwordEncoder.matches(dto.getPasswordActual(), usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        usuario.setPassword(passwordEncoder.encode(dto.getPasswordNueva()));
        usuarioRepository.save(usuario);
    }

    // ---------- Administración (solo ADMIN) ----------

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar(Rol rol) {
        List<Usuario> usuarios = (rol == null)
                ? usuarioRepository.findAllByOrderByIdAsc()
                : usuarioRepository.findByRolOrderByIdAsc(rol);
        return usuarios.stream().map(UsuarioMapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        return UsuarioMapper.toDTO(buscarPorId(id));
    }

    @Transactional
    public UsuarioResponseDTO cambiarRol(Long id, Rol nuevoRol, String emailAdmin) {
        Usuario usuario = buscarPorId(id);
        evitarAutoModificacion(usuario, emailAdmin, "No puedes cambiar tu propio rol");
        usuario.setRol(nuevoRol);
        return UsuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO cambiarActivo(Long id, boolean activo, String emailAdmin) {
        Usuario usuario = buscarPorId(id);
        evitarAutoModificacion(usuario, emailAdmin, "No puedes desactivar tu propia cuenta");
        usuario.setActivo(activo);
        return UsuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminar(Long id, String emailAdmin) {
        Usuario usuario = buscarPorId(id);
        evitarAutoModificacion(usuario, emailAdmin, "No puedes eliminar tu propia cuenta");
        usuarioRepository.delete(usuario);
    }

    // ---------- helpers ----------

    private Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el ID: " + id));
    }

    private Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    /** Evita que el último ADMIN se quite el acceso a sí mismo por accidente. */
    private void evitarAutoModificacion(Usuario objetivo, String emailAdmin, String mensaje) {
        if (objetivo.getEmail().equalsIgnoreCase(emailAdmin)) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}
