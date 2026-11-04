export const moeda = (valor) => Number(valor).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
export const data = (valor) =>
  valor ? new Date(`${valor.slice(0, 10)}T12:00:00`).toLocaleDateString("pt-BR") : "—";
export const statusNome = {
  EM_ANALISE: "Em análise",
  APROVADO: "Aprovado",
  REPROVADO: "Reprovado",
  CANCELADO: "Cancelado",
  CONTRATADO: "Contratado",
};
export function hoje() {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
}
