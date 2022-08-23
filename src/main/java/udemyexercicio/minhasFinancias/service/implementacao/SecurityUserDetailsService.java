package udemyexercicio.minhasFinancias.service.implementacao;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import udemyexercicio.minhasFinancias.model.entity.Usuario;
import udemyexercicio.minhasFinancias.model.repository.UsuarioRepository;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

	private UsuarioRepository usuarioRepository;
	
	public SecurityUserDetailsService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		//codigo para pegar o user id
		Usuario userEncontrado = usuarioRepository
									.findByEmail(email)
									.orElseThrow(() -> new UsernameNotFoundException("Email não cadastrado!"));
		
		return User.builder()
						.username(userEncontrado.getNome())
						.password(userEncontrado.getSenha())
						.roles("USER")
						.build();
		
	}

}
