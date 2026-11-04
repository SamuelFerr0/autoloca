package com.autoloca.back.service;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.exception.RegraNegocioException;
import com.autoloca.back.model.Cliente;
import com.autoloca.back.model.Perfil;
import com.autoloca.back.model.Rendimento;
import com.autoloca.back.model.Usuario;
import com.autoloca.back.repository.ClienteRepository;
import com.autoloca.back.repository.PedidoRepository;
import com.autoloca.back.repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClienteService {
  private final ClienteRepository clienteRepository;
  private final UsuarioRepository usuarioRepository;
  private final PedidoRepository pedidoRepository;
  private final PasswordEncoder passwordEncoder;
  private final UsuarioService usuarioService;

  public ClienteService(
      ClienteRepository clienteRepository,
      UsuarioRepository usuarioRepository,
      PedidoRepository pedidoRepository,
      PasswordEncoder passwordEncoder,
      UsuarioService usuarioService) {
    this.clienteRepository = clienteRepository;
    this.usuarioRepository = usuarioRepository;
    this.pedidoRepository = pedidoRepository;
    this.passwordEncoder = passwordEncoder;
    this.usuarioService = usuarioService;
  }

  public Cliente cadastrar(Dados.Cadastro dados) {
    if (dados.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
      throw new RegraNegocioException("Senha deve ter no máximo 72 bytes.");
    }

    Usuario usuario = new Usuario();
    usuario.setUsername(dados.username());
    usuario.setSenha(passwordEncoder.encode(dados.senha()));
    usuario.setNome(dados.cliente().nome().trim());
    usuario.setPerfil(Perfil.CLIENTE);
    usuarioRepository.save(usuario);

    Cliente cliente = new Cliente();
    cliente.setUsuario(usuario);
    preencher(cliente, dados.cliente());
    return clienteRepository.save(cliente);
  }

  public List<Cliente> listar() {
    Usuario usuario = usuarioService.atual();
    if (usuario.getPerfil() == Perfil.CLIENTE) {
      Cliente cliente = clienteRepository.findByUsuarioId(usuario.getId()).orElseThrow();
      return List.of(cliente);
    }
    return clienteRepository.findAll();
  }

  public Cliente buscar(Long id) {
    Cliente cliente = clienteRepository.findById(id).orElseThrow();
    usuarioService.exigirDonoOuAgente(cliente.getUsuario().getId());
    return cliente;
  }

  public Cliente atualizar(Long id, Dados.PerfilCliente dados) {
    Cliente cliente = buscar(id);
    preencher(cliente, dados);
    return clienteRepository.save(cliente);
  }

  public void excluir(Long id) {
    Cliente cliente = buscar(id);
    if (pedidoRepository.existsByClienteId(id)) {
      throw new RegraNegocioException("Cliente possui pedidos. O histórico deve ser preservado.");
    }
    Usuario usuario = cliente.getUsuario();
    clienteRepository.delete(cliente);
    clienteRepository.flush();
    usuarioRepository.delete(usuario);
  }

  private void preencher(Cliente cliente, Dados.PerfilCliente dados) {
    cliente.getUsuario().setNome(dados.nome().trim());
    cliente.setRg(dados.rg().trim());
    cliente.setCpf(dados.cpf());
    cliente.setEndereco(dados.endereco().trim());
    cliente.setProfissao(dados.profissao().trim());
    cliente.getRendimentos().clear();
    for (Dados.Renda renda : dados.rendimentos()) {
      Rendimento rendimento = new Rendimento(renda.empregador().trim(), renda.valor());
      cliente.getRendimentos().add(rendimento);
    }
  }
}
