package udemyexercicio.minhasFinancias.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import udemyexercicio.minhasFinancias.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	// SELECT * FROM USUARIO WHERE EXISTS (email)
	boolean existsByEmail(String email);
	
	Optional<Usuario> findByEmail(String email); 

}
