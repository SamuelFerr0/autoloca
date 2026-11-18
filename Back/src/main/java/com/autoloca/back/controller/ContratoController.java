package com.autoloca.back.controller;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.model.Contrato;
import com.autoloca.back.service.ContratoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contratos")
public class ContratoController {
  private final ContratoService contratoService;

  public ContratoController(ContratoService contratoService) {
    this.contratoService = contratoService;
  }

  @GetMapping
  public List<Contrato> listar() {
    return contratoService.listar();
  }

  @PostMapping("/pedido/{id}")
  @ResponseStatus(HttpStatus.CREATED)
  public Contrato criar(@PathVariable Long id, @Valid @RequestBody Dados.NovoContrato dados) {
    return contratoService.criar(id, dados);
  }

  @GetMapping("/{id}")
  public Contrato buscar(@PathVariable Long id) {
    return contratoService.buscar(id);
  }

  @GetMapping("/{id}/pdf")
  public org.springframework.http.ResponseEntity<byte[]> pdf(@PathVariable Long id) {
    Contrato c = contratoService.buscar(id);
    if (c.getArquivoPdf() == null)
      throw new com.autoloca.back.exception.RegraNegocioException(
          "Documento anterior ainda não consolidado. Reinicie o backend após aplicar a migração.");
    return org.springframework.http.ResponseEntity.ok()
        .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
        .header("Content-Disposition", "attachment; filename=contrato-" + c.getNumero() + ".pdf")
        .header("X-Content-Type-Options", "nosniff")
        .header("Cache-Control", "no-store")
        .body(c.getArquivoPdf());
  }
}
