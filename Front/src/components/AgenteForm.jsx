import { useAcao } from "../hooks/useAcao";
import { Campo, Submit, Message } from "./UI";

export default function AgenteForm({ perfil, onSalvar }) {
  const { busy, erro, executar } = useAcao();

  function salvar(evento) {
    evento.preventDefault();
    const dados = new FormData(evento.currentTarget);
    executar(() =>
      onSalvar({
        nome: dados.get("nome"),
        username: dados.get("username"),
        senha: dados.get("senha"),
        perfil,
      }),
    );
  }

  return (
    <form className="form" onSubmit={salvar}>
      <Message erro={erro} />
      <Campo
        label={perfil === "BANCO" ? "Nome do banco" : "Nome da empresa"}
        name="nome"
        maxLength={150}
        required
      />
      <Campo
        label="Usuário"
        name="username"
        minLength={3}
        maxLength={80}
        pattern={"[a-zA-Z0-9._\\-]+"}
        autoComplete="username"
        required
      />
      <Campo
        label="Senha (mínimo 8 caracteres)"
        name="senha"
        type="password"
        minLength={8}
        maxLength={72}
        autoComplete="new-password"
        required
      />
      <Submit busy={busy}>Criar cadastro</Submit>
    </form>
  );
}
