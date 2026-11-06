import { useAcao } from "../hooks/useAcao";
import { useState } from "react";
import { useAuth } from "../hooks/useAuth";
import { Campo, Submit, Message } from "../components/UI";
import AgenteForm from "../components/AgenteForm";
import ClienteForm from "../components/ClienteForm";
import { requisicao } from "../services/httpClient";
export default function Login() {
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
    <main className="login-page">
      <section className="login-scene" aria-label="AutoLoca">
        <div className="login-scene-copy">
          <span className="hero-kicker">AutoLoca · aluguel de carros</span>
          <h2>Seu próximo destino começa aqui.</h2>
          <p>Escolha o carro. Planeje os dias. A gente cuida do pedido.</p>
        </div>
        <img src="/viagem.png" alt="Ilustração de um carro em uma estrada à beira-mar" />
        <small>
          <a href="/carros/creditos.html" target="_blank" rel="noreferrer">
            Créditos das imagens
          </a>
        </small>
      </section>
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
                executar(() => login(f.get("username"), f.get("password")));
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
    </main>
  );
}
