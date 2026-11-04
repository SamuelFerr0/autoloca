package com.autoloca.back.service;

import com.autoloca.back.model.Perfil;
import com.autoloca.back.model.Usuario;
import com.autoloca.back.repository.UsuarioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
  private final UsuarioRepository usuarioRepository;

  public UsuarioService(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  public Usuario atual() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return usuarioRepository
        .findByUsername(username)
        .orElseThrow(() -> new AccessDeniedException("Sessão inválida"));
  }

  public void exigirAgente() {
    if (atual().getPerfil() == Perfil.CLIENTE) {
      throw new AccessDeniedException("Somente agentes");
    }
  }

  public void exigirCliente() {
    if (atual().getPerfil() != Perfil.CLIENTE) {
      throw new AccessDeniedException("Somente clientes");
    }
  }

  public void exigirDonoOuAgente(Long usuarioId) {
    Usuario usuario = atual();
    if (usuario.getPerfil() == Perfil.CLIENTE && !usuario.getId().equals(usuarioId)) {
      throw new AccessDeniedException("Outro cliente");
    }
  }
}
