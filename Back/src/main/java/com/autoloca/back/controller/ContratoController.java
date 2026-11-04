package com.autoloca.back.controller;

import com.autoloca.back.service.ContratoService;
import com.autoloca.back.dto.Dados;
import com.autoloca.back.model.Contrato;
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
}
