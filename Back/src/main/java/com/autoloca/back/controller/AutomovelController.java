package com.autoloca.back.controller;

import com.autoloca.back.service.AutomovelService;
import com.autoloca.back.dto.Dados;
import com.autoloca.back.model.*;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
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

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Automovel criar(@Valid @RequestBody Dados.Carro dados) {
    return automovelService.salvar(null, dados);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Automovel editar(@PathVariable Long id, @Valid @RequestBody Dados.Carro dados) {
    return automovelService.salvar(id, dados);
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Automovel criarComFoto(
      @Valid @RequestPart("dados") Dados.Carro dados,
      @RequestPart(value = "foto", required = false) MultipartFile foto) {
    return automovelService.salvar(null, dados, foto);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Automovel editarComFoto(
      @PathVariable Long id,
      @Valid @RequestPart("dados") Dados.Carro dados,
      @RequestPart(value = "foto", required = false) MultipartFile foto) {
    return automovelService.salvar(id, dados, foto);
  }

  @GetMapping("/{id}/foto")
  public ResponseEntity<byte[]> foto(@PathVariable Long id) {
    Automovel carro = automovelService.buscar(id);
    if (!carro.isTemFoto()) return ResponseEntity.notFound().build();
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(carro.getFotoTipo()))
        .header("Cache-Control", "no-store")
        .body(carro.getFoto());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(@PathVariable Long id) {
    automovelService.excluir(id);
  }
}
