import { useAcao } from "../hooks/useAcao";
import { useState } from "react";
import { useAuth } from "../hooks/useAuth";
import { Campo, Submit, Message } from "../components/UI";
import ClienteForm from "../components/ClienteForm";
import { requisicao } from "../services/httpClient";
export default function Login() {
  const [cadastro, setCadastro] = useState(false);
  const [sucesso, setSucesso] = useState("");
  const { login, erro: inicial } = useAuth();
  const { busy, erro, executar } = useAcao();
  return (
    <main className="login-page">
      <section className="login-form">
        <div className={cadastro ? "auth-box wide" : "auth-box"}>
          <h1>AutoLoca</h1>
          <h2>{cadastro ? "Cadastro de cliente" : "Login"}</h2>
          <p className="muted">
            {cadastro ? "Preencha os dados para criar sua conta." : "Informe seu usuário e senha."}
          </p>
          <Message erro={erro || inicial} sucesso={sucesso} />
          {cadastro ? (
            <ClienteForm
              cadastro
              onSalvar={async (d) => {
                await requisicao("/auth/cadastro", { method: "POST", body: d });
                setCadastro(false);
                setSucesso("Cadastro criado! Entre com seu usuário e senha.");
              }}
            />
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
