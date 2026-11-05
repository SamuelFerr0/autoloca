import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ArrowLeft, Check, Copy, Moon, Sun } from "lucide-react";
import { paleta, semanticos, tipografia, espacamentos, raios, statusPedido } from "./tokens";
import { Logo } from "./Marca";
import "./design.css";

function temaInicial() {
  try {
    const salvo = localStorage.getItem("autoloca-tema");
    if (salvo === "light" || salvo === "dark") return salvo;
  } catch {
    return "light";
  }
  return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
}

function variaveis(tema) {
  const vars = {};
  Object.entries(paleta).forEach(([grupo, escala]) => {
    Object.entries(escala).forEach(([n, hex]) => {
      vars[`--${grupo}-${n}`] = hex;
    });
  });
  semanticos.forEach((s) => {
    vars[s.variavel] = s[tema];
  });
  return vars;
}

function Copiavel({ valor, children, className = "" }) {
  const [copiado, setCopiado] = useState(false);
  async function copiar() {
    try {
      await navigator.clipboard.writeText(valor);
      setCopiado(true);
      setTimeout(() => setCopiado(false), 1200);
    } catch {
      setCopiado(false);
    }
  }
  return (
    <button type="button" className={`ds-copiavel ${className}`} onClick={copiar} title={`Copiar ${valor}`}>
      {children}
      <span className="ds-copiavel-icone" aria-hidden="true">
        {copiado ? <Check size={14} /> : <Copy size={14} />}
      </span>
    </button>
  );
}

function Secao({ rotulo, titulo, id, children }) {
  return (
    <section className="ds-secao" id={id}>
      <p className="ds-rotulo">{rotulo}</p>
      <h2 className="ds-h2">{titulo}</h2>
      {children}
    </section>
  );
}

function Escala({ nome, escala }) {
  return (
    <div className="ds-escala">
      {Object.entries(escala).map(([n, hex]) => (
        <Copiavel key={n} valor={hex} className="ds-swatch">
          <span className="ds-chip" style={{ background: hex }} />
          <span className="ds-swatch-nome">
            {nome}.{n}
          </span>
          <span className="ds-swatch-hex">{hex}</span>
        </Copiavel>
      ))}
    </div>
  );
}

function Valor({ cor }) {
  return (
    <span className="ds-valor">
      <span className="ds-ponto" style={{ background: cor }} />
      <code>{cor}</code>
    </span>
  );
}

export default function Design() {
  const [tema, setTema] = useState(temaInicial);
  const escuro = tema === "dark";

  useEffect(() => {
    try {
      localStorage.setItem("autoloca-tema", tema);
    } catch {
      return;
    }
  }, [tema]);

  useEffect(() => {
    const anterior = document.title;
    document.title = "AutoLoca · Design system";
    return () => {
      document.title = anterior;
    };
  }, []);

  useEffect(() => {
    const fundo = semanticos.find((s) => s.token === "bg.page")[tema];
    document.body.style.background = fundo;
    document.documentElement.style.colorScheme = tema;
    return () => {
      document.body.style.background = "";
      document.documentElement.style.colorScheme = "";
    };
  }, [tema]);

  const alternar = () => setTema(escuro ? "light" : "dark");
  const BotaoTema = () => (
    <button type="button" className="ds-icone-btn" onClick={alternar} aria-label="Alternar tema">
      {escuro ? <Sun size={18} /> : <Moon size={18} />}
    </button>
  );

  return (
    <div className="ds" data-theme={tema} style={variaveis(tema)}>
      <header className="ds-header">
        <div className="ds-container ds-header-linha">
          <Link to="/" aria-label="AutoLoca">
            <Logo escuro={escuro} tamanho={34} />
          </Link>
          <nav className="ds-nav" aria-label="Navegação">
            <Link to="/">Início</Link>
            <Link to="/frota">Frota</Link>
            <BotaoTema />
          </nav>
        </div>
      </header>

      <main className="ds-container">
        <div className="ds-hero">
          <p className="ds-rotulo">Documentação do design system</p>
          <h1 className="ds-display">AutoLoca</h1>
          <p className="ds-lead">
            Esta página centraliza as decisões visuais do front-end do sistema de aluguel de carros. Ela foi
            pensada para consulta rápida durante a implementação de telas, componentes e estados.
          </p>
          <div className="ds-hero-acoes">
            <Link to="/" className="ds-btn ds-btn-primario">
              <ArrowLeft size={16} /> Voltar ao painel
            </Link>
            <BotaoTema />
          </div>
        </div>

        <Secao rotulo="Paleta base" titulo="Escala cromática" id="paleta">
          <p className="ds-apoio">Clique em qualquer cor para copiar o valor hexadecimal.</p>
          {Object.entries(paleta).map(([nome, escala]) => (
            <Escala key={nome} nome={nome} escala={escala} />
          ))}
        </Secao>

        <Secao rotulo="Tokens semânticos" titulo="Light e dark lado a lado" id="tokens">
          <div className="ds-tabela-wrap">
            <table className="ds-tabela">
              <thead>
                <tr>
                  <th>Token</th>
                  <th>Uso</th>
                  <th>Light</th>
                  <th>Dark</th>
                </tr>
              </thead>
              <tbody>
                {semanticos.map((s) => (
                  <tr key={s.token}>
                    <td>
                      <code>{s.token}</code>
                    </td>
                    <td>{s.uso}</td>
                    <td>
                      <Valor cor={s.light} />
                    </td>
                    <td>
                      <Valor cor={s.dark} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Secao>

        <Secao rotulo="Tipografia" titulo="Fontes e tamanhos disponíveis" id="tipografia">
          <div className="ds-grade-2">
            <div className="ds-card">
              <h3 className="ds-h4">Famílias</h3>
              <p className="ds-familia-nome ds-serif">DM Serif Display</p>
              <p className="ds-apoio">
                Usada para títulos, manchetes, números de destaque e trechos de alto impacto.
              </p>
              <p className="ds-familia-nome">DM Sans</p>
              <p className="ds-apoio">
                Base de leitura do sistema: navegação, formulários, cards, tabelas e textos de apoio.
              </p>
            </div>
            <div className="ds-card">
              <h3 className="ds-h4">Regras</h3>
              <ul className="ds-regras">
                <li>Use DM Serif Display apenas em pontos de alto contraste visual.</li>
                <li>Use DM Sans para todo conteúdo funcional e leitura contínua.</li>
                <li>Evite misturar mais de dois níveis tipográficos fortes na mesma seção.</li>
                <li>Prefira variações de tamanho e peso antes de introduzir novas famílias.</li>
              </ul>
            </div>
          </div>
          <div className="ds-especimes">
            {tipografia.map((t) => (
              <div key={t.nome} className="ds-especime">
                <div className="ds-especime-meta">
                  <code>{t.nome}</code>
                  <span>{t.familia}</span>
                  <span>{t.tamanho}</span>
                  <span>{t.uso}</span>
                </div>
                <p className={`ds-t-${t.nome}`}>{t.exemplo}</p>
              </div>
            ))}
          </div>
        </Secao>

        <Secao rotulo="Spacing" titulo="Escala de espaçamento" id="espacamento">
          <div className="ds-card ds-espacamentos">
            {espacamentos.map((e) => (
              <div key={e} className="ds-espacamento">
                <span className="ds-barra" style={{ width: e }} />
                <code>{e}px</code>
              </div>
            ))}
          </div>
        </Secao>

        <Secao rotulo="Radii" titulo="Arredondamentos padrão" id="raios">
          <div className="ds-raios">
            {raios.map((r) => (
              <div key={r} className="ds-raio">
                <span className="ds-raio-caixa" style={{ borderRadius: r }} />
                <code>{r === 999 ? "999px" : `${r}px`}</code>
              </div>
            ))}
          </div>
        </Secao>

        <Secao rotulo="Componentes" titulo="Estados de interface" id="componentes">
          <div className="ds-grade-2">
            <div className="ds-card">
              <h3 className="ds-h4">Botões</h3>
              <div className="ds-linha">
                <button type="button" className="ds-btn ds-btn-primario">
                  Solicitar aluguel
                </button>
                <button type="button" className="ds-btn ds-btn-secundario">
                  Ver detalhes
                </button>
                <button type="button" className="ds-btn ds-btn-destaque">
                  Formalizar contrato
                </button>
                <button type="button" className="ds-btn ds-btn-primario" disabled>
                  Desabilitado
                </button>
              </div>
            </div>
            <div className="ds-card">
              <h3 className="ds-h4">Status do pedido</h3>
              <div className="ds-linha">
                {statusPedido.map((s) => (
                  <span key={s.chave} className={`ds-badge ds-badge-${s.chave}`}>
                    {s.nome}
                  </span>
                ))}
              </div>
            </div>
            <div className="ds-card">
              <h3 className="ds-h4">Campo de formulário</h3>
              <label className="ds-campo">
                Usuário
                <input type="text" placeholder="Seu nome de usuário" />
                <span className="ds-caption">Use o mesmo usuário do cadastro.</span>
              </label>
            </div>
            <div className="ds-card">
              <h3 className="ds-h4">Card de veículo</h3>
              <p className="ds-carro-nome">Onix LT 1.0</p>
              <p className="ds-caption">2024 · Chevrolet · ABC1D23</p>
              <p className="ds-preco">
                R$ 189,00 <span>/ dia</span>
              </p>
            </div>
          </div>
        </Secao>

        <Secao rotulo="Logos" titulo="Versões da marca (Light / Dark)" id="logos">
          <div className="ds-logos">
            {[
              [
                "horizontal",
                "Horizontal (principal)",
                "Uso: header desktop, páginas públicas e materiais de marca.",
              ],
              [
                "vertical",
                "Empilhada",
                "Uso: telas de login e mobile quando o espaço horizontal for limitado.",
              ],
              ["icone", "Marca (ícone)", "Uso: favicon, botões, avatars e contextos de espaço reduzido."],
            ].map(([variante, nome, uso]) => (
              <div key={variante} className="ds-card">
                <h3 className="ds-h4">{nome}</h3>
                <div className="ds-logo-pares">
                  <figure className="ds-logo-painel ds-painel-claro">
                    <Logo variante={variante} tamanho={44} />
                    <figcaption>Light</figcaption>
                  </figure>
                  <figure className="ds-logo-painel ds-painel-escuro">
                    <Logo variante={variante} escuro tamanho={44} />
                    <figcaption>Dark</figcaption>
                  </figure>
                </div>
                <p className="ds-apoio">{uso}</p>
              </div>
            ))}
          </div>
          <div className="ds-card">
            <h3 className="ds-h4">Regras rápidas</h3>
            <ul className="ds-regras">
              <li>
                Use a variante <strong>light</strong> em fundos claros e a variante <strong>dark</strong> em
                fundos escuros. O símbolo permanece igual; a cor do nome se adapta ao fundo.
              </li>
              <li>Mantenha uma área de respiro mínima equivalente à altura da letra “A”.</li>
              <li>Use o arquivo logo.png com fundo transparente, sem distorcer a proporção.</li>
            </ul>
          </div>
        </Secao>
      </main>

      <footer className="ds-rodape">
        <div className="ds-container ds-rodape-linha">
          <span>© 2026 AutoLoca · Sistema de aluguel de carros</span>
          <BotaoTema />
        </div>
      </footer>
    </div>
  );
}
