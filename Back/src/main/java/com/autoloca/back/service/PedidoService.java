package com.autoloca.back.service;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.exception.RegraNegocioException;
import com.autoloca.back.model.Automovel;
import com.autoloca.back.model.Cliente;
import com.autoloca.back.model.Pedido;
import com.autoloca.back.model.Perfil;
import com.autoloca.back.model.StatusPedido;
import com.autoloca.back.model.Usuario;
import com.autoloca.back.repository.AutomovelRepository;
import com.autoloca.back.repository.ClienteRepository;
import com.autoloca.back.repository.PedidoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PedidoService {
  private final PedidoRepository pedidoRepository;
  private final ClienteRepository clienteRepository;
  private final AutomovelRepository automovelRepository;
  private final UsuarioService usuarioService;

  public PedidoService(
      PedidoRepository pedidoRepository,
      ClienteRepository clienteRepository,
      AutomovelRepository automovelRepository,
      UsuarioService usuarioService) {
    this.pedidoRepository = pedidoRepository;
    this.clienteRepository = clienteRepository;
    this.automovelRepository = automovelRepository;
    this.usuarioService = usuarioService;
  }

  public List<Pedido> listar() {
    Usuario usuario = usuarioService.atual();
    if (usuario.getPerfil() == Perfil.CLIENTE) {
      return pedidoRepository.findByClienteUsuarioIdOrderByIdDesc(usuario.getId());
    }
    return pedidoRepository.findAllByOrderByIdDesc();
  }

  public Pedido buscar(Long id) {
    Pedido pedido = pedidoRepository.findById(id).orElseThrow();
    usuarioService.exigirDonoOuAgente(pedido.getCliente().getUsuario().getId());
    return pedido;
  }

  public Pedido criar(Dados.NovoPedido dados) {
    usuarioService.exigirCliente();
    Usuario usuario = usuarioService.atual();
    Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId()).orElseThrow();
    Pedido pedido = new Pedido();
    pedido.setCliente(cliente);
    pedido.setCriadoEm(LocalDateTime.now());
    preencher(pedido, dados);
    return pedidoRepository.save(pedido);
  }

  public Pedido editar(Long id, Dados.NovoPedido dados) {
    Pedido pedido = pedidoRepository.buscarComBloqueio(id).orElseThrow();
    usuarioService.exigirDonoOuAgente(pedido.getCliente().getUsuario().getId());
    if (pedido.getStatus() != StatusPedido.EM_ANALISE
        && pedido.getStatus() != StatusPedido.APROVADO) {
      throw new RegraNegocioException("Este pedido não pode ser alterado.");
    }
    preencher(pedido, dados);
    return pedido;
  }

  public Pedido cancelar(Long id) {
    usuarioService.exigirCliente();
    Pedido pedido = pedidoRepository.buscarComBloqueio(id).orElseThrow();
    usuarioService.exigirDonoOuAgente(pedido.getCliente().getUsuario().getId());
    if (pedido.getStatus() != StatusPedido.EM_ANALISE
        && pedido.getStatus() != StatusPedido.APROVADO) {
      throw new RegraNegocioException(
          "Somente pedidos em análise ou aprovados podem ser cancelados.");
    }
    pedido.setStatus(StatusPedido.CANCELADO);
    return pedido;
  }

  public Pedido avaliar(Long id, Dados.Avaliacao dados) {
    usuarioService.exigirAgente();
    Pedido pedido = pedidoRepository.buscarComBloqueio(id).orElseThrow();
    if (pedido.getStatus() != StatusPedido.EM_ANALISE) {
      throw new RegraNegocioException("Somente pedidos em análise podem receber parecer.");
    }
    if (dados.aprovado()) {
      automovelRepository.buscarComBloqueio(pedido.getAutomovel().getId()).orElseThrow();
      verificarDisponibilidade(pedido);
      pedido.setStatus(StatusPedido.APROVADO);
    } else {
      pedido.setStatus(StatusPedido.REPROVADO);
    }
    pedido.setParecer(dados.parecer().trim());
    pedido.setAgente(usuarioService.atual());
    return pedido;
  }

  public void verificarDisponibilidade(Pedido pedido) {
    if (pedido.getInicio().isBefore(LocalDate.now())) {
      throw new RegraNegocioException("A data de retirada já passou. Altere o pedido.");
    }

    Long pedidoIgnorado = pedido.getId();
    if (pedidoIgnorado == null) {
      pedidoIgnorado = -1L;
    }
    List<StatusPedido> statusReservados = List.of(StatusPedido.APROVADO, StatusPedido.CONTRATADO);
    long conflitos =
        pedidoRepository.contarConflitos(
            pedido.getAutomovel().getId(),
            statusReservados,
            pedido.getInicio(),
            pedido.getFim(),
            pedidoIgnorado);
    if (conflitos > 0) {
      throw new RegraNegocioException("Automóvel já reservado neste período.");
    }
  }

  private void preencher(Pedido pedido, Dados.NovoPedido dados) {
    if (dados.inicio().isBefore(LocalDate.now()) || !dados.fim().isAfter(dados.inicio())) {
      throw new RegraNegocioException(
          "A devolução deve ocorrer após a retirada, sem datas passadas.");
    }
    Automovel automovel = automovelRepository.buscarComBloqueio(dados.automovelId()).orElseThrow();
    pedido.setAutomovel(automovel);
    pedido.setInicio(dados.inicio());
    pedido.setFim(dados.fim());
    verificarDisponibilidade(pedido);

    long quantidadeDiarias = ChronoUnit.DAYS.between(dados.inicio(), dados.fim());
    BigDecimal valorTotal = automovel.getDiaria().multiply(BigDecimal.valueOf(quantidadeDiarias));
    pedido.setValorTotal(valorTotal);
    pedido.setStatus(StatusPedido.EM_ANALISE);
    pedido.setParecer(null);
    pedido.setAgente(null);
  }
}
