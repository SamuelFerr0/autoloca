import { useAcao } from "../hooks/useAcao";
import { useState } from "react";
import { Campo, Submit, Message } from "./UI";
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
          await avaliarPedido(pedido.id, {
            aprovado: aprovado === "true",
            parecer: f.get("parecer"),
            valorCreditoAprovado:
              pedido.solicitaCredito && aprovado === "true" ? Number(f.get("valorCreditoAprovado")) : null,
            parcelasAprovadas:
              pedido.solicitaCredito && aprovado === "true" ? Number(f.get("parcelasAprovadas")) : null,
          });
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
      {pedido.solicitaCredito && (
        <div className="credit-box">
          <strong>
            Crédito solicitado: {moeda(pedido.valorCreditoSolicitado)} em {pedido.parcelasSolicitadas}{" "}
            parcelas
          </strong>
          {aprovado === "true" && (
            <div className="form-grid">
              <Campo
                label="Crédito aprovado (R$)"
                name="valorCreditoAprovado"
                type="number"
                min="0.01"
                step="0.01"
                max={pedido.valorCreditoSolicitado}
                defaultValue={pedido.valorCreditoSolicitado}
                required
              />
              <Campo
                label="Parcelas aprovadas"
                name="parcelasAprovadas"
                type="number"
                min="1"
                max="60"
                defaultValue={pedido.parcelasSolicitadas}
                required
              />
            </div>
          )}
          <p>Essas condições serão copiadas para o contrato após a aprovação.</p>
        </div>
      )}
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
