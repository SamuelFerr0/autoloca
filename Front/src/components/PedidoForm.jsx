import { useAcao } from "../hooks/useAcao";
import { useState } from "react";
import { Campo, Submit, Message } from "./UI";
import { moeda, hoje } from "../utils/formatar";
import { salvar } from "../services/aluguelService";
export default function PedidoForm({ carros, carro, pedido, onSalvo }) {
  const [id, setId] = useState(pedido?.automovel.id || carro?.id || carros[0]?.id || "");
  const [inicio, setInicio] = useState(pedido?.inicio || "");
  const [fim, setFim] = useState(pedido?.fim || "");
  const [credito, setCredito] = useState(pedido?.solicitaCredito || false);
  const [valorCredito, setValorCredito] = useState(pedido?.valorCreditoSolicitado || "");
  const [parcelas, setParcelas] = useState(pedido?.parcelasSolicitadas || 1);
  const { busy, erro, executar } = useAcao();
  const selecionado = carros.find((c) => c.id === Number(id));
  const banco = selecionado?.anunciante?.perfil === "BANCO";
  const dias = inicio && fim ? Math.round((Date.parse(fim) - Date.parse(inicio)) / 86400000) : 0;
  return (
    <form
      className="form"
      onSubmit={(e) => {
        e.preventDefault();
        executar(async () => {
          await salvar("pedidos", pedido?.id, {
            automovelId: Number(id),
            inicio,
            fim,
            solicitaCredito: banco && credito,
            valorCreditoSolicitado: banco && credito ? Number(valorCredito) : null,
            parcelasSolicitadas: banco && credito ? Number(parcelas) : null,
          });
          onSalvo();
        });
      }}
    >
      <Message erro={erro} />
      <label className="field">
        Automóvel
        <select value={id} onChange={(e) => setId(e.target.value)} required>
          {carros.map((c) => (
            <option value={c.id} key={c.id}>
              {c.marca} {c.modelo} · {c.placa} · {moeda(c.diaria)}/dia
            </option>
          ))}
        </select>
      </label>
      <div className="form-grid">
        <Campo
          label="Retirada"
          type="date"
          value={inicio}
          min={hoje()}
          onChange={(e) => setInicio(e.target.value)}
          required
        />
        <Campo
          label="Devolução"
          type="date"
          value={fim}
          min={inicio || hoje()}
          onChange={(e) => setFim(e.target.value)}
          required
        />
      </div>
      <div className="estimate">
        <span>{dias > 0 ? `${dias} diária${dias > 1 ? "s" : ""}` : "Escolha o período"}</span>
        <strong>{dias > 0 && selecionado ? moeda(dias * selecionado.diaria) : "—"}</strong>
      </div>
      <div className="detail-box">
        <strong>Anunciante: {selecionado?.anunciante?.nome || "Não informado"}</strong>
      </div>
      {banco && (
        <fieldset className="credit-box">
          <legend>Crédito do banco anunciante</legend>
          <label className="checkbox">
            <input type="checkbox" checked={credito} onChange={(e) => setCredito(e.target.checked)} />
            Solicitar crédito para este aluguel
          </label>
          {credito && (
            <>
              <div className="form-grid">
                <Campo
                  label="Crédito solicitado (R$)"
                  type="number"
                  min="0.01"
                  max={dias > 0 ? dias * selecionado.diaria : undefined}
                  step="0.01"
                  value={valorCredito}
                  onChange={(e) => setValorCredito(e.target.value)}
                  required
                />
                <Campo
                  label="Parcelas solicitadas"
                  type="number"
                  min="1"
                  max="60"
                  value={parcelas}
                  onChange={(e) => setParcelas(e.target.value)}
                  required
                />
              </div>
              <p>
                O banco analisará o pedido e confirmará o valor e as parcelas. A solicitação não garante
                aprovação.
              </p>
            </>
          )}
        </fieldset>
      )}
      <p className="muted small-text">
        A retirada está incluída e a devolução não conta como diária. O pedido passará pela análise financeira
        de um agente.{pedido && " Ao salvar alterações, uma nova análise será necessária."}
      </p>
      <Submit busy={busy}>{pedido ? "Salvar pedido" : "Solicitar aluguel"}</Submit>
    </form>
  );
}
