import { useAcao } from "../hooks/useAcao";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { Campo, Submit, Message } from "../components/UI";
import AgenteForm from "../components/AgenteForm";
import ClienteForm from "../components/ClienteForm";
import {
  ArrowUpRight,
  ArrowRight,
  CarFront,
  CalendarDays,
  FileCheck2,
  ChevronDown,
  Building2,
} from "lucide-react";
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
          <span>
            AutoLoca<small>ALUGUEL DE CARROS</small>
          </span>
        </a>
        <nav aria-label="Navegação pública">
          <a href="#sobre">Sobre</a>
          <a href="#como-funciona">Como funciona</a>
          <a href="#duvidas">Dúvidas</a>
        </nav>
        <a href="#entrar" className="button public-enter">
          Minha conta <ArrowUpRight size={17} aria-hidden="true" />
        </a>
      </header>
      <section className="public-hero" id="inicio" aria-labelledby="titulo-inicio">
        <div className="public-hero-copy">
          <span className="hero-kicker">UM CARRO. MUITAS POSSIBILIDADES.</span>
          <h1 id="titulo-inicio">
            Seu próximo
            <br />
            caminho
            <br />
            <em>começa aqui.</em>
          </h1>
          <p>Para a rotina ou para sair dela. Encontre o carro que combina com os seus planos.</p>
          <a href="#entrar" className="button public-primary">
            Escolher meu carro <ArrowUpRight size={20} aria-hidden="true" />
          </a>
          <p className="public-hero-note">Entre ou crie sua conta para conhecer a frota.</p>
        </div>
        <figure className="public-hero-photo">
          <img
            src="/imagens/carro-floresta.jpg"
            alt="Carro prata visto de lado em uma estrada de floresta, sem placa visível"
            width="1400"
            height="933"
            fetchPriority="high"
          />
          <figcaption>
            <span>Para onde vamos hoje?</span>
            <a href="/carros/creditos.html">Imagem ilustrativa · créditos</a>
          </figcaption>
        </figure>
      </section>
      <div className="public-benefits" aria-label="Recursos do aluguel">
        <span>
          <CarFront size={21} aria-hidden="true" />
          Compare modelos e diárias
        </span>
        <span>
          <CalendarDays size={21} aria-hidden="true" />
          Escolha seu período
        </span>
        <span>
          <FileCheck2 size={21} aria-hidden="true" />
          Acompanhe até o contrato
        </span>
      </div>
      <section id="sobre" className="public-about" aria-labelledby="titulo-sobre">
        <figure className="public-about-photo">
          <img
            src="/imagens/estrada-floresta.jpg"
            alt="Carro percorrendo uma curva em uma estrada arborizada"
            width="1400"
            height="933"
            loading="lazy"
          />
          <figcaption>O caminho também faz parte da viagem.</figcaption>
        </figure>
        <div className="public-about-copy">
          <span className="hero-kicker">SOBRE A AUTOLOCA</span>
          <h2 id="titulo-sobre">
            O carro certo.
            <br />
            Para os <em>seus planos.</em>
          </h2>
          <p>A AutoLoca conecta quem precisa de um carro a empresas e bancos que anunciam seus veículos.</p>
          <p>
            Compare as diárias, escolha o período e envie seu pedido. Você acompanha a análise e acessa o
            contrato em um só lugar.
          </p>
          <a href="#como-funciona" className="public-text-link">
            Veja como alugar <ArrowRight size={18} aria-hidden="true" />
          </a>
        </div>
      </section>
      <section id="como-funciona" className="public-how" aria-labelledby="titulo-etapas">
        <div className="public-section-heading">
          <div>
            <span className="hero-kicker">DO PLANO À ESTRADA</span>
            <h2 id="titulo-etapas">Seu aluguel, passo a passo.</h2>
          </div>
          <p>
            Você escolhe. O anunciante analisa.
            <br />
            Tudo fica registrado na sua conta.
          </p>
        </div>
        <div className="public-steps">
          <article>
            <div className="public-step-top">
              <span>01</span>
              <CarFront size={28} aria-hidden="true" />
            </div>
            <h3>Encontre seu carro</h3>
            <p>Consulte as fotos, confira o anunciante e compare o valor das diárias.</p>
          </article>
          <article>
            <div className="public-step-top">
              <span>02</span>
              <CalendarDays size={28} aria-hidden="true" />
            </div>
            <h3>Marque os dias</h3>
            <p>Informe retirada e devolução. Confira o total antes de enviar o pedido.</p>
          </article>
          <article>
            <div className="public-step-top">
              <span>03</span>
              <FileCheck2 size={28} aria-hidden="true" />
            </div>
            <h3>Acompanhe o pedido</h3>
            <p>Consulte o parecer do anunciante e baixe o contrato quando ele for emitido.</p>
          </article>
        </div>
      </section>
      <section id="duvidas" className="public-faq" aria-labelledby="titulo-duvidas">
        <div className="public-faq-intro">
          <span className="hero-kicker">ANTES DE PARTIR</span>
          <h2 id="titulo-duvidas">
            Bom saber
            <br />
            <em>antes de alugar.</em>
          </h2>
          <p>As respostas para você começar com tudo esclarecido.</p>
          <a href="#entrar" className="public-text-link">
            Acessar minha conta <ArrowRight size={18} aria-hidden="true" />
          </a>
        </div>
        <div className="public-faq-list">
          <details>
            <summary>
              Preciso de uma conta para alugar?
              <ChevronDown size={18} aria-hidden="true" />
            </summary>
            <p>
              Sim. Cadastre-se como cliente e informe seus dados e rendimentos. Depois de entrar, você poderá
              consultar a frota e enviar um pedido.
            </p>
          </details>
          <details>
            <summary>
              Como o valor do aluguel é calculado?
              <ChevronDown size={18} aria-hidden="true" />
            </summary>
            <p>
              Multiplicamos a diária pelo número de dias entre retirada e devolução. A data de devolução não
              conta como uma diária adicional.
            </p>
          </details>
          <details>
            <summary>
              Quando posso solicitar crédito?
              <ChevronDown size={18} aria-hidden="true" />
            </summary>
            <p>
              Nos anúncios de bancos, você pode solicitar crédito e informar o valor e as parcelas. O próprio
              banco precisa analisar e aprovar as condições antes da emissão do contrato.
            </p>
          </details>
          <details>
            <summary>
              Posso alterar ou cancelar meu pedido?
              <ChevronDown size={18} aria-hidden="true" />
            </summary>
            <p>
              Enquanto estiver em análise ou aprovado, você pode modificar ou cancelar. Uma alteração exige
              nova análise. Depois da contratação, o documento emitido é preservado.
            </p>
          </details>
          <details>
            <summary>
              As fotos são do carro anunciado?
              <ChevronDown size={18} aria-hidden="true" />
            </summary>
            <p>
              A foto enviada pelo anunciante tem prioridade. Quando não há uma foto enviada, mostramos uma
              imagem ilustrativa do modelo e seus créditos. As imagens desta página são de apresentação e não
              representam anúncios disponíveis.
            </p>
          </details>
          <details>
            <summary>
              Onde encontro meu contrato?
              <ChevronDown size={18} aria-hidden="true" />
            </summary>
            <p>
              Após a emissão pelo anunciante, abra Contratos para ler e baixar o PDF. O documento traz os
              dados das partes, do veículo, os valores e os campos para assinatura. A emissão não representa
              assinatura digital.
            </p>
          </details>
        </div>
      </section>
      <section className="public-access" id="entrar" aria-labelledby="titulo-acesso">
        <div className="access-copy">
          <span className="hero-kicker">VAMOS COMEÇAR?</span>
          <h2 id="titulo-acesso">
            Mais perto
            <br />
            do seu próximo
            <br />
            <em>destino.</em>
          </h2>
          <p>Entre para conhecer os carros e acompanhar seus aluguéis.</p>
          <div className="public-agent-note">
            <Building2 size={22} aria-hidden="true" />
            <p>
              Tem uma empresa ou um banco? Crie sua conta de anunciante para gerenciar seus próprios veículos.
            </p>
          </div>
        </div>
        <section className="login-form">
          <div className={cadastro ? "auth-box wide" : "auth-box"}>
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
      <footer className="public-footer">
        <a href="#inicio" className="public-footer-brand">
          AutoLoca<span>Um carro para cada caminho.</span>
        </a>
        <nav aria-label="Links do rodapé">
          <a href="#sobre">Sobre</a>
          <a href="#duvidas">Dúvidas</a>
          <a href="/carros/creditos.html">Créditos das imagens</a>
        </nav>
        <p>Projeto acadêmico · DIAW</p>
      </footer>
    </main>
  );
}
