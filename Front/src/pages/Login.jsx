import { useAcao } from "../hooks/useAcao";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { Campo, Submit, Message } from "../components/UI";
import AgenteForm from "../components/AgenteForm";
import ClienteForm from "../components/ClienteForm";
import FotoCarro from "../components/FotoCarro";
import "./Login.css";
import { requisicao } from "../services/httpClient";
export default function Login() {
  const navigate = useNavigate();
  const [perfil, setPerfil] = useState("CLIENTE");
  const [cadastro, setCadastro] = useState(false);
  const [sucesso, setSucesso] = useState("");
  const { login, erro: inicial } = useAuth();
  const { busy, erro, executar } = useAcao();
  async function criarConta(caminho, dados) {
    await requisicao(caminho, { method: "POST", body: dados });
    setCadastro(false);
    setSucesso("Cadastro criado! Entre com seu usuário e senha.");
  }
  return (
    <main className="public-page">
      <header className="public-header">
        <a href="#inicio" className="public-brand">
          <img src="/logo.png" alt="" />
          AutoLoca<span>ALUGUEL DE CARROS</span>
        </a>
        <nav aria-label="Navegação pública">
          <a href="#sobre">Sobre</a>
          <a href="#duvidas">Dúvidas</a>
          <a href="#entrar" className="button">
            Entrar na minha conta →
          </a>
        </nav>
      </header>
      <section className="public-hero" id="inicio">
        <div className="public-hero-copy">
          <span className="hero-kicker">Para cada plano, um caminho.</span>
          <h1>
            O destino é seu.
            <br />O carro está aqui.
          </h1>
          <p>Compare modelos, planeje o período e acompanhe seu pedido de aluguel do começo ao contrato.</p>
          <a href="#entrar" className="button">
            Encontrar meu próximo carro →
          </a>
          <a className="public-secondary" href="#sobre">
            Conheça a AutoLoca ↓
          </a>
        </div>
        <div className="public-hero-photo">
          <FotoCarro carro={{ marca: "Toyota", modelo: "Corolla", temFoto: false }} />
          <span className="public-photo-label">DA ROTINA À PRÓXIMA VIAGEM</span>
        </div>
      </section>
      <section id="sobre" className="public-about">
        <div>
          <span className="hero-kicker">Sobre a AutoLoca</span>
          <h2>
            Menos etapas.
            <br />
            Mais estrada.
          </h2>
          <p>
            Um espaço para conectar quem precisa de um carro a empresas e bancos que anunciam seus veículos.
          </p>
          <p>
            Você escolhe o modelo e as datas. O anunciante analisa o pedido e emite o contrato com todas as
            informações do aluguel.
          </p>
        </div>
        <div className="public-steps">
          <article>
            <b>01</b>
            <div>
              <h3>Encontre seu carro</h3>
              <p>Veja fotos, anunciante e valor da diária antes de escolher.</p>
            </div>
          </article>
          <article>
            <b>02</b>
            <div>
              <h3>Planeje os dias</h3>
              <p>Informe retirada e devolução e confira o valor do período.</p>
            </div>
          </article>
          <article>
            <b>03</b>
            <div>
              <h3>Acompanhe a aprovação</h3>
              <p>Consulte o parecer do anunciante e baixe o contrato quando emitido.</p>
            </div>
          </article>
        </div>
      </section>
      <section id="duvidas" className="public-faq">
        <div>
          <span className="hero-kicker">Antes de partir</span>
          <h2>Alguma dúvida?</h2>
          <p>Veja como funcionam os pedidos e o crédito.</p>
        </div>
        <div>
          <details>
            <summary>Preciso de uma conta para alugar?</summary>
            <p>
              Sim. Cadastre-se como cliente e informe seus dados e rendimentos. Depois de entrar, você poderá
              consultar a frota e enviar um pedido.
            </p>
          </details>
          <details>
            <summary>Como o valor do aluguel é calculado?</summary>
            <p>
              Multiplicamos a diária pelo número de dias entre retirada e devolução. A data de devolução não
              conta como uma diária adicional.
            </p>
          </details>
          <details>
            <summary>Quando posso solicitar crédito?</summary>
            <p>
              Nos anúncios de bancos, você pode solicitar crédito e informar o valor e as parcelas. O próprio
              banco precisa analisar e aprovar as condições antes da emissão do contrato.
            </p>
          </details>
          <details>
            <summary>Posso alterar ou cancelar meu pedido?</summary>
            <p>
              Enquanto estiver em análise ou aprovado, você pode modificar ou cancelar. Uma alteração exige
              nova análise. Depois da contratação, o documento emitido é preservado.
            </p>
          </details>
          <details>
            <summary>As fotos são do carro anunciado?</summary>
            <p>
              A foto enviada pelo anunciante tem prioridade. Quando não há uma foto enviada, mostramos uma
              imagem ilustrativa do modelo e seus créditos. Confira os dados do anúncio e as condições com o
              anunciante.
            </p>
          </details>
          <details>
            <summary>Onde encontro meu contrato?</summary>
            <p>
              Após a emissão pelo anunciante, abra Contratos para ler e baixar o PDF. O documento traz os
              dados das partes, do veículo, os valores e os campos para assinatura. A emissão não representa
              assinatura digital.
            </p>
          </details>
        </div>
      </section>
      <section className="public-access" id="entrar">
        <div className="access-copy">
          <span className="hero-kicker">Vamos começar?</span>
          <h2>
            Seu próximo caminho
            <br />
            começa aqui.
          </h2>
          <p>Entre para consultar os carros e acompanhar seus pedidos.</p>
          <p>
            Tem uma empresa ou um banco? Crie uma conta de anunciante para gerenciar seus próprios veículos.
          </p>
        </div>
        <section className="login-form">
          <div className={cadastro ? "auth-box wide" : "auth-box"}>
            <h1>AutoLoca</h1>
            <h2>{cadastro ? "Criar conta" : "Login"}</h2>
            <p className="muted">
              {cadastro ? "Preencha os dados para criar sua conta." : "Informe seu usuário e senha."}
            </p>
            <Message erro={erro || inicial} sucesso={sucesso} />
            {cadastro ? (
              <>
                <label className="field">
                  Tipo de conta
                  <select value={perfil} onChange={(e) => setPerfil(e.target.value)}>
                    <option value="CLIENTE">Cliente</option>
                    <option value="EMPRESA">Empresa</option>
                    <option value="BANCO">Banco</option>
                  </select>
                </label>
                <p className="muted">
                  {perfil === "CLIENTE"
                    ? "Para solicitar aluguéis e acompanhar seus pedidos."
                    : perfil === "EMPRESA"
                      ? "Para cadastrar automóveis, avaliar pedidos e formalizar contratos."
                      : "Para atuar como agente e conceder crédito nos contratos."}
                </p>
                {perfil === "CLIENTE" ? (
                  <ClienteForm cadastro onSalvar={(dados) => criarConta("/auth/cadastro", dados)} />
                ) : (
                  <AgenteForm
                    key={perfil}
                    perfil={perfil}
                    onSalvar={(dados) => criarConta("/auth/cadastro/agente", dados)}
                  />
                )}
              </>
            ) : (
              <form
                className="form"
                onSubmit={(e) => {
                  e.preventDefault();
                  const f = new FormData(e.currentTarget);
                  executar(async () => {
                    await login(f.get("username"), f.get("password"));
                    navigate("/", { replace: true });
                    window.scrollTo(0, 0);
                  });
                }}
              >
                <Campo
                  label="Usuário"
                  name="username"
                  placeholder="Seu nome de usuário"
                  autoComplete="username"
                  required
                />
                <Campo
                  label="Senha"
                  name="password"
                  type="password"
                  placeholder="Sua senha"
                  autoComplete="current-password"
                  required
                />
                <Submit busy={busy}>Entrar</Submit>
              </form>
            )}
            <p className="auth-switch">
              {cadastro ? "Já tem uma conta?" : "Não tem uma conta?"}{" "}
              <button
                className="text-button"
                onClick={() => {
                  setCadastro(!cadastro);
                  setSucesso("");
                }}
              >
                {cadastro ? "Entrar" : "Criar uma conta"}
              </button>
            </p>
          </div>
        </section>
      </section>
      <div className="public-footer">
        <strong>AutoLoca</strong>
        <span>Projeto acadêmico · DIAW</span>
        <a href="/carros/creditos.html">Créditos das imagens</a>
      </div>
    </main>
  );
}
