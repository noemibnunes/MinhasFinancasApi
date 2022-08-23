package udemyexercicio.minhasFinancias.service.implementacao;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import udemyexercicio.minhasFinancias.exception.AutenticacaoException;
import udemyexercicio.minhasFinancias.exception.RegraNegocioException;
import udemyexercicio.minhasFinancias.model.entity.Usuario;
import udemyexercicio.minhasFinancias.model.repository.UsuarioRepository;
import udemyexercicio.minhasFinancias.service.IUsuarioService;

@Service
public class UsuarioService implements IUsuarioService {
	
	@Autowired
	private UsuarioRepository repository;
	
	private PasswordEncoder encoder;
	
	public UsuarioService(UsuarioRepository repository, PasswordEncoder encoder) {
		this.repository = repository;
		this.encoder = encoder;
	}
	
	@Override
	public Usuario autenticarUsuario(String email, String senha) {
		
		Optional<Usuario> usuario = repository.findByEmail(email); 
		
		if(!usuario.isPresent()) {
			throw new AutenticacaoException("Usuário não encontrado para o email informado!");
		}
		
		boolean senhasIguais = encoder.matches(senha, usuario.get().getSenha());
		
		if(!senhasIguais){
			throw new AutenticacaoException("Senha Inválida!");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario cadastrarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		criptografarSenha(usuario);
		return repository.save(usuario);
	}

	private void criptografarSenha(Usuario usuario) { //alt+shift+m - cria um metodo
		String senha = usuario.getSenha();
		String senhaCripto = encoder.encode(senha);
		usuario.setSenha(senhaCripto);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		
		if(existe) {
			throw new RegraNegocioException("Email já cadastrado!");
		}
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		return repository.findById(id);
	}
	
}
