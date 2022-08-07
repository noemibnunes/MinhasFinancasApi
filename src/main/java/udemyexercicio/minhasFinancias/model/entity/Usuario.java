package udemyexercicio.minhasFinancias.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario", schema = "financas") // nome da tabela e esquema
public class Usuario {
	
	public static final String COLUNA_ID = "id";
	public static final String COLUNA_NOME = "nome";
	public static final String COLUNA_EMAIL = "email";
	public static final String COLUNA_SENHA = "senha";

	@Id // define que este atributo é a Primary Key da tabela
	@Column(name=COLUNA_ID) // nome da coluna que foi mapeada no banco
	@GeneratedValue(strategy = GenerationType.IDENTITY) // gerar os ids
	private Long id; 
	
	@Column(name=COLUNA_NOME)
	private String nome;
	
	@Column(name=COLUNA_EMAIL)
	private String email;
	
	@Column(name=COLUNA_SENHA)
	private String senha;

}
