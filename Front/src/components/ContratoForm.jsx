import { useAcao } from "../hooks/useAcao";
import { useState } from "react";
import { useAuth } from "../hooks/useAuth";
import { useDados } from "../hooks/useDados";
import { Campo, Submit, Message } from "./UI";
import { contratarPedido } from "../services/aluguelService";
import { moeda } from "../utils/formatar";
export default function ContratoForm({ pedido, onSalvo }) {
  const { usuario } = useAuth();
  const { dados, erro: erroDados } = useDados("automoveis/proprietarios");
  const { busy, erro, executar } = useAcao();
  const [credito, setCredito] = useState(false);
  return (
    <form
      className="form"
      onSubmit={(e) => {
        e.preventDefault();
        const f = new FormData(e.currentTarget);
        executar(async () => {
          await contratarPedido(pedido.id, {
            proprietarioId: Number(f.get("proprietarioId")),
            valorCredito: credito ? Number(f.get("valorCredito")) : null,
            parcelas: credito ? Number(f.get("parcelas")) : null,
          });
          onSalvo();
        });
      }}
    >
      <Message erro={erro || erroDados} />
      <div className="detail-box">
        <strong>
          Pedido #{pedido.id} · {pedido.automovel.modelo}
        </strong>
        <p>Contratante: {pedido.cliente.usuario.nome}</p>
        <p>Valor total: {moeda(pedido.valorTotal)}</p>
      </div>
      <label className="field">
        Registrar automóvel em nome de
        <select name="proprietarioId" defaultValue={pedido.automovel.proprietario.id} required>
          {dados
            .filter((u) => u.perfil !== "CLIENTE" || u.id === pedido.cliente.usuario.id)
            .map((u) => (
              <option key={u.id} value={u.id}>
                {u.nome} ({u.perfil})
              </option>
            ))}
        </select>
      </label>
      {usuario.perfil === "BANCO" && (
        <>
          <label className="checkbox">
            <input type="checkbox" checked={credito} onChange={(e) => setCredito(e.target.checked)} />
            Associar crédito concedido por {usuario.nome}
          </label>
          {credito && (
            <div className="form-grid">
              <Campo
                label="Valor do crédito (R$)"
                name="valorCredito"
                type="number"
                min="0.01"
                max={pedido.valorTotal}
                step="0.01"
                required
              />
              <Campo label="Parcelas" name="parcelas" type="number" min="1" max="60" required />
            </div>
          )}
        </>
      )}
      <p className="muted small-text">
        A confirmação registra o contrato e o proprietário do veículo. Os dados financeiros deste pedido não
        poderão mais ser alterados.
      </p>
      <Submit busy={busy}>Confirmar contrato</Submit>
    </form>
  );
}
