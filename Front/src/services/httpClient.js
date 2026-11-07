import { API_URL } from "../config/apiConfig";
let csrf;
export async function renovarCsrf() {
  const response = await fetch(`${API_URL}/api/auth/csrf`, { credentials: "include" });
  if (!response.ok) throw new Error("Não foi possível iniciar uma sessão segura.");
  csrf = await response.json();
}
export async function requisicao(path, { method = "GET", body, form = false } = {}) {
  try {
    const headers = {};
    if (method !== "GET") {
      if (!csrf) await renovarCsrf();
      headers[csrf.headerName] = csrf.token;
    }
    const multipart = body instanceof FormData;
    if (body && !multipart)
      headers["Content-Type"] = form ? "application/x-www-form-urlencoded" : "application/json";
    const options = { method, headers, credentials: "include" };
    if (body && method !== "GET")
      options.body = multipart ? body : form ? new URLSearchParams(body) : JSON.stringify(body);
    const response = await fetch(`${API_URL}/api${path}`, options);
    const data = response.status === 204 ? null : await response.json().catch(() => null);
    if (!response.ok) {
      const error = new Error(data?.mensagem || `Não foi possível concluir a operação (${response.status}).`);
      error.status = response.status;
      throw error;
    }
    return data;
  } catch (error) {
    if (error instanceof TypeError)
      throw new Error("Não foi possível conectar ao servidor. Confira se o back-end está rodando.");
    throw error;
  }
}
