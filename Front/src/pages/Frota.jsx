import { useAcao } from "../hooks/useAcao";
import { useState } from "react";
import { Search, Plus } from "lucide-react";
import { useAuth } from "../hooks/useAuth";
import { useDados } from "../hooks/useDados";
import { Modal, Message, Empty } from "../components/UI";
import PedidoForm from "../components/PedidoForm";
import AutomovelForm from "../components/AutomovelForm";
import { excluir } from "../services/aluguelService";
import { moeda } from "../utils/formatar";
export default function Frota() {
  const { usuario } = useAuth();
  const agente = usuario.perfil !== "CLIENTE";
  const { dados, erro, carregando, carregar } = useDados("automoveis");
  const [busca, setBusca] = useState("");
  const [ordem, setOrdem] = useState("modelo");
  const [modal, setModal] = useState(null);
  const [sucesso, setSucesso] = useState("");
  const acao = useAcao();
  const carros = dados
    .filter((c) => `${c.marca} ${c.modelo} ${c.placa}`.toLowerCase().includes(busca.toLowerCase()))
    .sort((a, b) => (ordem === "preco" ? a.diaria - b.diaria : a.modelo.localeCompare(b.modelo)));
  function salvo(msg) {
    setModal(null);
    setSucesso(msg);
    carregar();
  }
  return (
    <>
      <div className="page-heading">
        <div>
          <h1>Frota</h1>
          <p>Consulte os automóveis e o valor das diárias.</p>
        </div>
        {agente && (
          <button className="button" onClick={() => setModal({ tipo: "carro" })}>
            <Plus size={18} />
            Novo automóvel
          </button>
        )}
      </div>
      <Message erro={erro || acao.erro} sucesso={sucesso} />
      <div className="filter-bar">
        <label className="search">
          <Search size={19} />
          <input
            aria-label="Buscar automóvel"
            placeholder="Buscar marca, modelo ou placa"
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
          />
        </label>
        <label className="sort">
          Ordenação
          <select aria-label="Ordenar frota" value={ordem} onChange={(e) => setOrdem(e.target.value)}>
            <option value="modelo">Ordenar por modelo</option>
            <option value="preco">Menor diária</option>
          </select>
        </label>
      </div>
      <div className="list-caption">
        <strong>{carros.length} automóveis</strong>
        <span>Disponibilidade confirmada na análise do pedido</span>
      </div>
      {carregando ? (
        <p className="loading">Carregando frota…</p>
      ) : carros.length === 0 ? (
        <Empty>Nenhum automóvel encontrado.</Empty>
      ) : (
        <div className="table-wrap panel">
          <table>
            <thead>
              <tr>
                <th>Matrícula</th>
                <th>Marca e modelo</th>
                <th>Ano</th>
                <th>Placa</th>
                <th>Diária</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {carros.map((carro) => (
                <tr key={carro.id}>
                  <td>{carro.matricula}</td>
                  <td>
                    {carro.marca} {carro.modelo}
                  </td>
                  <td>{carro.ano}</td>
                  <td>{carro.placa}</td>
                  <td>{moeda(carro.diaria)}</td>
                  <td>
                    <div className="table-actions">
                      {agente ? (
                        <>
                          <button onClick={() => setModal({ tipo: "carro", carro })}>Editar</button>
                          <button
                            className="danger"
                            disabled={acao.busy}
                            onClick={() => {
                              if (window.confirm(`Excluir ${carro.modelo}?`)) {
                                acao.executar(async () => {
                                  await excluir("automoveis", carro.id);
                                  salvo("Automóvel excluído.");
                                });
                              }
                            }}
                          >
                            Excluir
                          </button>
                        </>
                      ) : (
                        <button className="button" onClick={() => setModal({ tipo: "pedido", carro })}>
                          Solicitar aluguel
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      {modal && (
        <Modal
          titulo={
            modal.tipo === "pedido" ? "Novo pedido" : modal.carro ? "Editar automóvel" : "Novo automóvel"
          }
          onClose={() => setModal(null)}
        >
          {modal.tipo === "pedido" ? (
            <PedidoForm
              carros={dados}
              carro={modal.carro}
              onSalvo={() => salvo("Pedido enviado! Acompanhe a análise em Meus pedidos.")}
            />
          ) : (
            <AutomovelForm carro={modal.carro} onSalvo={() => salvo("Automóvel salvo.")} />
          )}
        </Modal>
      )}
    </>
  );
}
