package com.autoloca.back.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.exception.GlobalExceptionHandler;
import com.autoloca.back.exception.RegraNegocioException;
import com.autoloca.back.model.Pedido;
import com.autoloca.back.model.StatusPedido;
import com.autoloca.back.service.PedidoService;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@DisplayName("Endpoints de pedidos")
class PedidoControllerTest {
  @Mock private PedidoService pedidoService;
  @InjectMocks private PedidoController pedidoController;
  private MockMvc mockMvc;

  @BeforeEach
  void preparar() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(pedidoController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  @DisplayName("GET /api/pedidos retorna a lista e HTTP 200")
  void listarRetornaPedidos() throws Exception {
    Pedido pedido = new Pedido();
    pedido.setId(1L);
    pedido.setStatus(StatusPedido.EM_ANALISE);
    when(pedidoService.listar()).thenReturn(List.of(pedido));

    mockMvc
        .perform(get("/api/pedidos"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].status").value("EM_ANALISE"));

    verify(pedidoService).listar();
  }

  @Test
  void criarRetorna201() throws Exception {
    LocalDate inicio = LocalDate.now().plusDays(10);
    LocalDate fim = inicio.plusDays(3);
    Pedido pedido = new Pedido();
    pedido.setId(1L);
    when(pedidoService.criar(new Dados.NovoPedido(2L, inicio, fim))).thenReturn(pedido);
    String corpo =
        """
        {"automovelId":2,"inicio":"%s","fim":"%s"}
        """
            .formatted(inicio, fim);

    mockMvc
        .perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON).content(corpo))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  void dadosObrigatoriosAusentesRetornam400() throws Exception {
    mockMvc
        .perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest());

    verify(pedidoService, never()).criar(any());
  }

  @Test
  void pedidoInexistenteRetorna404() throws Exception {
    when(pedidoService.buscar(99L)).thenThrow(new NoSuchElementException());

    mockMvc
        .perform(get("/api/pedidos/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.mensagem").value("Registro não encontrado."));
  }

  @Test
  void acessoNegadoPeloServiceRetorna403() throws Exception {
    when(pedidoService.buscar(1L)).thenThrow(new AccessDeniedException("Outro cliente"));

    mockMvc.perform(get("/api/pedidos/1")).andExpect(status().isForbidden());
  }

  @Test
  void cancelamentoInvalidoRetorna422() throws Exception {
    when(pedidoService.cancelar(1L)).thenThrow(new RegraNegocioException("Pedido já contratado."));

    mockMvc
        .perform(post("/api/pedidos/1/cancelamento"))
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.mensagem").value("Pedido já contratado."));
  }
}
