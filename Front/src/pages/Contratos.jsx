import FotoCarro from "../components/FotoCarro";
import "./Frota.css";
import { Printer } from "lucide-react";
import { useDados } from "../hooks/useDados";
import { Message, Empty } from "../components/UI";
import { moeda, data } from "../utils/formatar";
export default function Contratos() {
  const { dados, erro, carregando } = useDados("contratos");
  return (
    <>
      <div className="page-heading">
        <div>
          <h1>Contratos</h1>
          <p>Consulte os registros dos seus aluguéis formalizados.</p>
        </div>
        {dados.length > 0 && (
          <button className="button secondary" onClick={() => window.print()}>
            <Printer size={18} />
            Imprimir
          </button>
        )}
      </div>
      <Message erro={erro} />
      {carregando ? (
        <p className="loading">Carregando contratos…</p>
      ) : dados.length === 0 ? (
        <Empty>Os contratos aparecerão aqui após a aprovação e formalização pelo agente.</Empty>
      ) : (
        <div className="contract-grid">
          {dados.map((c) => (
            <article className="panel contract-card" key={c.id}>
              <div className="section-heading">
                <span className="badge CONTRATADO">Formalizado</span>
              </div>
              <div className="pedido-foto">
                <FotoCarro carro={c.pedido.automovel} />
              </div>
              <span className="eyebrow">CONTRATO #{String(c.id).padStart(4, "0")}</span>
              <h2>
                {c.pedido.automovel.marca} {c.pedido.automovel.modelo}
              </h2>
              <p className="muted">
                {c.pedido.automovel.placa} · Pedido #{c.pedido.id}
              </p>
              <dl>
                <div>
                  <dt>Contratante</dt>
                  <dd>{c.pedido.cliente.usuario.nome}</dd>
                </div>
                <div>
                  <dt>Período</dt>
                  <dd>
                    {data(c.pedido.inicio)} a {data(c.pedido.fim)}
                  </dd>
                </div>
                <div>
                  <dt>Proprietário registrado</dt>
                  <dd>
                    {c.proprietario.nome} ({c.proprietario.perfil.toLowerCase()})
                  </dd>
                </div>
                <div>
                  <dt>Formalização</dt>
                  <dd>{data(c.assinadoEm)}</dd>
                </div>
                <div>
                  <dt>Valor do aluguel</dt>
                  <dd>
                    <strong>{moeda(c.pedido.valorTotal)}</strong>
                  </dd>
                </div>
              </dl>
              {c.credito ? (
                <div className="credit-box">
                  <strong>Crédito · {c.credito.banco.nome}</strong>
                  <p>
                    {moeda(c.credito.valor)} em {c.credito.parcelas} parcelas
                  </p>
                  <small>Registro de crédito concedido pelo banco agente.</small>
                </div>
              ) : (
                <p className="muted small-text">Sem contrato de crédito associado.</p>
              )}
            </article>
          ))}
        </div>
      )}
    </>
  );
}
