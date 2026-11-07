import { useState } from "react";
import { API_URL } from "../config/apiConfig";
import { FOTOS_CARROS } from "../config/fotosCarros";

export default function FotoCarro({ carro }) {
  const [falhou, setFalhou] = useState(false);
  const nome = `${carro.marca.trim()} ${carro.modelo.trim()}`;
  const foto = carro.temFoto
    ? `${API_URL}/api/automoveis/${carro.id}/foto`
    : FOTOS_CARROS[nome.toLowerCase()];

  return (
    <div className="carro-foto">
      {foto && !falhou ? (
        <img
          src={foto}
          alt={`${carro.temFoto ? "Foto de" : "Foto ilustrativa de"} ${nome}`}
          loading="lazy"
          onError={() => setFalhou(true)}
        />
      ) : (
        <span>Foto não disponível</span>
      )}
    </div>
  );
}
