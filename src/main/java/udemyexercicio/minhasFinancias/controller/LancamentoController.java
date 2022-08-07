package udemyexercicio.minhasFinancias.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import udemyexercicio.minhasFinancias.dto.AtualizarStatusDTO;
import udemyexercicio.minhasFinancias.dto.LancamentoDTO;
import udemyexercicio.minhasFinancias.exception.RegraNegocioException;
import udemyexercicio.minhasFinancias.model.entity.Lancamentos;
import udemyexercicio.minhasFinancias.model.entity.Usuario;
import udemyexercicio.minhasFinancias.model.enuns.EnumStatusLancamento;
import udemyexercicio.minhasFinancias.model.enuns.EnumTipoLancamento;
import udemyexercicio.minhasFinancias.service.implementacao.LancamentoService;
import udemyexercicio.minhasFinancias.service.implementacao.UsuarioService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lancamentos")
public class LancamentoController {

	private final LancamentoService service;

	private final UsuarioService usuarioService;

	@PostMapping("/salvar")
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		try {
			Lancamentos entity = converterDTO(dto);
			entity = service.salvar(entity);
			return new ResponseEntity(entity, HttpStatus.CREATED);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/obterLancamento/{id}")
	public ResponseEntity obterLancamento(@PathVariable("id") Long id) {
		return service.obterPorId(id).map(lancamento -> 
			new ResponseEntity(converter(lancamento), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
	}

	@PutMapping("{id}")
	public ResponseEntity atualizarLancamento(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		return service.obterPorId(id).map(entity -> {
			try {
				Lancamentos lancamento = converterDTO(dto);
				lancamento.setId(entity.getId());
				service.atualizarLancamento(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado!", HttpStatus.BAD_REQUEST));
	}

	@DeleteMapping("/deletar/{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		return service.obterPorId(id).map(entity -> {
			try {
				service.deletar(entity);
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado!", HttpStatus.BAD_REQUEST));
	}

	@GetMapping
	public ResponseEntity buscar(@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano, @RequestParam("usuario") Long idUsuario) {

		Lancamentos filtro = new Lancamentos();
		filtro.setDescricao(descricao);
		filtro.setMes(mes);
		filtro.setAno(ano);
		Optional<Usuario> user = usuarioService.obterPorId(idUsuario);

		if (!user.isPresent()) {
			return ResponseEntity.badRequest()
					.body("Não foi possível realizar a consulta, Usuário não encontrado para o ID informado!");
		} else {
			filtro.setUsuario(user.get());
		}

		List<Lancamentos> resultado = service.buscar(filtro);

		return ResponseEntity.ok(resultado);
	}

	@PutMapping("/atualizarStatus/{id}")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizarStatusDTO statusDto) {

		return service.obterPorId(id).map(entity -> {
			EnumStatusLancamento statusSelecionado = EnumStatusLancamento.valueOf(statusDto.getStatus());
			if (statusSelecionado == null) {
				return ResponseEntity.badRequest()
						.body("Não foi possível atualizar o status do lançamento, envie um status válido!");
			}
			try {
				entity.setStatus(statusSelecionado);
				service.atualizarStatus(entity, statusSelecionado);
				return ResponseEntity.ok(entity);
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado!", HttpStatus.BAD_REQUEST));
	}

	private LancamentoDTO converter(Lancamentos lancamentos) {
		return LancamentoDTO.builder()
				.id(lancamentos.getId())
				.descricao(lancamentos.getDescricao())
				.valor(lancamentos.getValor())
				.mes(lancamentos.getMes())
				.ano(lancamentos.getAno())
				.status(lancamentos.getStatus().name())
				.tipo(lancamentos.getTipo().name())
				.usuario(lancamentos.getUsuario().getId())
				.build();
	}
	
	private Lancamentos converterDTO(LancamentoDTO dto) {
		Lancamentos lancamento = new Lancamentos();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		lancamento.setDataCadastro(LocalDate.now());

		Usuario user = usuarioService.obterPorId(dto.getUsuario())
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o ID informado!"));

		lancamento.setUsuario(user);

		if (dto.getTipo() != null) {
			lancamento.setTipo(EnumTipoLancamento.valueOf(dto.getTipo()));
		}

		if (dto.getStatus() != null) {
			lancamento.setStatus(EnumStatusLancamento.valueOf(dto.getStatus()));
		}
		return lancamento;
	}

}
