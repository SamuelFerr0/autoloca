package com.autoloca.back.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.exception.RegraNegocioException;
import com.autoloca.back.model.Automovel;
import com.autoloca.back.model.Usuario;
import com.autoloca.back.repository.AutomovelRepository;
import com.autoloca.back.repository.UsuarioRepository;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class AutomovelServiceTest {
  @Mock private AutomovelRepository automovelRepository;
  @Mock private UsuarioRepository usuarioRepository;
  @Mock private UsuarioService usuarioService;
  @InjectMocks private AutomovelService automovelService;

  private Dados.Carro dados() {
    return new Dados.Carro("AUTO-TESTE", 2025, "Fiat", "Argo", "ABC1D23", new BigDecimal("150"), 1L);
  }

  private void prepararProprietario() {
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
  }

  @Test
  void salvaFotoPngJuntoDoAutomovel() throws Exception {
    prepararProprietario();
    var bytes = new ByteArrayOutputStream();
    ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", bytes);
    var arquivo = new MockMultipartFile("foto", "foto.png", "image/png", bytes.toByteArray());
    when(automovelRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    Automovel carro = automovelService.salvar(null, dados(), arquivo);

    assertTrue(carro.isTemFoto());
    assertEquals("image/png", carro.getFotoTipo());
    assertArrayEquals(bytes.toByteArray(), carro.getFoto());
  }

  @Test
  void editarSemArquivoMantemFotoAtual() {
    prepararProprietario();
    Automovel carro = new Automovel();
    byte[] foto = {1, 2, 3};
    carro.setFoto(foto);
    carro.setFotoTipo("image/png");
    when(automovelRepository.buscarComBloqueio(5L)).thenReturn(Optional.of(carro));
    when(automovelRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    Automovel resultado = automovelService.salvar(5L, dados(), null);

    assertArrayEquals(foto, resultado.getFoto());
    assertEquals("image/png", resultado.getFotoTipo());
  }

  @Test
  void rejeitaTextoDisfarcadoDeImagem() {
    prepararProprietario();
    var arquivo = new MockMultipartFile("foto", "carro.png", "image/png", "texto".getBytes());
    assertThrows(RegraNegocioException.class, () -> automovelService.salvar(null, dados(), arquivo));
    verify(automovelRepository, never()).save(any());
  }

  @Test
  void rejeitaArquivoAcimaDeTresMegabytes() {
    prepararProprietario();
    var arquivo = new MockMultipartFile("foto", "carro.jpg", "image/jpeg", new byte[3145729]);
    assertThrows(RegraNegocioException.class, () -> automovelService.salvar(null, dados(), arquivo));
    verify(automovelRepository, never()).save(any());
  }

  @Test
  void clienteNaoPodeCadastrarAutomovel() {
    doThrow(new AccessDeniedException("Acesso negado")).when(usuarioService).exigirAgente();
    assertThrows(AccessDeniedException.class, () -> automovelService.salvar(null, dados(), null));
    verifyNoInteractions(automovelRepository, usuarioRepository);
  }
}
