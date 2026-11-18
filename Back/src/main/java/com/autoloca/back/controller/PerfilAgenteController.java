package com.autoloca.back.controller;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.model.Usuario;
import com.autoloca.back.repository.UsuarioRepository;
import com.autoloca.back.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agente/perfil")
public class PerfilAgenteController {
  private final UsuarioService usuarios;
  private final UsuarioRepository repository;

  public PerfilAgenteController(UsuarioService usuarios, UsuarioRepository repository) {
    this.usuarios = usuarios;
    this.repository = repository;
  }

  @PutMapping
  public Usuario atualizar(@Valid @RequestBody Dados.PerfilAgente dados) {
    usuarios.exigirAgente();
    Usuario u = usuarios.atual();
    u.setNome(dados.nome().trim());
    u.setCnpj(dados.cnpj());
    u.setEnderecoEmpresa(dados.enderecoEmpresa().trim());
    u.setRepresentante(dados.representante().trim());
    return repository.save(u);
  }
}
