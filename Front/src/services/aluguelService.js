import { requisicao } from "./httpClient";

export function listar(recurso) {
  return requisicao(`/${recurso}`);
}

export function salvar(recurso, id, dados) {
  if (id) {
    return requisicao(`/${recurso}/${id}`, { method: "PUT", body: dados });
  }
  return requisicao(`/${recurso}`, { method: "POST", body: dados });
}

export function excluir(recurso, id) {
  return requisicao(`/${recurso}/${id}`, { method: "DELETE" });
}

export function cancelarPedido(id) {
  return requisicao(`/pedidos/${id}/cancelamento`, { method: "POST" });
}

export function avaliarPedido(id, dados) {
  return requisicao(`/pedidos/${id}/avaliacao`, { method: "POST", body: dados });
}

export function contratarPedido(id, dados) {
  return requisicao(`/contratos/pedido/${id}`, { method: "POST", body: dados });
}

export function salvarAutomovel(id, dados, foto) {
  const formulario = new FormData();
  formulario.append("dados", new Blob([JSON.stringify(dados)], { type: "application/json" }));
  if (foto) formulario.append("foto", foto);
  return requisicao(id ? `/automoveis/${id}` : "/automoveis", {
    method: id ? "PUT" : "POST",
    body: formulario,
  });
}
