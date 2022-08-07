package udemyexercicio.minhasFinancias.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import udemyexercicio.minhasFinancias.model.enuns.EnumStatusLancamento;
import udemyexercicio.minhasFinancias.model.enuns.EnumTipoLancamento;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lancamento", schema = "financas") // nome da tabela e esquema
public class Lancamentos {
	
	public static final String COLUNA_ID = "id";
	public static final String COLUNA_DESCRICAO = "descricao";
	public static final String COLUNA_MES = "mes";
	public static final String COLUNA_ANO = "ano";
	public static final String COLUNA_USUARIO = "id_usuario";
	public static final String COLUNA_VALOR = "valor";
	public static final String COLUNA_DATA_CADASTRO = "data_cadastro";
	public static final String COLUNA_TIPO = "tipo";
	public static final String COLUNA_STATUS = "status";

	@Id // define que este atributo é a Primary Key da tabela
	@Column(name=COLUNA_ID) // nome da coluna que foi mapeada no banco
	@GeneratedValue(strategy = GenerationType.IDENTITY) // gerar os ids
	private Long id; 
	
	@Column(name=COLUNA_DESCRICAO)
	private String descricao;
	
	@Column(name=COLUNA_MES)
	private Integer mes;
	
	@Column(name=COLUNA_ANO)
	private Integer ano;
	
	@ManyToOne
	@JoinColumn(name=COLUNA_USUARIO)
	private Usuario usuario;

	@Column(name=COLUNA_VALOR)
	private BigDecimal valor;
	
	@Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
	@Column(name=COLUNA_DATA_CADASTRO)
	private LocalDate dataCadastro;
	
	@Column(name=COLUNA_TIPO)
	@Enumerated(value= EnumType.STRING)
	private EnumTipoLancamento tipo;
	
	@Column(name=COLUNA_STATUS)
	@Enumerated(value= EnumType.STRING)
	private EnumStatusLancamento status;

}

	
