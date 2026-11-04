package com.autoloca.back.controller;

import com.autoloca.back.service.*;
import com.autoloca.back.dto.Dados;
import com.autoloca.back.model.Cliente;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
  private final ClienteService clienteService;
  private final UsuarioService usuarioService;

  public ClienteController(ClienteService clienteService, UsuarioService usuarioService) {
    this.clienteService = clienteService;
    this.usuarioService = usuarioService;
  }

  @GetMapping
  public List<Cliente> listar() {
    return clienteService.listar();
  }

  @GetMapping("/{id}")
  public Cliente buscar(@PathVariable Long id) {
    return clienteService.buscar(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Cliente criar(@Valid @RequestBody Dados.Cadastro dados) {
    usuarioService.exigirAgente();
    return clienteService.cadastrar(dados);
  }

  @PutMapping("/{id}")
  public Cliente editar(@PathVariable Long id, @Valid @RequestBody Dados.PerfilCliente dados) {
    return clienteService.atualizar(id, dados);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(@PathVariable Long id) {
    clienteService.excluir(id);
  }
}
