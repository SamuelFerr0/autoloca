package com.autoloca.back.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.exception.RegraNegocioException;
import com.autoloca.back.model.*;
import com.autoloca.back.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContratoServiceTest {
  @Mock ContratoRepository contratoRepository;
  @Mock PedidoRepository pedidoRepository;
  @Mock UsuarioRepository usuarioRepository;
  @Mock AutomovelRepository automovelRepository;
  @Mock UsuarioService usuarioService;
  @Mock PedidoService pedidoService;
  @InjectMocks ContratoService service;
  Pedido pedido;
  Automovel carro;
  Usuario banco;
  Cliente cliente;

  @BeforeEach
  void preparar() {
    banco = new Usuario();
    banco.setId(1L);
    banco.setNome("Banco de testes");
    banco.setPerfil(Perfil.BANCO);
    banco.setCnpj("00000000000100");
    banco.setEnderecoEmpresa("Rua Banco 10");
    banco.setRepresentante("Representante");
    Usuario u = new Usuario();
    u.setId(9L);
    u.setNome("Ana Souza");
    u.setPerfil(Perfil.CLIENTE);
    cliente = new Cliente();
    cliente.setUsuario(u);
    cliente.setCpf("12345678909");
    cliente.setRg("MG123");
    cliente.setEndereco("Rua Cliente 20");
    cliente.setProfissao("Estudante");
    carro = new Automovel();
    carro.setId(2L);
    carro.setAnunciante(banco);
    carro.setProprietario(banco);
    carro.setMarca("Fiat");
    carro.setModelo("Argo");
    carro.setAno(2025);
    carro.setMatricula("MAT-1");
    carro.setPlaca("ABC1D23");
    pedido = new Pedido();
    pedido.setId(3L);
    pedido.setCliente(cliente);
    pedido.setAutomovel(carro);
    pedido.setStatus(StatusPedido.APROVADO);
    pedido.setValorTotal(new BigDecimal("300.00"));
    pedido.setInicio(LocalDate.now().plusDays(5));
    pedido.setFim(pedido.getInicio().plusDays(3));
    when(pedidoRepository.buscarComBloqueio(3L)).thenReturn(Optional.of(pedido));
  }

  void permitirEmissao() {
    when(automovelRepository.buscarComBloqueio(2L)).thenReturn(Optional.of(carro));
  }

  Contrato emitir() {
    permitirEmissao();
    when(contratoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    return service.criar(3L, new Dados.NovoContrato(null, null, null));
  }

  @Test
  void exigePedidoAprovado() {
    pedido.setStatus(StatusPedido.EM_ANALISE);
    assertThrows(
        RegraNegocioException.class,
        () -> service.criar(3L, new Dados.NovoContrato(null, null, null)));
    verify(contratoRepository, never()).save(any());
  }

  @Test
  void exigeDadosDasPartes() {
    banco.setCnpj(null);
    assertThrows(
        RegraNegocioException.class,
        () -> service.criar(3L, new Dados.NovoContrato(null, null, null)));
    verify(contratoRepository, never()).save(any());
  }

  @Test
  void emiteDocumentoCompletoEPdfLegivel() throws Exception {
    Contrato c = emitir();
    assertEquals(StatusPedido.CONTRATADO, pedido.getStatus());
    assertNull(c.getCredito());
    assertTrue(c.getDocumento().contains("Ana Souza"));
    assertTrue(c.getDocumento().contains("00000000000100"));
    assertTrue(c.getDocumento().contains("OBRIGAÇÕES DO LOCATÁRIO"));
    try (var pdf = Loader.loadPDF(c.getArquivoPdf())) {
      assertTrue(pdf.getNumberOfPages() >= 2);
      assertTrue(new PDFTextStripper().getText(pdf).contains("ABC1D23"));
    }
    verify(usuarioService).exigirAnunciante(carro);
  }

  @Test
  void alteracoesPosterioresNaoMudamDocumentoNemPdf() {
    Contrato c = emitir();
    String texto = c.getDocumento();
    byte[] pdf = c.getArquivoPdf().clone();
    cliente.getUsuario().setNome("Outro nome");
    carro.setModelo("Outro modelo");
    banco.setNome("Outro banco");
    pedido.setValorTotal(BigDecimal.ONE);
    assertEquals(texto, c.getDocumento());
    assertArrayEquals(pdf, c.getArquivoPdf());
    assertEquals("Ana Souza", c.getLocatarioNome());
    assertEquals(new BigDecimal("300.00"), c.getValorAluguel());
  }

  @Test
  void copiaSomenteCreditoAprovadoPeloBanco() {
    pedido.setSolicitaCredito(true);
    pedido.setValorCreditoSolicitado(new BigDecimal("250"));
    pedido.setValorCreditoAprovado(new BigDecimal("200"));
    pedido.setParcelasAprovadas(2);
    Contrato c = emitir();
    assertEquals(new BigDecimal("200"), c.getCredito().getValor());
    assertEquals(2, c.getCredito().getParcelas());
    assertSame(banco, c.getCredito().getBanco());
    assertTrue(c.getDocumento().contains("CRÉDITO ASSOCIADO"));
  }

  @Test
  void naoEmiteCreditoSemAprovacao() {
    pedido.setSolicitaCredito(true);
    permitirEmissao();
    assertThrows(
        RegraNegocioException.class,
        () -> service.criar(3L, new Dados.NovoContrato(null, null, null)));
    verify(contratoRepository, never()).save(any());
  }

  @Test
  void naoPermiteInventarCreditoNaEmissao() {
    permitirEmissao();
    assertThrows(
        RegraNegocioException.class,
        () -> service.criar(3L, new Dados.NovoContrato(null, new BigDecimal("200"), 2)));
    verify(contratoRepository, never()).save(any());
  }

  @Test
  void registroEmNomeDoClientePreservaAnunciante() {
    permitirEmissao();
    when(usuarioRepository.findById(9L)).thenReturn(Optional.of(cliente.getUsuario()));
    when(contratoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    service.criar(3L, new Dados.NovoContrato(9L, null, null));
    assertSame(cliente.getUsuario(), carro.getProprietario());
    assertSame(banco, carro.getAnunciante());
  }
}
