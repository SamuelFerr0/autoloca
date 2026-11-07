package com.autoloca.back.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.exception.RegraNegocioException;
import com.autoloca.back.model.*;
import com.autoloca.back.repository.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {
  @Mock private ClienteRepository clienteRepository;
  @Mock private UsuarioRepository usuarioRepository;
  @Mock private PedidoRepository pedidoRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private UsuarioService usuarioService;
  @InjectMocks private ClienteService clienteService;

  @Test
  void cadastroUsaPerfilClienteESenhaCodificada() {
    Dados.PerfilCliente perfil =
        new Dados.PerfilCliente(
            " Ana ", "MG123", "12345678909", "Rua A, 10", "Estudante", List.of());
    Dados.Cadastro dados = new Dados.Cadastro("ana", "Senha123!", perfil);
    when(passwordEncoder.encode("Senha123!")).thenReturn("senha-codificada");
    when(clienteRepository.save(any(Cliente.class)))
        .thenAnswer(invocacao -> invocacao.getArgument(0));

    Cliente resultado = clienteService.cadastrar(dados);

    assertEquals(Perfil.CLIENTE, resultado.getUsuario().getPerfil());
    assertEquals("senha-codificada", resultado.getUsuario().getSenha());
    assertEquals("Ana", resultado.getUsuario().getNome());
    verify(usuarioRepository).save(resultado.getUsuario());
  }

  @Test
  void naoExcluiClienteComPedidos() {
    Usuario usuario = new Usuario();
    usuario.setId(1L);
    Cliente cliente = new Cliente();
    cliente.setUsuario(usuario);
    when(clienteRepository.findById(2L)).thenReturn(Optional.of(cliente));
    when(pedidoRepository.existsByClienteId(2L)).thenReturn(true);

    assertThrows(RegraNegocioException.class, () -> clienteService.excluir(2L));

    verify(clienteRepository, never()).delete(any());
    verify(usuarioRepository, never()).delete(any());
  }

  @Test
  void excluiClienteSemPedidos() {
    Usuario usuario = new Usuario();
    usuario.setId(1L);
    Cliente cliente = new Cliente();
    cliente.setUsuario(usuario);
    when(clienteRepository.findById(2L)).thenReturn(Optional.of(cliente));

    clienteService.excluir(2L);

    verify(usuarioService).exigirDonoOuAgente(1L);
    verify(clienteRepository).delete(cliente);
    verify(usuarioRepository).delete(usuario);
  }
}
