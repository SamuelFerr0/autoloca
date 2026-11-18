package com.autoloca.back.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.autoloca.back.model.*;
import com.autoloca.back.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class UsuarioServiceTest {
  UsuarioRepository repo;
  UsuarioService service;
  Usuario atual;
  Automovel carro;
  Pedido pedido;

  @BeforeEach
  void preparar() {
    repo = mock(UsuarioRepository.class);
    service = new UsuarioService(repo);
    atual = new Usuario();
    atual.setId(1L);
    atual.setPerfil(Perfil.EMPRESA);
    when(repo.findByUsername("teste")).thenReturn(Optional.of(atual));
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("teste", ""));
    Usuario outro = new Usuario();
    outro.setId(2L);
    outro.setPerfil(Perfil.BANCO);
    carro = new Automovel();
    carro.setAnunciante(outro);
    pedido = new Pedido();
    pedido.setAutomovel(carro);
    Cliente c = new Cliente();
    c.setUsuario(outro);
    pedido.setCliente(c);
  }

  @AfterEach
  void limpar() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void agenteNaoAcessaAnuncioDeOutro() {
    assertThrows(AccessDeniedException.class, () -> service.exigirAnunciante(carro));
  }

  @Test
  void agenteNaoAcessaPedidoDeOutro() {
    assertThrows(AccessDeniedException.class, () -> service.exigirAcessoPedido(pedido));
  }

  @Test
  void agenteNaoAlteraCadastroDeCliente() {
    assertThrows(AccessDeniedException.class, () -> service.exigirTitular(2L));
  }

  @Test
  void clienteNaoAcessaPedidoDeOutro() {
    atual.setPerfil(Perfil.CLIENTE);
    assertThrows(AccessDeniedException.class, () -> service.exigirAcessoPedido(pedido));
  }

  @Test
  void anuncianteAcessaSeuPedidoMesmoAposTrocaDoProprietario() {
    carro.setAnunciante(atual);
    assertDoesNotThrow(() -> service.exigirAcessoPedido(pedido));
  }
}
