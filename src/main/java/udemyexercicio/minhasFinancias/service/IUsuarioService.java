package udemyexercicio.minhasFinancias.service;

import java.util.Optional;

import udemyexercicio.minhasFinancias.model.entity.Usuario;

public interface IUsuarioService {
	
	Usuario autenticarUsuario(String email, String senha);
	Usuario cadastrarUsuario(Usuario usuario);
	void validarEmail(String email);
	Optional<Usuario> obterPorId(Long id);
}
