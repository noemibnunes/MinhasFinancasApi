package udemyexercicio.minhasFinancias.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import udemyexercicio.minhasFinancias.model.entity.Usuario;

public interface IJwtService {
	
	String gerarToken(Usuario usuario);
	Claims obterClaims(String token) throws ExpiredJwtException;
	boolean isTokenValido(String token);
	String obterLoginUsuario(String token);

}
