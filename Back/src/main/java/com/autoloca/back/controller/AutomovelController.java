package com.autoloca.back.controller;

import com.autoloca.back.service.AutomovelService;
import com.autoloca.back.dto.Dados;
import com.autoloca.back.model.*;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/automoveis")
public class AutomovelController {
  private final AutomovelService automovelService;

  public AutomovelController(AutomovelService automovelService) {
    this.automovelService = automovelService;
  }

  @GetMapping
  public List<Automovel> listar() {
    return automovelService.listar();
  }

  @GetMapping("/proprietarios")
  public List<Usuario> proprietarios() {
    return automovelService.proprietarios();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Automovel criar(@Valid @RequestBody Dados.Carro dados) {
    return automovelService.salvar(null, dados);
  }

  @PutMapping("/{id}")
  public Automovel editar(@PathVariable Long id, @Valid @RequestBody Dados.Carro dados) {
    return automovelService.salvar(id, dados);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(@PathVariable Long id) {
    automovelService.excluir(id);
  }
}
