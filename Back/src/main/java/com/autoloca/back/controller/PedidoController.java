package com.autoloca.back.controller;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.model.Pedido;
import com.autoloca.back.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
  private final PedidoService pedidoService;

  public PedidoController(PedidoService pedidoService) {
    this.pedidoService = pedidoService;
  }

  @GetMapping
  public List<Pedido> listar() {
    return pedidoService.listar();
  }

  @GetMapping("/{id}")
  public Pedido buscar(@PathVariable Long id) {
    return pedidoService.buscar(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Pedido criar(@Valid @RequestBody Dados.NovoPedido dados) {
    return pedidoService.criar(dados);
  }

  @PutMapping("/{id}")
  public Pedido editar(@PathVariable Long id, @Valid @RequestBody Dados.NovoPedido dados) {
    return pedidoService.editar(id, dados);
  }

  @PostMapping("/{id}/cancelamento")
  public Pedido cancelar(@PathVariable Long id) {
    return pedidoService.cancelar(id);
  }

  @PostMapping("/{id}/avaliacao")
  public Pedido avaliar(@PathVariable Long id, @Valid @RequestBody Dados.Avaliacao dados) {
    return pedidoService.avaliar(id, dados);
  }
}
