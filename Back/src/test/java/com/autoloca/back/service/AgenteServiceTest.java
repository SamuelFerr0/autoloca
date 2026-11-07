package com.autoloca.back.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.exception.RegraNegocioException;
import com.autoloca.back.model.Perfil;
import com.autoloca.back.model.Usuario;
import com.autoloca.back.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AgenteServiceTest {
  @Mock private UsuarioRepository usuarioRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @InjectMocks private AgenteService agenteService;

  private void verificarCadastro(Perfil perfil) {
    when(passwordEncoder.encode("Senha123!")).thenReturn("hash-da-senha");
    when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
    Usuario usuario =
        agenteService.cadastrar(
            new Dados.CadastroAgente("agente", "Senha123!", " Empresa A ", perfil));
    assertEquals(perfil, usuario.getPerfil());
    assertEquals("Empresa A", usuario.getNome());
    assertEquals("agente", usuario.getUsername());
    assertEquals("hash-da-senha", usuario.getSenha());
    verify(usuarioRepository).save(usuario);
  }

  @Test
  void cadastraEmpresaComSenhaCodificada() {
    verificarCadastro(Perfil.EMPRESA);
  }

  @Test
  void cadastraBancoComSenhaCodificada() {
    verificarCadastro(Perfil.BANCO);
  }

  @Test
  void rejeitaClienteNaRotaDeAgentes() {
    assertThrows(
        RegraNegocioException.class,
        () ->
            agenteService.cadastrar(
                new Dados.CadastroAgente("cliente", "Senha123!", "Ana", Perfil.CLIENTE)));
    verifyNoInteractions(usuarioRepository, passwordEncoder);
  }

  @Test
  void rejeitaSenhaComMaisDe72Bytes() {
    assertThrows(
        RegraNegocioException.class,
        () ->
            agenteService.cadastrar(
                new Dados.CadastroAgente("banco", "á".repeat(37), "Banco A", Perfil.BANCO)));
    verifyNoInteractions(usuarioRepository, passwordEncoder);
  }
}
