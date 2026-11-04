package com.autoloca.back.service;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.exception.RegraNegocioException;
import com.autoloca.back.model.Automovel;
import com.autoloca.back.model.Contrato;
import com.autoloca.back.model.ContratoCredito;
import com.autoloca.back.model.Pedido;
import com.autoloca.back.model.Perfil;
import com.autoloca.back.model.StatusPedido;
import com.autoloca.back.model.Usuario;
import com.autoloca.back.repository.AutomovelRepository;
import com.autoloca.back.repository.ContratoRepository;
import com.autoloca.back.repository.PedidoRepository;
import com.autoloca.back.repository.UsuarioRepository;
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
      ContratoRepository contratoRepository,
      PedidoRepository pedidoRepository,
      UsuarioRepository usuarioRepository,
      AutomovelRepository automovelRepository,
      UsuarioService usuarioService,
      PedidoService pedidoService) {
    this.contratoRepository = contratoRepository;
    this.pedidoRepository = pedidoRepository;
    this.usuarioRepository = usuarioRepository;
    this.automovelRepository = automovelRepository;
    this.usuarioService = usuarioService;
    this.pedidoService = pedidoService;
  }

  public List<Contrato> listar() {
    Usuario usuario = usuarioService.atual();
    if (usuario.getPerfil() == Perfil.CLIENTE) {
      return contratoRepository.findByPedidoClienteUsuarioIdOrderByIdDesc(usuario.getId());
    }
    return contratoRepository.findAllByOrderByIdDesc();
  }

  public Contrato criar(Long id, Dados.NovoContrato dados) {
    usuarioService.exigirAgente();
    Pedido pedido = pedidoRepository.buscarComBloqueio(id).orElseThrow();
    if (pedido.getStatus() != StatusPedido.APROVADO) {
      throw new RegraNegocioException("O pedido precisa estar aprovado para gerar contrato.");
    }
    Automovel automovel =
        automovelRepository.buscarComBloqueio(pedido.getAutomovel().getId()).orElseThrow();
    pedidoService.verificarDisponibilidade(pedido);
    Usuario proprietario = usuarioRepository.findById(dados.proprietarioId()).orElseThrow();
    if (proprietario.getPerfil() == Perfil.CLIENTE
        && !proprietario.getId().equals(pedido.getCliente().getUsuario().getId())) {
      throw new RegraNegocioException("O proprietário cliente deve ser o próprio contratante.");
    }

    Contrato contrato = new Contrato();
    contrato.setPedido(pedido);
    contrato.setProprietario(proprietario);
    contrato.setAssinadoEm(LocalDateTime.now());
    if (dados.valorCredito() != null || dados.parcelas() != null) {
      contrato.setCredito(criarCredito(dados, pedido));
    }
    automovel.setProprietario(proprietario);
    pedido.setStatus(StatusPedido.CONTRATADO);
    return contratoRepository.save(contrato);
  }

  private ContratoCredito criarCredito(Dados.NovoContrato dados, Pedido pedido) {
    Usuario banco = usuarioService.atual();
    if (banco.getPerfil() != Perfil.BANCO) {
      throw new RegraNegocioException("Somente o banco autenticado pode conceder crédito.");
    }
    if (dados.valorCredito() == null
        || dados.parcelas() == null
        || dados.valorCredito().compareTo(pedido.getValorTotal()) > 0) {
      throw new RegraNegocioException("Informe parcelas e crédito de até o valor total do pedido.");
    }
    ContratoCredito credito = new ContratoCredito();
    credito.setBanco(banco);
    credito.setValor(dados.valorCredito());
    credito.setParcelas(dados.parcelas());
    return credito;
  }
}
