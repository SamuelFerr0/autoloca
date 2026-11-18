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

  public void exigirAnunciante(com.autoloca.back.model.Automovel carro) {
    exigirAgente();
    if (carro.getAnunciante() == null || !atual().getId().equals(carro.getAnunciante().getId()))
      throw new AccessDeniedException("Este anúncio pertence a outro agente.");
  }

  public void exigirAcessoPedido(com.autoloca.back.model.Pedido pedido) {
    Usuario u = atual();
    if (u.getPerfil() == Perfil.CLIENTE) {
      if (!u.getId().equals(pedido.getCliente().getUsuario().getId()))
        throw new AccessDeniedException("Pedido de outro cliente.");
    } else exigirAnunciante(pedido.getAutomovel());
  }

  public void exigirTitular(Long id) {
    if (!atual().getId().equals(id))
      throw new AccessDeniedException("Somente o titular pode alterar o cadastro.");
  }
}
