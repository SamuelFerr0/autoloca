import { useAuth } from "../hooks/useAuth";
import FotoCarro from "../components/FotoCarro";
import "./Frota.css";
import { Link } from "react-router-dom";
import { useDados } from "../hooks/useDados";
import { Message, Empty } from "../components/UI";
import { moeda, data, statusNome } from "../utils/formatar";
export default function Dashboard() {
  const { usuario } = useAuth();
  const cliente = usuario.perfil === "CLIENTE";
  const frota = useDados("automoveis");
  const { dados, erro, carregando } = useDados("pedidos");
  return (
    <>
      <section className="hero grid md:grid-cols-2">
        <div className="hero-copy flex flex-col items-start justify-center">
          <span className="hero-kicker">AutoLoca · seu caminho, sua escolha</span>
          <h1>
            {cliente ? (
              <>
                Mais estrada.
                <br />
                Novas histórias.
              </>
            ) : (
              <>
                Tudo pronto
                <br />
                para partir.
              </>
            )}
          </h1>
          <p>
            {cliente
              ? "Encontre o carro para a sua próxima viagem e acompanhe seu aluguel em um só lugar."
              : "Organize a frota, analise os pedidos e acompanhe cada aluguel."}
          </p>
          <Link to="/frota" className="button mt-4">
            {cliente ? "Encontrar meu carro →" : "Ver a frota →"}
          </Link>
          <span className="hero-note">Escolha por modelo, compare as diárias e faça seu pedido.</span>
        </div>
        <img className="hero-image" src="/viagem.png" alt="Carro em um mirante à beira-mar" />
      </section>
      {cliente && (
        <section className="road-steps grid gap-6 md:grid-cols-3" aria-label="Como alugar">
          <div>
            <b>01 / ESCOLHA</b>
            <h2>Um carro para o seu caminho</h2>
            <p>Veja as fotos e compare as diárias na frota.</p>
          </div>
          <div>
            <b>02 / PLANEJE</b>
            <h2>Marque os dias da viagem</h2>
            <p>Informe o período e confira o valor estimado.</p>
          </div>
          <div>
            <b>03 / ACOMPANHE</b>
            <h2>Consulte seu pedido</h2>
            <p>Aguarde a análise e a formalização do contrato.</p>
          </div>
        </section>
      )}
      {cliente && frota.dados.length > 0 && (
        <section className="mb-12" aria-label="Conheça alguns modelos">
          <div className="section-heading">
            <div>
              <span className="hero-kicker">Para perto ou para longe</span>
              <h2>Qual vai ser o seu?</h2>
            </div>
            <Link to="/frota">Ver todos os carros →</Link>
          </div>
          <div className="grid gap-6 md:grid-cols-3">
            {frota.dados.slice(0, 3).map((carro) => (
              <article className="carro-card" key={carro.id}>
                <FotoCarro carro={carro} />
                <div className="carro-dados">
                  <h2>
                    {carro.marca} {carro.modelo}
                  </h2>
                  <p>
                    {carro.ano} · Diária de <strong>{moeda(carro.diaria)}</strong>
                  </p>
                  <Link className="button" to={`/frota?busca=${encodeURIComponent(carro.modelo)}`}>
                    Ver automóvel →
                  </Link>
                </div>
              </article>
            ))}
          </div>
        </section>
      )}
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
