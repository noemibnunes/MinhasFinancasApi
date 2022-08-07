package udemyexercicio.minhasFinancias.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import udemyexercicio.minhasFinancias.model.entity.Usuario;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTeste {

	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager; // responsavel pelas operações no BD - TestEntityManager é usado para teste
	
	@Test
	public void verificarExisteEmail() {
		// Cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		// Acao - Execucao
		boolean resutlado = repository.existsByEmail("test@email.com");
		// Verificacao
		Assertions.assertThat(resutlado).isTrue();
	}
	
	@Test
	public void retornarFalsoCasoNaoHouverUsuarioCadastradoComEmail() {
		// Cenario
		// Acao - Execucao
		boolean resutlado = repository.existsByEmail("test@email.com");
		// Verificacao
		Assertions.assertThat(resutlado).isFalse();
	}
	
	@Test
	public void persistirUsuarioNoBD() {
		// Cenario
		Usuario usuario = criarUsuario();
		// Acao - Execucao
		Usuario userSalvo = repository.save(usuario);
		// Verificacao
		Assertions.assertThat(userSalvo).isNotNull();
	}
	
	@Test
	public void buscarUsuarioPorEmail() {
		// Cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		// Acao - Execucao
		Optional<Usuario> resultado = repository.findByEmail("test@email.com");
		
		// Verificacao
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void retornarVazioSeUsuarioNaoTiverNaBase() {
		// Cenario
		
		// Acao - Execucao
		Optional<Usuario> resultado = repository.findByEmail("test@email.com");
		
		// Verificacao
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
	public static Usuario criarUsuario() {
		return Usuario.builder().nome("Teste").email("test@email.com").senha("123").build();
	}
	
	
}
