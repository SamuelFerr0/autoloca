package com.autoloca.back.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.exception.RegraNegocioException;
import com.autoloca.back.model.*;
import com.autoloca.back.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Regras dos pedidos")
class PedidoServiceTest {
  @Mock private PedidoRepository pedidoRepository;
  @Mock private ClienteRepository clienteRepository;
  @Mock private AutomovelRepository automovelRepository;
  @Mock private UsuarioService usuarioService;
  @InjectMocks private PedidoService pedidoService;

  private Usuario usuario;
  private Cliente cliente;
  private Automovel automovel;
  private Pedido pedido;
  private LocalDate inicio;

  @BeforeEach
  void preparar() {
    usuario = new Usuario();
    usuario.setId(1L);
    usuario.setPerfil(Perfil.CLIENTE);
    cliente = new Cliente();
    cliente.setUsuario(usuario);
    automovel = new Automovel();
    automovel.setId(2L);
    automovel.setDiaria(new BigDecimal("100.00"));
    inicio = LocalDate.now().plusDays(10);
    pedido = new Pedido();
    pedido.setId(3L);
    pedido.setCliente(cliente);
    pedido.setAutomovel(automovel);
    pedido.setInicio(inicio);
    pedido.setFim(inicio.plusDays(3));
    pedido.setStatus(StatusPedido.EM_ANALISE);
  }

  @Test
  @DisplayName("Três diárias de 100 reais geram um pedido de 300 reais")
  void criarCalculaDiarias() {
    Dados.NovoPedido dados = new Dados.NovoPedido(2L, inicio, inicio.plusDays(3));
    when(usuarioService.atual()).thenReturn(usuario);
    when(clienteRepository.findByUsuarioId(1L)).thenReturn(Optional.of(cliente));
    when(automovelRepository.buscarComBloqueio(2L)).thenReturn(Optional.of(automovel));
    when(pedidoRepository.save(any(Pedido.class)))
        .thenAnswer(invocacao -> invocacao.getArgument(0));

    Pedido resultado = pedidoService.criar(dados);

    assertEquals(new BigDecimal("300.00"), resultado.getValorTotal());
    assertEquals(StatusPedido.EM_ANALISE, resultado.getStatus());
    assertSame(cliente, resultado.getCliente());
    verify(usuarioService).exigirCliente();
    verify(pedidoRepository).save(resultado);
  }

  @Test
  void clienteListaSomenteSeusPedidos() {
    when(usuarioService.atual()).thenReturn(usuario);
    when(pedidoRepository.findByClienteUsuarioIdOrderByIdDesc(1L)).thenReturn(List.of(pedido));

    assertEquals(List.of(pedido), pedidoService.listar());
    verify(pedidoRepository, never()).findAllByOrderByIdDesc();
  }

  @Test
  void editarPedidoAprovadoExigeNovaAnalise() {
    pedido.setStatus(StatusPedido.APROVADO);
    pedido.setParecer("Aprovado");
    pedido.setAgente(new Usuario());
    when(pedidoRepository.buscarComBloqueio(3L)).thenReturn(Optional.of(pedido));
    when(automovelRepository.buscarComBloqueio(2L)).thenReturn(Optional.of(automovel));

    pedidoService.editar(3L, new Dados.NovoPedido(2L, inicio, inicio.plusDays(4)));

    assertEquals(StatusPedido.EM_ANALISE, pedido.getStatus());
    assertEquals(new BigDecimal("400.00"), pedido.getValorTotal());
    assertNull(pedido.getParecer());
    assertNull(pedido.getAgente());
    verify(usuarioService).exigirDonoOuAgente(1L);
  }

  @Test
  void rejeitaDevolucaoAntesDaRetirada() {
    when(pedidoRepository.buscarComBloqueio(3L)).thenReturn(Optional.of(pedido));
    Dados.NovoPedido dados = new Dados.NovoPedido(2L, inicio, inicio.minusDays(1));

    assertThrows(RegraNegocioException.class, () -> pedidoService.editar(3L, dados));
    verifyNoInteractions(automovelRepository);
  }

  @Test
  void rejeitaRetiradaNoPassado() {
    when(pedidoRepository.buscarComBloqueio(3L)).thenReturn(Optional.of(pedido));
    Dados.NovoPedido dados = new Dados.NovoPedido(2L, LocalDate.now().minusDays(1), inicio);

    assertThrows(RegraNegocioException.class, () -> pedidoService.editar(3L, dados));
  }

  @Test
  void aprovaPedidoSemConflito() {
    usuario.setPerfil(Perfil.EMPRESA);
    when(pedidoRepository.buscarComBloqueio(3L)).thenReturn(Optional.of(pedido));
    when(automovelRepository.buscarComBloqueio(2L)).thenReturn(Optional.of(automovel));
    when(usuarioService.atual()).thenReturn(usuario);

    pedidoService.avaliar(3L, new Dados.Avaliacao(true, " Renda compatível "));

    assertEquals(StatusPedido.APROVADO, pedido.getStatus());
    assertEquals("Renda compatível", pedido.getParecer());
    verify(usuarioService).exigirAgente();
  }

  @Test
  void impedeAprovacaoQuandoExisteReserva() {
    when(pedidoRepository.buscarComBloqueio(3L)).thenReturn(Optional.of(pedido));
    when(automovelRepository.buscarComBloqueio(2L)).thenReturn(Optional.of(automovel));
    when(pedidoRepository.contarConflitos(
            2L,
            List.of(StatusPedido.APROVADO, StatusPedido.CONTRATADO),
            inicio,
            inicio.plusDays(3),
            3L))
        .thenReturn(1L);

    assertThrows(
        RegraNegocioException.class,
        () -> pedidoService.avaliar(3L, new Dados.Avaliacao(true, "Aprovado")));
    assertEquals(StatusPedido.EM_ANALISE, pedido.getStatus());
  }

  @Test
  void reprovaPedidoComParecer() {
    when(pedidoRepository.buscarComBloqueio(3L)).thenReturn(Optional.of(pedido));

    pedidoService.avaliar(3L, new Dados.Avaliacao(false, "Renda insuficiente"));

    assertEquals(StatusPedido.REPROVADO, pedido.getStatus());
    assertEquals("Renda insuficiente", pedido.getParecer());
    verifyNoInteractions(automovelRepository);
  }

  @Test
  void cancelaPedidoEmAnalise() {
    when(pedidoRepository.buscarComBloqueio(3L)).thenReturn(Optional.of(pedido));

    pedidoService.cancelar(3L);

    assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    verify(usuarioService).exigirCliente();
    verify(usuarioService).exigirDonoOuAgente(1L);
  }

  @Test
  void naoCancelaPedidoContratado() {
    pedido.setStatus(StatusPedido.CONTRATADO);
    when(pedidoRepository.buscarComBloqueio(3L)).thenReturn(Optional.of(pedido));

    assertThrows(RegraNegocioException.class, () -> pedidoService.cancelar(3L));
    assertEquals(StatusPedido.CONTRATADO, pedido.getStatus());
  }
}
