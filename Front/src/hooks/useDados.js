import { useCallback, useEffect, useState } from "react";
import { listar } from "../services/aluguelService";
export function useDados(recurso) {
  const [dados, setDados] = useState([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);
  const carregar = useCallback(async () => {
    setErro("");
    setCarregando(true);
    try {
      setDados(await listar(recurso));
    } catch (e) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  }, [recurso]);
  useEffect(() => {
    carregar();
  }, [carregar]);
  return { dados, erro, carregando, carregar };
}
