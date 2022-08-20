package udemyexercicio.minhasFinancias.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import udemyexercicio.minhasFinancias.exception.RegraNegocioException;
import udemyexercicio.minhasFinancias.model.entity.Lancamentos;
import udemyexercicio.minhasFinancias.model.entity.Usuario;
import udemyexercicio.minhasFinancias.model.enuns.EnumStatusLancamento;
import udemyexercicio.minhasFinancias.model.enuns.EnumTipoLancamento;
import udemyexercicio.minhasFinancias.model.repository.LancamentoRepository;
import udemyexercicio.minhasFinancias.model.repository.LancamentoRepositoryTest;
import udemyexercicio.minhasFinancias.service.implementacao.LancamentoService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoService service;
	@MockBean
	LancamentoRepository repository;

	@Test
	public void deveSalvarUmLancamento() {
		//cenário
		Lancamentos lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamentos lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(EnumStatusLancamento.PENDENTE);
		when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//execucao
		Lancamentos lancamento = service.salvar(lancamentoASalvar);
		
		//verificação
		assertThat( lancamento.getId() ).isEqualTo(lancamentoSalvo.getId());
		assertThat(lancamento.getStatus()).isEqualTo(EnumStatusLancamento.PENDENTE);
	}

	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		// cenário
		Lancamentos lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

		// execucao e verificacao
		catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		verify(repository, never()).save(lancamentoASalvar);
	}

	@Test
	public void deveAtualizarUmLancamento() {
		// cenário
		Lancamentos lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(EnumStatusLancamento.PENDENTE);

		doNothing().when(service).validar(lancamentoSalvo);

		when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		// execucao
		service.atualizarLancamento(lancamentoSalvo);

		// verificação
		verify(repository, times(1)).save(lancamentoSalvo);

	}

	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		// cenário
		Lancamentos lancamento = LancamentoRepositoryTest.criarLancamento();

		// execucao e verificacao
		catchThrowableOfType(() -> service.atualizarLancamento(lancamento), NullPointerException.class);
		verify(repository, never()).save(lancamento);
	}

	@Test
	public void deveDeletarUmLancamento() {
		// cenário
		Lancamentos lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		// execucao
		service.deletar(lancamento);

		// verificacao
		verify(repository).delete(lancamento);
	}

	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {

		// cenário
		Lancamentos lancamento = LancamentoRepositoryTest.criarLancamento();

		// execucao
		catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);

		// verificacao
		verify(repository, never()).delete(lancamento);
	}

	@Test
	public void deveFiltrarLancamentos() {
		// cenário
		Lancamentos lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		List<Lancamentos> lista = Arrays.asList(lancamento);
		when(repository.findAll(any(Example.class))).thenReturn(lista);

		// execucao
		List<Lancamentos> resultado = service.buscar(lancamento);

		// verificacoes
		assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);

	}

	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		// cenário
		Lancamentos lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(EnumStatusLancamento.PENDENTE);

		EnumStatusLancamento novoStatus = EnumStatusLancamento.EFETIVADO;
		doReturn(lancamento).when(service).atualizarLancamento(lancamento);

		// execucao
		service.atualizarStatus(lancamento, novoStatus);

		// verificacoes
		assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		verify(service).atualizarLancamento(lancamento);

	}

	@Test
	public void deveObterUmLancamentoPorID() {
		// cenário
		Long id = 1l;

		Lancamentos lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		when(repository.findById(id)).thenReturn(Optional.of(lancamento));

		// execucao
		Optional<Lancamentos> resultado = service.obterPorId(id);

		// verificacao
		assertThat(resultado.isPresent()).isTrue();
	}

	@Test
	public void deveREtornarVazioQuandoOLancamentoNaoExiste() {
		// cenário
		Long id = 1l;

		Lancamentos lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		when(repository.findById(id)).thenReturn(Optional.empty());

		// execucao
		Optional<Lancamentos> resultado = service.obterPorId(id);

		// verificacao
		assertThat(resultado.isPresent()).isFalse();
	}

	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		Lancamentos lancamento = new Lancamentos();

		Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida!");

		lancamento.setDescricao("");
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida!");

		lancamento.setDescricao("Salario");

		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");

		lancamento.setAno(0);

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");

		lancamento.setAno(13);

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");

		lancamento.setMes(1);

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido!");

		lancamento.setAno(202);

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido!");

		lancamento.setAno(2020);

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário!");

		lancamento.setUsuario(new Usuario());

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário!");

		lancamento.getUsuario().setId(1l);

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido!");

		lancamento.setValor(BigDecimal.ZERO);

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido!");

		lancamento.setValor(BigDecimal.valueOf(1));

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de Lançamento!");

	}

	@Test
	public void deveObterSaldoPorUsuario() {
		//cenario
		Long idUsuario = 1l;
		
		when( repository
				.obterSaldoPorTipoLancamentoUsuarioEStatus(idUsuario, EnumTipoLancamento.RECEITA, EnumStatusLancamento.EFETIVADO)) 
				.thenReturn(BigDecimal.valueOf(100));
		
		when( repository
				.obterSaldoPorTipoLancamentoUsuarioEStatus(idUsuario, EnumTipoLancamento.DESPESA, EnumStatusLancamento.EFETIVADO)) 
				.thenReturn(BigDecimal.valueOf(50));
		
		//execucao
		BigDecimal saldo = service.obterSaldoPorUsuario(idUsuario);
		
		//verificacao
		assertThat(saldo).isEqualTo(BigDecimal.valueOf(50));
		
	}
}
