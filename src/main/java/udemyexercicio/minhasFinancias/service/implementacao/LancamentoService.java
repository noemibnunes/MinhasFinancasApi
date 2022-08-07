package udemyexercicio.minhasFinancias.service.implementacao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;

import udemyexercicio.minhasFinancias.exception.RegraNegocioException;
import udemyexercicio.minhasFinancias.model.entity.Lancamentos;
import udemyexercicio.minhasFinancias.model.enuns.EnumStatusLancamento;
import udemyexercicio.minhasFinancias.model.enuns.EnumTipoLancamento;
import udemyexercicio.minhasFinancias.model.repository.LancamentoRepository;
import udemyexercicio.minhasFinancias.service.ILancamentoService;

@Service
public class LancamentoService implements ILancamentoService {

	@Autowired
	private LancamentoRepository repository;

	public LancamentoService(LancamentoRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional // transação no BD, faz commit se tudo estiver ok e rollback se algo dê errado
	public Lancamentos salvar(Lancamentos lancamento) {
		validar(lancamento);
		lancamento.setStatus(EnumStatusLancamento.PENDENTE);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamentos atualizarLancamento(Lancamentos lancamento) {
		Objects.requireNonNull(lancamento.getId());
		validar(lancamento);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamentos lancamento) {
		Objects.requireNonNull(lancamento.getId());
		repository.delete(lancamento);
	}

	@Override
	@Transactional
	public List<Lancamentos> buscar(Lancamentos lancamentoFiltro) {
		// leva em consideração o filtro
		Example example = Example.of(lancamentoFiltro,
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING)); // não importa se é maiusculo ou minisculo
		return repository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamentos lancamento, EnumStatusLancamento status) {
		lancamento.setStatus(status);
		atualizarLancamento(lancamento);
	}
	
	@Override
	public void validar(Lancamentos lancamento) {
		if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma Descrição válida!");
		}
		
		if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
			throw new RegraNegocioException("Informe um Mês válido!");
		}
		
		if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
			throw new RegraNegocioException("Informe um Ano válido!");
		}
		
		if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
			throw new RegraNegocioException("Informe um Usuário!");
		}
		
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) { // retorna 1 se o valor for maior, 0 se for igual e -1 se for menor que o valor passado
			throw new RegraNegocioException("Informe um Valor válido!");
		}
		
		if(lancamento.getTipo() == null) {
			throw new RegraNegocioException("Informe um Tipo de Lançamento!");

		}
		
		
	}

	@Override
	public Optional<Lancamentos> obterPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	@Transactional
	public BigDecimal obterSaldoPorUsuario(Long id) {
		BigDecimal receitas = repository.obterSaldoPorTipoLancamentoUsuarioEStatus(id, EnumTipoLancamento.RECEITA, EnumStatusLancamento.EFETIVADO);
		BigDecimal despesas = repository.obterSaldoPorTipoLancamentoUsuarioEStatus(id, EnumTipoLancamento.DESPESA, EnumStatusLancamento.EFETIVADO);

		if(receitas == null) {
			receitas = BigDecimal.ZERO;
		}
		
		if(despesas == null) {
			despesas = BigDecimal.ZERO;
		}
		return receitas.subtract(despesas);
	}

}
