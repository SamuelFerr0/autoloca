package com.autoloca.back;

import static org.junit.jupiter.api.Assertions.*;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.model.*;
import com.autoloca.back.repository.*;
import com.autoloca.back.service.*;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
    classes = com.autoloca.back.application.BackApplication.class,
    properties = {
      "spring.profiles.active=local",
      "spring.datasource.url=jdbc:h2:mem:fluxo;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "app.seed=true"
    })
@Transactional
class FluxoAluguelTest {
  @Autowired PedidoService pedidos;
  @Autowired ContratoService contratos;
  @Autowired AutomovelService automoveis;
  @Autowired ClienteService clientes;
  @Autowired ContratoRepository contratoRepository;
  @Autowired PedidoRepository pedidoRepository;
  @Autowired EntityManager em;

  void entrar(String usuario) {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken(usuario, ""));
  }

  @AfterEach
  void limpar() {
    SecurityContextHolder.clearContext();
  }

  Pedido pedirCredito() {
    entrar("cliente");
    Automovel carro =
        automoveis.listar().stream()
            .filter(c -> c.getAnunciante().getPerfil() == Perfil.BANCO)
            .findFirst()
            .orElseThrow();
    LocalDate inicio = LocalDate.now().plusDays(300);
    return pedidos.criar(
        new Dados.NovoPedido(
            carro.getId(), inicio, inicio.plusDays(3), true, new BigDecimal("200"), 2));
  }

  @Test
  void contratoPersistePdfEDadosFixosMesmoDepoisDeEditarClienteEVeiculo() {
    Pedido p = pedirCredito();
    entrar("banco");
    pedidos.avaliar(
        p.getId(), new Dados.Avaliacao(true, "Renda conferida", new BigDecimal("180"), 2));
    Contrato c = contratos.criar(p.getId(), new Dados.NovoContrato(null, null, null));
    Long id = c.getId();
    String documento = c.getDocumento();
    byte[] pdf = c.getArquivoPdf().clone();
    String nome = c.getLocatarioNome();
    c.setDocumento("Tentativa de reescrita");
    c.setArquivoPdf(new byte[] {0});
    c.setLocatarioNome("Nome adulterado");
    p.getCliente().getUsuario().setNome("Nome alterado");
    p.getAutomovel().setModelo("Modelo alterado");
    em.flush();
    em.clear();
    Contrato recarregado = contratoRepository.findById(id).orElseThrow();
    assertEquals(documento, recarregado.getDocumento());
    assertArrayEquals(pdf, recarregado.getArquivoPdf());
    assertEquals(nome, recarregado.getLocatarioNome());
    assertEquals(new BigDecimal("180.00"), recarregado.getCredito().getValor());
    assertTrue(recarregado.getArquivoPdf().length > 1000);
  }

  @Test
  void empresaNaoPodeAnalisarPedidoDoBanco() {
    Pedido p = pedirCredito();
    entrar("empresa");
    assertThrows(
        AccessDeniedException.class,
        () -> pedidos.avaliar(p.getId(), new Dados.Avaliacao(true, "Analisado")));
    assertTrue(pedidos.listar().isEmpty());
  }

  @Test
  void mudarRendaDepoisDaAprovacaoExigeNovaAnaliseDoBanco() {
    Pedido p = pedirCredito();
    entrar("banco");
    pedidos.avaliar(
        p.getId(), new Dados.Avaliacao(true, "Renda conferida", new BigDecimal("180"), 2));
    entrar("cliente");
    Cliente c = clientes.listar().getFirst();
    clientes.atualizar(
        c.getId(),
        new Dados.PerfilCliente(
            c.getUsuario().getNome(),
            c.getRg(),
            c.getCpf(),
            c.getEndereco(),
            c.getProfissao(),
            List.of(new Dados.Renda("Novo emprego", new BigDecimal("1000")))));
    em.flush();
    em.clear();
    Pedido alterado = pedidoRepository.findById(p.getId()).orElseThrow();
    assertEquals(StatusPedido.EM_ANALISE, alterado.getStatus());
    assertNull(alterado.getValorCreditoAprovado());
    assertNull(alterado.getParecer());
  }
}
