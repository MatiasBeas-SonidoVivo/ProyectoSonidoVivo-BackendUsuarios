package cl.Proyecto.Usuarios.config;

import cl.Proyecto.Usuarios.model.Rol;
import cl.Proyecto.Usuarios.model.Usuario;
import cl.Proyecto.Usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Crear Usuario Admin por defecto si no existe
        if (!usuarioRepository.existsByEmail("admin@sonidovivo.cl")) {
            Usuario admin = Usuario.builder()
                    .nombre("Administrador")
                    .email("admin@sonidovivo.cl")
                    .password(passwordEncoder.encode("admin123"))
                    .rol(Rol.ADMIN) //
                    .activo(true)
                    .build();

            usuarioRepository.save(admin);
            System.out.println(">>> Usuario ADMIN inicial creado exitosamente: admin@sonidovivo.cl / admin123");
        }

        // En tu DataInitializer.java del microservicio de Usuarios
        if (!usuarioRepository.existsByEmail("vendedor@sonidovivo.cl")) {
            Usuario vendedor = new Usuario();
            vendedor.setNombre("Carlos");
            vendedor.setApellido("Vendedor");
            vendedor.setEmail("vendedor@sonidovivo.cl");
            vendedor.setPassword(passwordEncoder.encode("vendedor123")); // o tu encoder
            vendedor.setRol(Rol.VENDEDOR); // o "ROLE_VENDEDOR"
            vendedor.setActivo(true);

            usuarioRepository.save(vendedor);
        }
    }


}