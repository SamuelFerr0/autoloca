package com.autoloca.back.controller;

import com.autoloca.back.service.FotoAutomovelService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fotos")
public class FotoAutomovelController {
  private final FotoAutomovelService fotos;

  public FotoAutomovelController(FotoAutomovelService fotos) {
    this.fotos = fotos;
  }

  @GetMapping
  public FotoAutomovelService.Foto buscar(@RequestParam String marca, @RequestParam String modelo) {
    if (marca.isBlank() || modelo.isBlank() || marca.length() > 80 || modelo.length() > 100)
      throw new com.autoloca.back.exception.RegraNegocioException(
          "Informe marca e modelo válidos.");
    return fotos.buscar(marca, modelo);
  }
}
