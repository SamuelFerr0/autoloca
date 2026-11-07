package com.autoloca.back.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.exception.RegraNegocioException;
import com.autoloca.back.model.*;
import com.autoloca.back.repository.*;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContratoServiceTest {
  @Mock private ContratoRepository contratoRepository;
  @Mock private PedidoRepository pedidoRepository;
  @Mock private UsuarioRepository usuarioRepository;
  @Mock private AutomovelRepository automovelRepository;
  @Mock private UsuarioService usuarioService;
  @Mock private PedidoService pedidoService;
  @InjectMocks private ContratoService contratoService;

  private Pedido pedido;
  private Automovel automovel;
  private Usuario agente;

  @BeforeEach
  void preparar() {
    agente = new Usuario();
    agente.setId(1L);
    agente.setPerfil(Perfil.BANCO);
    automovel = new Automovel();
    automovel.setId(2L);
    pedido = new Pedido();
    pedido.setId(3L);
    pedido.setAutomovel(automovel);
    pedido.setStatus(StatusPedido.APROVADO);
    pedido.setValorTotal(new BigDecimal("300.00"));
  }

  private void prepararPedidoAprovado() {
    when(pedidoRepository.buscarComBloqueio(3L)).thenReturn(Optional.of(pedido));
    when(automovelRepository.buscarComBloqueio(2L)).thenReturn(Optional.of(automovel));
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(agente));
  }

  @Test
  void naoCriaContratoAntesDaAprovacao() {
    pedido.setStatus(StatusPedido.EM_ANALISE);
    when(pedidoRepository.buscarComBloqueio(3L)).thenReturn(Optional.of(pedido));

    assertThrows(
        RegraNegocioException.class,
        () -> contratoService.criar(3L, new Dados.NovoContrato(1L, null, null)));
    verify(contratoRepository, never()).save(any());
  }

  @Test
  void criaContratoSemCredito() {
    prepararPedidoAprovado();
    when(contratoRepository.save(any(Contrato.class)))
        .thenAnswer(invocacao -> invocacao.getArgument(0));

    Contrato resultado = contratoService.criar(3L, new Dados.NovoContrato(1L, null, null));

    assertEquals(StatusPedido.CONTRATADO, pedido.getStatus());
    assertSame(agente, automovel.getProprietario());
    assertSame(pedido, resultado.getPedido());
    assertNull(resultado.getCredito());
    verify(pedidoService).verificarDisponibilidade(pedido);
    verify(usuarioService).exigirAgente();
  }

  @Test
  void bancoPodeConcederCredito() {
    prepararPedidoAprovado();
    when(usuarioService.atual()).thenReturn(agente);
    when(contratoRepository.save(any(Contrato.class)))
        .thenAnswer(invocacao -> invocacao.getArgument(0));

    Contrato resultado =
        contratoService.criar(3L, new Dados.NovoContrato(1L, new BigDecimal("200.00"), 2));

    assertEquals(new BigDecimal("200.00"), resultado.getCredito().getValor());
    assertEquals(2, resultado.getCredito().getParcelas());
    assertSame(agente, resultado.getCredito().getBanco());
  }

  @Test
  void empresaNaoPodeConcederCredito() {
    prepararPedidoAprovado();
    agente.setPerfil(Perfil.EMPRESA);
    when(usuarioService.atual()).thenReturn(agente);

    assertThrows(
        RegraNegocioException.class,
        () -> contratoService.criar(3L, new Dados.NovoContrato(1L, new BigDecimal("200.00"), 2)));
    verify(contratoRepository, never()).save(any());
    assertEquals(StatusPedido.APROVADO, pedido.getStatus());
  }

  @Test
  void creditoNaoPodeSuperarValorDoPedido() {
    prepararPedidoAprovado();
    when(usuarioService.atual()).thenReturn(agente);

    assertThrows(
        RegraNegocioException.class,
        () -> contratoService.criar(3L, new Dados.NovoContrato(1L, new BigDecimal("400.00"), 2)));
    verify(contratoRepository, never()).save(any());
  }
}
