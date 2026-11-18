package com.autoloca.back.service;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.exception.RegraNegocioException;
import com.autoloca.back.model.*;
import com.autoloca.back.repository.*;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ContratoService {
  private final ContratoRepository contratoRepository;
  private final PedidoRepository pedidoRepository;
  private final UsuarioRepository usuarioRepository;
  private final AutomovelRepository automovelRepository;
  private final UsuarioService usuarioService;
  private final PedidoService pedidoService;

  public ContratoService(
      ContratoRepository contratos,
      PedidoRepository pedidos,
      UsuarioRepository usuarios,
      AutomovelRepository automoveis,
      UsuarioService usuarioService,
      PedidoService pedidoService) {
    this.contratoRepository = contratos;
    this.pedidoRepository = pedidos;
    this.usuarioRepository = usuarios;
    this.automovelRepository = automoveis;
    this.usuarioService = usuarioService;
    this.pedidoService = pedidoService;
  }

  public List<Contrato> listar() {
    Usuario u = usuarioService.atual();
    return u.getPerfil() == Perfil.CLIENTE
        ? contratoRepository.findByPedidoClienteUsuarioIdOrderByIdDesc(u.getId())
        : contratoRepository.findByPedidoAutomovelAnuncianteIdOrderByIdDesc(u.getId());
  }

  public Contrato buscar(Long id) {
    Contrato c = contratoRepository.findById(id).orElseThrow();
    usuarioService.exigirAcessoPedido(c.getPedido());
    return c;
  }

  public Contrato criar(Long id, Dados.NovoContrato dados) {
    usuarioService.exigirAgente();
    Pedido pedido = pedidoRepository.buscarComBloqueio(id).orElseThrow();
    usuarioService.exigirAnunciante(pedido.getAutomovel());
    if (pedido.getStatus() != StatusPedido.APROVADO)
      throw new RegraNegocioException("O pedido precisa estar aprovado para gerar contrato.");
    Usuario locador = pedido.getAutomovel().getAnunciante();
    if (locador.getCnpj() == null
        || locador.getEnderecoEmpresa() == null
        || locador.getRepresentante() == null
        || locador.getCnpj().isBlank()
        || locador.getEnderecoEmpresa().isBlank()
        || locador.getRepresentante().isBlank())
      throw new RegraNegocioException(
          "Complete CNPJ, endereço e representante em Minha empresa antes de emitir o contrato.");
    Automovel carro =
        automovelRepository.buscarComBloqueio(pedido.getAutomovel().getId()).orElseThrow();
    pedidoService.verificarDisponibilidade(pedido);
    Usuario proprietario =
        dados.proprietarioId() == null
            ? carro.getProprietario()
            : usuarioRepository.findById(dados.proprietarioId()).orElseThrow();
    if (!proprietario.getId().equals(locador.getId())
        && !proprietario.getId().equals(pedido.getCliente().getUsuario().getId()))
      throw new RegraNegocioException(
          "Selecione o locador ou o próprio contratante como proprietário.");
    if (dados.valorCredito() != null || dados.parcelas() != null)
      throw new RegraNegocioException(
          "As condições de crédito devem ser aprovadas na análise do pedido.");
    Contrato contrato = new Contrato();
    contrato.setPedido(pedido);
    contrato.setProprietario(proprietario);
    contrato.setAssinadoEm(LocalDateTime.now());
    if (pedido.isSolicitaCredito()) {
      if (locador.getPerfil() != Perfil.BANCO
          || pedido.getValorCreditoAprovado() == null
          || pedido.getParcelasAprovadas() == null)
        throw new RegraNegocioException(
            "Crédito solicitado ainda não aprovado pelo banco anunciante.");
      ContratoCredito credito = new ContratoCredito();
      credito.setBanco(locador);
      credito.setValor(pedido.getValorCreditoAprovado());
      credito.setParcelas(pedido.getParcelasAprovadas());
      contrato.setCredito(credito);
    }
    DocumentoContrato.preparar(contrato, false);
    carro.setProprietario(proprietario);
    pedido.setStatus(StatusPedido.CONTRATADO);
    return contratoRepository.save(contrato);
  }
}
