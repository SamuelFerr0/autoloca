import { useState } from "react";

export function useAcao() {
  const [busy, setBusy] = useState(false);
  const [erro, setErro] = useState("");

  async function executar(acao) {
    setBusy(true);
    setErro("");
    try {
      await acao();
    } catch (erro) {
      setErro(erro.message);
    } finally {
      setBusy(false);
    }
  }

  return { busy, erro, executar };
}
