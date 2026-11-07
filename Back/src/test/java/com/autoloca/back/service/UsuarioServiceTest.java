package com.autoloca.back.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.autoloca.back.model.Perfil;
import com.autoloca.back.model.Usuario;
import com.autoloca.back.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
  @Mock private UsuarioRepository usuarioRepository;
  @InjectMocks private UsuarioService usuarioService;
  private Usuario usuario;

  @BeforeEach
  void preparar() {
    usuario = new Usuario();
    usuario.setId(1L);
    usuario.setPerfil(Perfil.CLIENTE);
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("ana", null));
    when(usuarioRepository.findByUsername("ana")).thenReturn(Optional.of(usuario));
  }

  @AfterEach
  void limpar() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void clienteNaoPodeExecutarOperacaoDeAgente() {
    assertThrows(AccessDeniedException.class, () -> usuarioService.exigirAgente());
  }

  @Test
  void clienteNaoPodeAcessarOutroCliente() {
    assertThrows(AccessDeniedException.class, () -> usuarioService.exigirDonoOuAgente(2L));
  }

  @Test
  void clientePodeAcessarSeuCadastro() {
    assertDoesNotThrow(() -> usuarioService.exigirDonoOuAgente(1L));
  }

  @Test
  void agentePodeAcessarCadastroDeCliente() {
    usuario.setPerfil(Perfil.EMPRESA);
    assertDoesNotThrow(() -> usuarioService.exigirDonoOuAgente(2L));
  }
}
