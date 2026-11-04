package com.autoloca.back.service;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.model.Automovel;
import com.autoloca.back.model.Usuario;
import com.autoloca.back.repository.AutomovelRepository;
import com.autoloca.back.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AutomovelService {
  private final AutomovelRepository automovelRepository;
  private final UsuarioRepository usuarioRepository;
  private final UsuarioService usuarioService;

  public AutomovelService(
      AutomovelRepository automovelRepository,
      UsuarioRepository usuarioRepository,
      UsuarioService usuarioService) {
    this.automovelRepository = automovelRepository;
    this.usuarioRepository = usuarioRepository;
    this.usuarioService = usuarioService;
  }

  public List<Automovel> listar() {
    return automovelRepository.findAll();
  }

  public Automovel salvar(Long id, Dados.Carro dados) {
    usuarioService.exigirAgente();
    Automovel automovel;
    if (id == null) {
      automovel = new Automovel();
    } else {
      automovel = automovelRepository.buscarComBloqueio(id).orElseThrow();
    }

    Usuario proprietario = usuarioRepository.findById(dados.proprietarioId()).orElseThrow();
    automovel.setMatricula(dados.matricula().trim());
    automovel.setAno(dados.ano());
    automovel.setMarca(dados.marca().trim());
    automovel.setModelo(dados.modelo().trim());
    automovel.setPlaca(dados.placa());
    automovel.setDiaria(dados.diaria());
    automovel.setProprietario(proprietario);
    return automovelRepository.save(automovel);
  }

  public void excluir(Long id) {
    usuarioService.exigirAgente();
    Automovel automovel = automovelRepository.findById(id).orElseThrow();
    automovelRepository.delete(automovel);
  }

  public List<Usuario> proprietarios() {
    usuarioService.exigirAgente();
    return usuarioRepository.findAll();
  }
}
