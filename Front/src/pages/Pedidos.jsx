import FotoCarro from "../components/FotoCarro";
import "./Frota.css";
import { useAcao } from "../hooks/useAcao";
import { useState } from "react";
import { Link } from "react-router-dom";
import { Plus } from "lucide-react";
import { useAuth } from "../hooks/useAuth";
import { useDados } from "../hooks/useDados";
import { Modal, Message, Empty } from "../components/UI";
import PedidoForm from "../components/PedidoForm";
import AvaliacaoForm from "../components/AvaliacaoForm";
import ContratoForm from "../components/ContratoForm";
import { cancelarPedido } from "../services/aluguelService";
import { moeda, data, statusNome } from "../utils/formatar";
export default function Pedidos() {
  const { usuario } = useAuth();
  const agente = usuario.perfil !== "CLIENTE";
  const { dados, erro, carregando, carregar } = useDados("pedidos");
  const frota = useDados("automoveis");
  const [filtro, setFiltro] = useState("");
  const [modal, setModal] = useState(null);
  const [sucesso, setSucesso] = useState("");
  const acao = useAcao();
  function salvo(msg) {
    setModal(null);
    setSucesso(msg);
    carregar();
  }
  return (
    <>
      <div className="page-heading">
        <div>
          <h1>{agente ? "Gestão de pedidos" : "Meus pedidos"}</h1>
          <p>
            {agente
              ? "Analise solicitações e acompanhe a contratação."
              : "Consulte, modifique ou cancele seus pedidos."}
          </p>
        </div>
        {!agente && (
          <Link to="/frota" className="button">
            <Plus size={18} />
            Novo pedido
          </Link>
        )}
      </div>
      <Message erro={erro || frota.erro || acao.erro} sucesso={sucesso} />
      <div className="tabs" aria-label="Filtrar pedidos">
        {[["", "Todos"], ...Object.entries(statusNome)].map(([v, n]) => (
          <button key={v} className={filtro === v ? "active" : ""} onClick={() => setFiltro(v)}>
            {n}
          </button>
        ))}
      </div>
      {carregando ? (
        <p className="loading">Carregando pedidos…</p>
      ) : dados.filter((p) => !filtro || p.status === filtro).length === 0 ? (
        <Empty>Nenhum pedido nesta categoria.</Empty>
      ) : (
        <div className="order-list">
          {dados
            .filter((p) => !filtro || p.status === filtro)
            .map((p) => (
              <article className="order-card" key={p.id}>
                <div className="order-head">
                  <span className="eyebrow">PEDIDO #{String(p.id).padStart(4, "0")}</span>
                  <span className={`badge ${p.status}`}>{statusNome[p.status]}</span>
                </div>
                <div className="pedido-foto">
                  <FotoCarro carro={p.automovel} />
                </div>
                <div className="order-main">
                  <div>
                    <h2>
                      {p.automovel.marca} {p.automovel.modelo}
                    </h2>
                    <p>
                      {p.automovel.placa} · {p.automovel.ano}
                    </p>
                    {agente && (
                      <p>
                        Cliente: <strong>{p.cliente.usuario.nome}</strong>
                      </p>
                    )}
                  </div>
                  <div>
                    <small>RETIRADA → DEVOLUÇÃO</small>
                    <strong>
                      {data(p.inicio)} → {data(p.fim)}
                    </strong>
                  </div>
                  <div>
                    <small>VALOR TOTAL</small>
                    <strong>{moeda(p.valorTotal)}</strong>
                  </div>
                </div>
                {p.parecer && (
                  <div className="opinion">
                    <strong>Parecer de {p.agente?.nome}</strong>
                    <p>{p.parecer}</p>
                  </div>
                )}
                <div className="order-actions">
                  {["EM_ANALISE", "APROVADO"].includes(p.status) && (
                    <>
                      <button
                        className="button secondary small"
                        onClick={() => setModal({ tipo: "editar", pedido: p })}
                      >
                        Modificar pedido
                      </button>
                      {!agente && (
                        <button
                          className="text-button danger"
                          disabled={acao.busy}
                          onClick={() => {
                            if (window.confirm(`Cancelar pedido #${p.id}?`))
                              acao.executar(async () => {
                                await cancelarPedido(p.id);
                                salvo("Pedido cancelado.");
                              });
                          }}
                        >
                          Cancelar pedido
                        </button>
                      )}
                    </>
                  )}
                  {agente && p.status === "EM_ANALISE" && (
                    <button className="button small" onClick={() => setModal({ tipo: "avaliar", pedido: p })}>
                      Avaliar pedido
                    </button>
                  )}
                  {agente && p.status === "APROVADO" && (
                    <button
                      className="button small"
                      onClick={() => setModal({ tipo: "contratar", pedido: p })}
                    >
                      Gerar contrato
                    </button>
                  )}
                  {p.status === "CONTRATADO" && (
                    <Link to="/contratos" className="text-link">
                      Consultar contrato
                    </Link>
                  )}
                </div>
              </article>
            ))}
        </div>
      )}
      {modal && (
        <Modal
          titulo={
            { editar: "Modificar pedido", avaliar: "Análise financeira", contratar: "Formalizar contrato" }[
              modal.tipo
            ]
          }
          onClose={() => setModal(null)}
        >
          {modal.tipo === "editar" ? (
            <PedidoForm
              carros={frota.dados}
              pedido={modal.pedido}
              onSalvo={() => salvo("Pedido atualizado e enviado para nova análise.")}
            />
          ) : modal.tipo === "avaliar" ? (
            <AvaliacaoForm pedido={modal.pedido} onSalvo={() => salvo("Parecer registrado.")} />
          ) : (
            <ContratoForm pedido={modal.pedido} onSalvo={() => salvo("Contrato formalizado.")} />
          )}
        </Modal>
      )}
    </>
  );
}
