import { Link } from "react-router-dom";
import { useDados } from "../hooks/useDados";
import { Message, Empty } from "../components/UI";
import { moeda, data, statusNome } from "../utils/formatar";
export default function Dashboard() {
  const { dados, erro, carregando } = useDados("pedidos");
  return (
    <>
      <div className="page-heading">
        <div>
          <h1>Resumo dos pedidos</h1>
          <p>Consulte a situação dos pedidos e os registros recentes.</p>
        </div>
        <Link to="/frota" className="button">
          Consultar frota
        </Link>
      </div>
      <Message erro={erro} />
      <section className="stats" aria-label="Resumo de pedidos">
        {[
          ["Pedidos registrados", dados.length],
          ["Aguardando análise", dados.filter((p) => p.status === "EM_ANALISE").length],
          ["Contratos firmados", dados.filter((p) => p.status === "CONTRATADO").length],
        ].map(([label, n]) => (
          <article key={label}>
            <div>
              <span>{label}</span>
              <strong>{carregando ? "—" : n}</strong>
            </div>
          </article>
        ))}
      </section>
      <section className="panel">
        <div className="section-heading">
          <div>
            <h2>Últimos pedidos</h2>
            <p>Acompanhe cada etapa do seu aluguel.</p>
          </div>
          <Link to="/pedidos" className="text-link">
            Ver todos
          </Link>
        </div>
        {carregando ? (
          <p className="loading">Carregando pedidos…</p>
        ) : dados.length === 0 ? (
          <Empty>Você ainda não tem pedidos. Explore a frota para começar.</Empty>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Veículo</th>
                  <th>Período</th>
                  <th>Valor total</th>
                  <th>Status</th>
                  <th>
                    <span className="sr-only">Detalhes</span>
                  </th>
                </tr>
              </thead>
              <tbody>
                {dados.slice(0, 4).map((p) => (
                  <tr key={p.id}>
                    <td>
                      <strong>
                        {p.automovel.marca} {p.automovel.modelo}
                      </strong>
                      <small>Pedido #{String(p.id).padStart(4, "0")}</small>
                    </td>
                    <td>
                      {data(p.inicio)} — {data(p.fim)}
                    </td>
                    <td>{moeda(p.valorTotal)}</td>
                    <td>
                      <span className={`badge ${p.status}`}>{statusNome[p.status]}</span>
                    </td>
                    <td>
                      <Link to="/pedidos" aria-label={`Ver pedido ${p.id}`}>
                        Ver pedidos
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </>
  );
}
