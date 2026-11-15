package cl.Proyecto.Usuarios.service;

import cl.Proyecto.Usuarios.dto.AuthResponseDTO;
import cl.Proyecto.Usuarios.dto.LoginRequestDTO;
import cl.Proyecto.Usuarios.dto.RegistroRequestDTO;
import cl.Proyecto.Usuarios.exception.DuplicateResourceException;
import cl.Proyecto.Usuarios.exception.ForbiddenException;
import cl.Proyecto.Usuarios.exception.UnauthorizedException;
import cl.Proyecto.Usuarios.mapper.UsuarioMapper;
import cl.Proyecto.Usuarios.model.Rol;
import cl.Proyecto.Usuarios.model.Usuario;
import cl.Proyecto.Usuarios.repository.UsuarioRepository;
import cl.Proyecto.Usuarios.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    // Mismo mensaje para correo inexistente y clave incorrecta: no revela qué correos están registrados.
    private static final String CREDENCIALES_INVALIDAS = "Correo o contraseña incorrectos";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /** El registro público SIEMPRE crea un CLIENTE; los demás roles los asigna un ADMIN. */
    @Transactional
    public AuthResponseDTO registrar(RegistroRequestDTO dto) {
        String email = normalizarEmail(dto.getEmail());
        if (usuarioRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Ya existe una cuenta con ese correo");
        }

        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre().trim())
                .apellido(limpiarOpcional(dto.getApellido()))
                .email(email)
                .telefono(limpiarOpcional(dto.getTelefono()))
                .password(passwordEncoder.encode(dto.getPassword()))
                .rol(Rol.CLIENTE)
                .activo(true)
                .build();

        return construirRespuesta(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(normalizarEmail(dto.getEmail()))
                .orElseThrow(() -> new UnauthorizedException(CREDENCIALES_INVALIDAS));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new UnauthorizedException(CREDENCIALES_INVALIDAS);
        }
        if (!usuario.isActivo()) {
            throw new ForbiddenException("La cuenta está desactivada");
        }
        return construirRespuesta(usuario);
    }

    private AuthResponseDTO construirRespuesta(Usuario usuario) {
        return AuthResponseDTO.builder()
                .token(jwtService.generarToken(usuario))
                .tipo("Bearer")
                .usuario(UsuarioMapper.toDTO(usuario))
                .build();
    }

    static String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }

    static String limpiarOpcional(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}
