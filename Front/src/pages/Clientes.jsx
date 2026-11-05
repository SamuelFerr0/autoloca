import { useAcao } from "../hooks/useAcao";
import { useState } from "react";
import { Plus, Search } from "lucide-react";
import { useAuth } from "../hooks/useAuth";
import { useDados } from "../hooks/useDados";
import { Modal, Message, Empty } from "../components/UI";
import ClienteForm from "../components/ClienteForm";
import { salvar, excluir } from "../services/aluguelService";
import { moeda } from "../utils/formatar";
export default function Clientes() {
  const { usuario, setUsuario, logout } = useAuth();
  const agente = usuario.perfil !== "CLIENTE";
  const { dados, erro, carregando, carregar } = useDados("clientes");
  const [modal, setModal] = useState(null);
  const [busca, setBusca] = useState("");
  const [sucesso, setSucesso] = useState("");
  const acao = useAcao();
  async function salvarCliente(d, id) {
    const c = await salvar("clientes", id, d);
    if (c.usuario.id === usuario.id) setUsuario(c.usuario);
    setModal(null);
    setSucesso("Cadastro salvo.");
    await carregar();
  }
  async function excluirCliente(c) {
    if (!window.confirm(`Excluir cadastro de ${c.usuario.nome}? Só é possível excluir clientes sem pedidos.`))
      return;
    await acao.executar(async () => {
      await excluir("clientes", c.id);
      if (c.usuario.id === usuario.id) await logout();
      else {
        setSucesso("Cliente excluído.");
        await carregar();
      }
    });
  }
  return (
    <>
      <div className="page-heading">
        <div>
          <h1>{agente ? "Clientes" : "Meu cadastro"}</h1>
          <p>Dados pessoais e rendimentos para a análise do aluguel.</p>
        </div>
        {agente && (
          <button className="button" onClick={() => setModal({ novo: true })}>
            <Plus size={18} />
            Novo cliente
          </button>
        )}
      </div>
      <Message erro={erro || acao.erro} sucesso={sucesso} />
      {agente && (
        <label className="search standalone">
          <Search size={19} />
          <input
            aria-label="Buscar cliente"
            placeholder="Buscar por nome ou CPF"
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
          />
        </label>
      )}
      {carregando ? (
        <p className="loading">Carregando cadastros…</p>
      ) : dados.filter((c) => `${c.usuario.nome} ${c.cpf}`.toLowerCase().includes(busca.toLowerCase()))
          .length === 0 ? (
        <Empty>Nenhum cliente encontrado.</Empty>
      ) : (
        dados
          .filter((c) => `${c.usuario.nome} ${c.cpf}`.toLowerCase().includes(busca.toLowerCase()))
          .map((c) => (
            <section className="panel client-card" key={c.id}>
              <div className="section-heading">
                <div>
                  <h2>{c.usuario.nome}</h2>
                  <p>
                    @{c.usuario.username} · {c.profissao}
                  </p>
                </div>
                <button className="button secondary small" onClick={() => setModal({ cliente: c })}>
                  Editar cadastro
                </button>
              </div>
              <dl className="details-grid">
                <div>
                  <dt>CPF</dt>
                  <dd>{c.cpf}</dd>
                </div>
                <div>
                  <dt>RG</dt>
                  <dd>{c.rg}</dd>
                </div>
                <div>
                  <dt>Endereço</dt>
                  <dd>{c.endereco}</dd>
                </div>
                <div>
                  <dt>Renda mensal declarada</dt>
                  <dd>{moeda(c.rendimentos.reduce((s, r) => s + Number(r.valor), 0))}</dd>
                </div>
              </dl>
              {c.rendimentos.length > 0 && (
                <div className="income-details">
                  {c.rendimentos.map((r, i) => (
                    <span key={i}>
                      {r.empregador}
                      <strong>{moeda(r.valor)}</strong>
                    </span>
                  ))}
                </div>
              )}
              <button className="text-button danger" disabled={acao.busy} onClick={() => excluirCliente(c)}>
                Excluir cadastro
              </button>
            </section>
          ))
      )}
      {modal && (
        <Modal titulo={modal.novo ? "Novo cliente" : "Editar cadastro"} onClose={() => setModal(null)}>
          <ClienteForm
            cadastro={modal.novo}
            cliente={modal.cliente}
            onSalvar={(d) => salvarCliente(d, modal.cliente?.id)}
          />
        </Modal>
      )}
    </>
  );
}
