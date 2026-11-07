package com.autoloca.back.controller;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.model.*;
import com.autoloca.back.service.*;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final ClienteService clienteService;
  private final UsuarioService usuarioService;
  private final AgenteService agenteService;

  public AuthController(
      ClienteService clienteService, UsuarioService usuarioService, AgenteService agenteService) {
    this.clienteService = clienteService;
    this.usuarioService = usuarioService;
    this.agenteService = agenteService;
  }

  @GetMapping("/csrf")
  public Map<String, String> csrf(CsrfToken token) {
    return Map.of("token", token.getToken(), "headerName", token.getHeaderName());
  }

  @PostMapping("/cadastro")
  @ResponseStatus(HttpStatus.CREATED)
  public Cliente cadastrar(@Valid @RequestBody Dados.Cadastro dados) {
    return clienteService.cadastrar(dados);
  }

  @PostMapping("/cadastro/agente")
  @ResponseStatus(HttpStatus.CREATED)
  public Usuario cadastrarAgente(@Valid @RequestBody Dados.CadastroAgente dados) {
    return agenteService.cadastrar(dados);
  }

  @GetMapping("/me")
  public Usuario me() {
    return usuarioService.atual();
  }
}
