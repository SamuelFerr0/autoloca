import { useEffect, useState } from "react";
import { API_URL } from "../config/apiConfig";
import { FOTOS_CARROS } from "../config/fotosCarros";
import { requisicao } from "../services/httpClient";
const consultas = new Map();
export default function FotoCarro({ carro }) {
  const [resultado, setResultado] = useState(null);
  const [falhou, setFalhou] = useState("");
  const nome = `${carro.marca.trim()} ${carro.modelo.trim()}`;
  useEffect(() => {
    if (carro.temFoto) return;
    let ativa = true;
    if (!consultas.has(nome)) {
      if (consultas.size > 200) consultas.delete(consultas.keys().next().value);
      consultas.set(
        nome,
        requisicao(
          `/fotos?marca=${encodeURIComponent(carro.marca)}&modelo=${encodeURIComponent(carro.modelo)}`,
        ).catch(() => null),
      );
    }
    consultas.get(nome).then((foto) => {
      if (ativa) setResultado({ nome, foto });
    });
    return () => {
      ativa = false;
    };
  }, [nome, carro.temFoto, carro.marca, carro.modelo]);
  const dados = resultado?.nome === nome ? resultado.foto : null;
  const enviada = carro.temFoto ? `${API_URL}/api/automoveis/${carro.id}/foto` : null;
  const local = FOTOS_CARROS[nome.toLowerCase()];
  const externa = dados?.url && falhou !== dados.url ? dados.url : null;
  const foto = enviada || externa || (falhou !== local ? local : null);
  return (
    <figure className="foto-veiculo">
      <div className="carro-foto">
        {foto && falhou !== foto ? (
          <img
            src={foto}
            alt={`${enviada ? "Foto de" : "Foto ilustrativa de"} ${nome}`}
            loading="lazy"
            onError={() => setFalhou(foto)}
          />
        ) : (
          <span>Foto não disponível · {nome}</span>
        )}
      </div>
      {!enviada && (
        <figcaption>
          {externa ? (
            <>
              <a href={dados.fonte} target="_blank" rel="noreferrer">
                Foto ilustrativa · {dados.autor}
              </a>
              {" · "}
              <a href={dados.licencaUrl} target="_blank" rel="noreferrer">
                {dados.licenca}
              </a>
            </>
          ) : (
            <a href="/carros/creditos.html" target="_blank" rel="noreferrer">
              Foto ilustrativa · créditos
            </a>
          )}
        </figcaption>
      )}
    </figure>
  );
}
