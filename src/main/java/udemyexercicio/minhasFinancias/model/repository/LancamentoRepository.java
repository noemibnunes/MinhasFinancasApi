package udemyexercicio.minhasFinancias.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import udemyexercicio.minhasFinancias.model.entity.Lancamentos;
import udemyexercicio.minhasFinancias.model.enuns.EnumStatusLancamento;
import udemyexercicio.minhasFinancias.model.enuns.EnumTipoLancamento;

public interface LancamentoRepository extends JpaRepository<Lancamentos, Long> {

	@Query(value = "select sum(l.valor) from Lancamentos l join l.usuario u where u.id = :idUsuario and l.tipo = :tipo and l.status = :status group by u")
	BigDecimal obterSaldoPorTipoLancamentoUsuarioEStatus(@Param("idUsuario") Long idUsuario,
			@Param("tipo") EnumTipoLancamento tipoLancamento,
			@Param("status") EnumStatusLancamento statusLancamento);

}
