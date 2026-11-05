import { Link } from "react-router-dom";
import { PALETA, ESTADOS, FONTES, ESCALA_TIPO, RAIOS, MEDIDAS } from "../config/designSystem";
import { Campo, Message, Submit } from "../components/UI";
import { statusNome } from "../utils/formatar";
import "./DesignSystem.css";

export default function DesignSystem() {
  return (
    <>
      <header className="site-header">
        <Link to="/" className="brand">
          <img src="/logo.png" alt="" />
          AutoLoca
        </Link>
        <Link to="/">Voltar ao sistema</Link>
      </header>
      <main className="content design-system">
        <div className="page-heading">
          <div>
            <h1>Design system</h1>
            <p>Cores, fontes e componentes usados nas telas do AutoLoca.</p>
          </div>
        </div>

        <section className="panel" aria-labelledby="cores">
          <h2 id="cores">Paleta</h2>
          <div className="design-paleta">
            {PALETA.map((cor) => (
              <div key={cor.variavel}>
                <span className="design-amostra" style={{ background: `var(${cor.variavel})` }} />
                <strong>{cor.nome}</strong>
                <p>
                  <code>{cor.variavel}</code> · {cor.hex}
                </p>
                <p className="small-text">{cor.uso}</p>
              </div>
            ))}
          </div>
        </section>

        <section className="panel" aria-labelledby="estados">
          <h2 id="estados">Cores de estado</h2>
          <div className="design-paleta">
            {ESTADOS.map((estado) => (
              <div key={estado.nome}>
                <span
                  className="design-amostra"
                  style={{ background: `var(${estado.variavel})`, color: `var(${estado.variavelTexto})` }}
                >
                  {estado.nome}
                </span>
                <p>
                  <code>{estado.variavel}</code> · {estado.hex}
                </p>
                <p className="small-text">Texto: {estado.texto}</p>
              </div>
            ))}
          </div>
        </section>

        <section className="panel" aria-labelledby="tipografia">
          <h2 id="tipografia">Tipografia</h2>
          {FONTES.map((fonte) => (
            <p key={fonte.nome}>
              <strong>{fonte.nome}</strong> · {fonte.familia}. {fonte.uso}
            </p>
          ))}
          {ESCALA_TIPO.map((tipo) => (
            <div className="design-tipo" key={tipo.nome}>
              <p className="small-text">
                {tipo.nome} · {tipo.classe} · {tipo.tamanho}
              </p>
              <p style={{ fontSize: `var(${tipo.variavel})`, fontWeight: tipo.peso }}>{tipo.exemplo}</p>
            </div>
          ))}
        </section>

        <section className="panel" aria-labelledby="medidas">
          <h2 id="medidas">Medidas</h2>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Elemento</th>
                  <th>Referência</th>
                  <th>Valor</th>
                </tr>
              </thead>
              <tbody>
                {[...RAIOS, ...MEDIDAS].map((medida) => (
                  <tr key={medida.nome}>
                    <td>{medida.nome}</td>
                    <td>
                      <code>{medida.variavel}</code>
                    </td>
                    <td>{medida.valor}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <section className="panel" aria-labelledby="componentes">
          <h2 id="componentes">Exemplos de componentes</h2>
          <div className="design-exemplos">
            <div>
              <h3>Botões</h3>
              <div className="table-actions">
                <button type="button" className="button">
                  Salvar
                </button>
                <button type="button" className="button secondary">
                  Voltar
                </button>
                <button type="button" className="danger">
                  Excluir
                </button>
                <Submit busy />
              </div>
            </div>
            <div>
              <h3>Campo de formulário</h3>
              <Campo
                label="Usuário"
                name="exemploUsuario"
                placeholder="Seu nome de usuário"
                autoComplete="off"
              />
            </div>
            <div>
              <h3>Status do pedido</h3>
              <div className="table-actions">
                {Object.entries(statusNome).map(([status, nome]) => (
                  <span key={status} className={`badge ${status}`}>
                    {nome}
                  </span>
                ))}
              </div>
            </div>
            <div>
              <h3>Mensagens</h3>
              <Message sucesso="Exemplo: cadastro salvo." />
              <Message erro="Exemplo: confira os campos obrigatórios." />
            </div>
          </div>
        </section>
      </main>
      <footer>AutoLoca · Sistema de aluguel de carros</footer>
    </>
  );
}
