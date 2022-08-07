package udemyexercicio.minhasFinancias.service.implementacao;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	public UsuarioService(UsuarioRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public Usuario autenticarUsuario(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email); 
		
		if(!usuario.isPresent()) {
			throw new AutenticacaoException("Usuário não encontrado para o email informado!");
		}
		
		if(!usuario.get().getSenha().equals(senha)){
			throw new AutenticacaoException("Senha Inválida!");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario cadastrarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
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
