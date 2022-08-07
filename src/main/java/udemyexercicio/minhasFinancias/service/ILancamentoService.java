package udemyexercicio.minhasFinancias.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import udemyexercicio.minhasFinancias.model.entity.Lancamentos;
import udemyexercicio.minhasFinancias.model.enuns.EnumStatusLancamento;

public interface ILancamentoService {
	
	Lancamentos salvar(Lancamentos lancamento);
	Lancamentos atualizarLancamento(Lancamentos lancamento);
	void deletar(Lancamentos lancamento);
	List<Lancamentos> buscar(Lancamentos lancamentoFiltro);
	void atualizarStatus(Lancamentos lancamento, EnumStatusLancamento status);
	void validar(Lancamentos lancamento);
	Optional<Lancamentos> obterPorId(Long id);
	BigDecimal obterSaldoPorUsuario(Long id);
}
