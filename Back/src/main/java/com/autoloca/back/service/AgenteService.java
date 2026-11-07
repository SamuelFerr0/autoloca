package com.autoloca.back.service;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.exception.RegraNegocioException;
import com.autoloca.back.model.Perfil;
import com.autoloca.back.model.Usuario;
import com.autoloca.back.repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AgenteService {
  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;

  public AgenteService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
    this.usuarioRepository = usuarioRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public Usuario cadastrar(Dados.CadastroAgente dados) {
    if (dados.perfil() != Perfil.EMPRESA && dados.perfil() != Perfil.BANCO) {
      throw new RegraNegocioException("Selecione Empresa ou Banco para cadastrar um agente.");
    }
    if (dados.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
      throw new RegraNegocioException("Senha deve ter no máximo 72 bytes.");
    }
    Usuario usuario = new Usuario();
    usuario.setUsername(dados.username());
    usuario.setSenha(passwordEncoder.encode(dados.senha()));
    usuario.setNome(dados.nome().trim());
    usuario.setPerfil(dados.perfil());
    return usuarioRepository.save(usuario);
  }
}
