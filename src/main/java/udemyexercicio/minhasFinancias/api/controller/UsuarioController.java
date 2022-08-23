package udemyexercicio.minhasFinancias.api.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import udemyexercicio.minhasFinancias.api.dto.TokenDTO;
import udemyexercicio.minhasFinancias.api.dto.UsuarioDTO;
import udemyexercicio.minhasFinancias.exception.AutenticacaoException;
import udemyexercicio.minhasFinancias.exception.RegraNegocioException;
import udemyexercicio.minhasFinancias.model.entity.Usuario;
import udemyexercicio.minhasFinancias.service.implementacao.JwtService;
import udemyexercicio.minhasFinancias.service.implementacao.LancamentoService;
import udemyexercicio.minhasFinancias.service.implementacao.UsuarioService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
public class UsuarioController {
	
	private final UsuarioService service;
	private final LancamentoService lancamentoService;
	private final JwtService jwtService;
	
	@PostMapping("/salvar")
	public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {
		Usuario user = Usuario.builder().nome(dto.getNome()).email(dto.getEmail()).senha(dto.getSenha()).build();
		
		try {
			Usuario userSalvo = service.cadastrarUsuario(user);
			return new ResponseEntity(userSalvo, HttpStatus.CREATED);
		}catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/autenticar")
	public ResponseEntity<?> autenticar(@RequestBody UsuarioDTO dto) {
		try {
			Usuario userAutenticado = service.autenticarUsuario(dto.getEmail(), dto.getSenha());
			String token = jwtService.gerarToken(userAutenticado);
			TokenDTO tokenDTO = new TokenDTO(userAutenticado.getNome(), token);
			
			return ResponseEntity.ok(tokenDTO);
		}catch (AutenticacaoException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/obterSaldo/{id}")
	public ResponseEntity obterSaldo(@PathVariable ("id") Long id, UsuarioDTO dto) {
		try {
			Optional<Usuario> user = service.obterPorId(id);
			if(!user.isPresent()) {
				return new ResponseEntity(HttpStatus.NOT_FOUND);
			}
			BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
			return ResponseEntity.ok(saldo);
		}catch (AutenticacaoException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
}
