import { useState } from "react";
import { Download, FileText } from "lucide-react";
import { useDados } from "../hooks/useDados";
import { Message, Empty } from "../components/UI";
import { API_URL } from "../config/apiConfig";
import { moeda, data } from "../utils/formatar";
import "./Contratos.css";
export default function Contratos() {
  const { dados, erro, carregando } = useDados("contratos");
  const [selecionado, setSelecionado] = useState(null);
  const [erroPdf, setErroPdf] = useState("");
  const [baixando, setBaixando] = useState(false);
  async function baixar(c) {
    setErroPdf("");
    setBaixando(true);
    try {
      const resposta = await fetch(`${API_URL}/api/contratos/${c.id}/pdf`, { credentials: "include" });
      if (!resposta.ok)
        throw new Error("Não foi possível baixar o contrato. Entre novamente ou tente mais tarde.");
      const url = URL.createObjectURL(await resposta.blob());
      const link = document.createElement("a");
      link.href = url;
      link.download = `${c.numero}.pdf`;
      document.body.appendChild(link);
      link.click();
      link.remove();
      setTimeout(() => URL.revokeObjectURL(url), 10000);
    } catch (e) {
      setErroPdf(e.message);
    } finally {
      setBaixando(false);
    }
  }
  return (
    <>
      <div className="page-heading">
        <div>
          <h1>Contratos</h1>
          <p>Documentos emitidos com os dados e as condições de cada aluguel.</p>
        </div>
      </div>
      <Message erro={erro || erroPdf} />
      {carregando ? (
        <p className="loading">Carregando contratos…</p>
      ) : dados.length === 0 ? (
        <Empty>Os contratos aparecerão após a aprovação e emissão pelo anunciante.</Empty>
      ) : (
        <div className="contract-grid">
          {dados.map((c) => (
            <article className="panel contract-card" key={c.id}>
              <span className="badge CONTRATADO">Emitido</span>
              <h2>{c.veiculoDescricao}</h2>
              <p className="eyebrow">{c.numero}</p>
              <dl>
                <div>
                  <dt>Contratante</dt>
                  <dd>{c.locatarioNome}</dd>
                </div>
                <div>
                  <dt>Emissão</dt>
                  <dd>{data(c.assinadoEm)}</dd>
                </div>
                <div>
                  <dt>Aluguel</dt>
                  <dd>{moeda(c.valorAluguel)}</dd>
                </div>
              </dl>
              <div className="contract-actions">
                <button className="button secondary" onClick={() => setSelecionado(c)}>
                  <FileText size={18} /> Ler contrato
                </button>
                <button className="button" disabled={baixando} onClick={() => baixar(c)}>
                  <Download size={18} /> Baixar PDF
                </button>
              </div>
            </article>
          ))}
        </div>
      )}
      {selecionado && (
        <section className="documento-area" aria-label="Documento do contrato">
          <div className="section-heading">
            <h2>{selecionado.numero}</h2>
            <button className="button secondary small" onClick={() => setSelecionado(null)}>
              Fechar documento
            </button>
          </div>
          <p className="muted">
            Esta é a cópia guardada na emissão. O PDF possui campos para assinatura das partes.
          </p>
          <pre className="documento-contrato">{selecionado.documento}</pre>
        </section>
      )}
    </>
  );
}
