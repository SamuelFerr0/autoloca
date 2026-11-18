import { useAcao } from "../hooks/useAcao";
import { Submit, Message } from "./UI";
import { contratarPedido } from "../services/aluguelService";
import { moeda } from "../utils/formatar";
export default function ContratoForm({ pedido, onSalvo }) {
  const { busy, erro, executar } = useAcao();
  const locador = pedido.automovel.anunciante;
  const cliente = pedido.cliente.usuario;
  return (
    <form
      className="form"
      onSubmit={(e) => {
        e.preventDefault();
        const dados = new FormData(e.currentTarget);
        executar(async () => {
          await contratarPedido(pedido.id, { proprietarioId: Number(dados.get("proprietarioId")) });
          onSalvo();
        });
      }}
    >
      <Message erro={erro} />
      <div className="detail-box">
        <strong>
          Pedido #{pedido.id} · {pedido.automovel.marca} {pedido.automovel.modelo}
        </strong>
        <p>Locador: {locador?.nome}</p>
        <p>Contratante: {cliente.nome}</p>
        <p>Valor do aluguel: {moeda(pedido.valorTotal)}</p>
      </div>
      <label className="field">
        Registrar automóvel em nome de
        <select name="proprietarioId" defaultValue={locador?.id} required>
          {[locador, cliente].filter(Boolean).map((u) => (
            <option key={u.id} value={u.id}>
              {u.nome} ({u.perfil})
            </option>
          ))}
        </select>
      </label>
      {pedido.solicitaCredito && (
        <div className="credit-box">
          <strong>Crédito aprovado pelo banco</strong>
          <p>
            {moeda(pedido.valorCreditoAprovado)} em {pedido.parcelasAprovadas} parcelas.
          </p>
        </div>
      )}
      <p>
        O contrato e o PDF serão preenchidos com os dados atuais das partes, do veículo e da aprovação. O
        documento emitido não poderá ser editado.
      </p>
      <p className="muted small-text">
        Complete CNPJ, endereço e representante em Minha empresa ou Meu banco. Após a emissão, baixe o PDF
        para leitura e assinatura das partes.
      </p>
      <Submit busy={busy}>Emitir contrato e PDF</Submit>
    </form>
  );
}
