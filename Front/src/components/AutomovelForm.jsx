import { useDados } from "../hooks/useDados";
import { useAcao } from "../hooks/useAcao";
import { Campo, Submit, Message } from "./UI";
import { useState, useEffect } from "react";
import FotoCarro from "./FotoCarro";
import { salvarAutomovel } from "../services/aluguelService";

export default function AutomovelForm({ carro, onSalvo }) {
  const { dados: usuarios, erro: erroDados } = useDados("automoveis/proprietarios");
  const { busy, erro, executar } = useAcao();

  const [foto, setFoto] = useState(null);
  const [preview, setPreview] = useState("");
  const [erroFoto, setErroFoto] = useState("");
  useEffect(() => {
    if (!foto) {
      setPreview("");
      return;
    }
    const url = URL.createObjectURL(foto);
    setPreview(url);
    return () => URL.revokeObjectURL(url);
  }, [foto]);

  async function handleSalvar(evento) {
    evento.preventDefault();
    const formulario = new FormData(evento.currentTarget);
    const dados = {
      marca: formulario.get("marca"),
      modelo: formulario.get("modelo"),
      matricula: formulario.get("matricula"),
      placa: formulario.get("placa"),
      ano: Number(formulario.get("ano")),
      diaria: Number(formulario.get("diaria")),
      proprietarioId: Number(formulario.get("proprietarioId")),
    };
    await executar(async () => {
      await salvarAutomovel(carro?.id, dados, foto);
      onSalvo();
    });
  }

  return (
    <form className="form" onSubmit={handleSalvar}>
      <Message erro={erroFoto || erro || erroDados} />
      <div className="foto-upload">
        {preview ? (
          <img src={preview} alt="Prévia da foto selecionada" />
        ) : carro ? (
          <FotoCarro carro={carro} />
        ) : (
          <p>Adicione uma foto do automóvel</p>
        )}
        <Campo
          label="Foto do automóvel"
          type="file"
          accept="image/jpeg,image/png"
          onChange={(e) => {
            const arquivo = e.target.files[0];
            setErroFoto("");
            if (
              arquivo &&
              (!["image/jpeg", "image/png"].includes(arquivo.type) || arquivo.size > 3 * 1024 * 1024)
            ) {
              setErroFoto("Selecione uma foto JPG ou PNG de até 3 MB.");
              e.target.value = "";
              setFoto(null);
              return;
            }
            setFoto(arquivo || null);
          }}
        />
        <small>
          JPG ou PNG, até 3 MB e 6000 pixels por lado. Ao editar, a foto atual é mantida se nenhum arquivo for
          escolhido.
        </small>
      </div>
      <div className="form-grid">
        <Campo label="Marca" name="marca" defaultValue={carro?.marca} maxLength={80} required />
        <Campo label="Modelo" name="modelo" defaultValue={carro?.modelo} maxLength={100} required />
        <Campo label="Matrícula" name="matricula" defaultValue={carro?.matricula} maxLength={40} required />
        <Campo
          label="Placa (sem hífen)"
          name="placa"
          defaultValue={carro?.placa}
          maxLength={7}
          pattern="[A-Z]{3}[0-9][A-Z0-9][0-9]{2}"
          required
        />
        <Campo
          label="Ano"
          type="number"
          name="ano"
          defaultValue={carro?.ano || 2025}
          min="1900"
          max="2100"
          required
        />
        <Campo
          label="Diária (R$)"
          type="number"
          name="diaria"
          defaultValue={carro?.diaria}
          min="0.01"
          max="9999999999.99"
          step="0.01"
          required
        />
      </div>
      <label className="field">
        Proprietário
        <select name="proprietarioId" defaultValue={carro?.proprietario.id || ""} required>
          <option value="" disabled>
            Selecione
          </option>
          {usuarios.map((usuario) => (
            <option key={usuario.id} value={usuario.id}>
              {usuario.nome} ({usuario.perfil})
            </option>
          ))}
        </select>
      </label>
      <Submit busy={busy}>Salvar automóvel</Submit>
    </form>
  );
}
