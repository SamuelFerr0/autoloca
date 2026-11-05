import { useState } from "react";
import { useAcao } from "../hooks/useAcao";
import { Campo, Submit, Message } from "./UI";

export default function ClienteForm({ cliente, onSalvar, cadastro = false }) {
  const [rendas, setRendas] = useState(
    (cliente?.rendimentos || []).map((renda, indice) => ({ ...renda, chave: indice })),
  );
  const { busy, erro, executar } = useAcao();

  function adicionarRenda() {
    const renda = { empregador: "", valor: "", chave: crypto.randomUUID() };
    setRendas([...rendas, renda]);
  }

  function atualizarRenda(indice, campo, valor) {
    const novasRendas = [...rendas];
    novasRendas[indice] = { ...novasRendas[indice], [campo]: valor };
    setRendas(novasRendas);
  }

  function removerRenda(indice) {
    setRendas(rendas.filter((renda, posicao) => posicao !== indice));
  }

  async function handleSalvar(evento) {
    evento.preventDefault();
    const formulario = new FormData(evento.currentTarget);
    const perfil = {
      nome: formulario.get("nome"),
      rg: formulario.get("rg"),
      cpf: formulario.get("cpf"),
      endereco: formulario.get("endereco"),
      profissao: formulario.get("profissao"),
      rendimentos: rendas.map((renda) => ({
        empregador: renda.empregador,
        valor: Number(renda.valor),
      })),
    };

    await executar(async () => {
      if (cadastro) {
        await onSalvar({
          username: formulario.get("username"),
          senha: formulario.get("senha"),
          cliente: perfil,
        });
      } else {
        await onSalvar(perfil);
      }
    });
  }

  return (
    <form onSubmit={handleSalvar} className="form">
      <Message erro={erro} />
      {cadastro && (
        <div className="form-grid">
          <Campo
            label="Usuário"
            name="username"
            required
            minLength={3}
            maxLength={80}
            pattern={"[a-zA-Z0-9._\\-]+"}
            autoComplete="username"
          />
          <Campo
            label="Senha (mínimo 8 caracteres)"
            type="password"
            name="senha"
            minLength={8}
            maxLength={72}
            autoComplete="new-password"
            required
          />
        </div>
      )}
      <Campo
        label="Nome completo"
        name="nome"
        defaultValue={cliente?.usuario?.nome}
        maxLength={150}
        required
      />
      <div className="form-grid">
        <Campo
          label="CPF (somente números)"
          name="cpf"
          defaultValue={cliente?.cpf}
          pattern="[0-9]{11}"
          maxLength={11}
          required
        />
        <Campo label="RG" name="rg" defaultValue={cliente?.rg} maxLength={30} required />
      </div>
      <Campo
        label="Endereço completo"
        name="endereco"
        defaultValue={cliente?.endereco}
        maxLength={250}
        required
      />
      <Campo label="Profissão" name="profissao" defaultValue={cliente?.profissao} maxLength={100} required />
      <fieldset>
        <legend>
          Empregadores e rendimentos <small>Até 3 fontes de renda</small>
        </legend>
        {rendas.map((renda, indice) => (
          <div className="income-row" key={renda.chave}>
            <Campo
              label="Empregador"
              name={`empregador${indice}`}
              value={renda.empregador}
              onChange={(evento) => atualizarRenda(indice, "empregador", evento.target.value)}
              maxLength={150}
              required
            />
            <Campo
              label="Renda mensal (R$)"
              name={`valor${indice}`}
              type="number"
              min="0.01"
              max="9999999999.99"
              step="0.01"
              value={renda.valor}
              onChange={(evento) => atualizarRenda(indice, "valor", evento.target.value)}
              required
            />
            <button type="button" className="text-button" onClick={() => removerRenda(indice)}>
              Remover
            </button>
          </div>
        ))}
        {rendas.length < 3 && (
          <button className="button secondary small" type="button" onClick={adicionarRenda}>
            + Adicionar renda
          </button>
        )}
      </fieldset>
      <Submit busy={busy}>{cadastro ? "Criar cadastro" : "Salvar alterações"}</Submit>
    </form>
  );
}
