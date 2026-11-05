import { useAcao } from "../hooks/useAcao";
import { useState } from "react";
import { Submit, Message } from "./UI";
import { avaliarPedido } from "../services/aluguelService";
import { moeda } from "../utils/formatar";
export default function AvaliacaoForm({ pedido, onSalvo }) {
  const [aprovado, setAprovado] = useState("true");
  const { busy, erro, executar } = useAcao();
  const c = pedido.cliente;
  return (
    <form
      className="form"
      onSubmit={(e) => {
        e.preventDefault();
        const f = new FormData(e.currentTarget);
        executar(async () => {
          await avaliarPedido(pedido.id, { aprovado: aprovado === "true", parecer: f.get("parecer") });
          onSalvo();
        });
      }}
    >
      <Message erro={erro} />
      <div className="detail-box">
        <strong>
          {c.usuario.nome} · {c.profissao}
        </strong>
        <p>
          CPF {c.cpf} · RG {c.rg}
        </p>
        <p>{c.endereco}</p>
        {c.rendimentos.map((r, i) => (
          <p key={i}>
            {r.empregador}: {moeda(r.valor)}/mês
          </p>
        ))}
        <strong>Renda declarada: {moeda(c.rendimentos.reduce((s, r) => s + Number(r.valor), 0))}</strong>
        <p>Valor do pedido: {moeda(pedido.valorTotal)}</p>
      </div>
      <label className="field">
        Resultado da análise
        <select value={aprovado} onChange={(e) => setAprovado(e.target.value)}>
          <option value="true">Aprovar pedido</option>
          <option value="false">Reprovar pedido</option>
        </select>
      </label>
      <label className="field">
        Parecer financeiro
        <textarea
          name="parecer"
          rows="4"
          required
          maxLength={2000}
          placeholder="Descreva os motivos da decisão…"
        />
      </label>
      <Submit busy={busy}>Registrar parecer</Submit>
    </form>
  );
}
