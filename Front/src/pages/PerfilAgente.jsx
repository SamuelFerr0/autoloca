import { Navigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { useAcao } from "../hooks/useAcao";
import { Campo, Submit, Message } from "../components/UI";
import { requisicao } from "../services/httpClient";
import { useState } from "react";
export default function PerfilAgente() {
  const { usuario, setUsuario } = useAuth();
  const { busy, erro, executar } = useAcao();
  const [sucesso, setSucesso] = useState("");
  if (usuario.perfil === "CLIENTE") return <Navigate to="/" replace />;
  return (
    <section className="panel company-profile">
      <h1>{usuario.perfil === "BANCO" ? "Meu banco" : "Minha empresa"}</h1>
      <p>Dados do anunciante usados nos novos contratos. Documentos já emitidos são preservados.</p>
      <form
        className="form"
        onSubmit={(e) => {
          e.preventDefault();
          const f = new FormData(e.currentTarget);
          executar(async () => {
            setUsuario(await requisicao("/agente/perfil", { method: "PUT", body: Object.fromEntries(f) }));
            setSucesso("Dados atualizados.");
          });
        }}
      >
        <Message erro={erro} sucesso={sucesso} />
        <Campo label="Razão social" name="nome" defaultValue={usuario.nome} maxLength={150} required />
        <Campo
          label="CNPJ (14 números)"
          name="cnpj"
          defaultValue={usuario.cnpj || ""}
          pattern="[0-9]{14}"
          maxLength={14}
          required
        />
        <Campo
          label="Endereço completo"
          name="enderecoEmpresa"
          defaultValue={usuario.enderecoEmpresa || ""}
          maxLength={250}
          required
        />
        <Campo
          label="Representante legal"
          name="representante"
          defaultValue={usuario.representante || ""}
          maxLength={150}
          required
        />
        <Submit busy={busy}>Salvar dados</Submit>
      </form>
    </section>
  );
}
