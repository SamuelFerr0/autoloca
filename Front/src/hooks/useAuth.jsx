import { createContext, useContext, useEffect, useState } from "react";
import { requisicao, renovarCsrf } from "../services/httpClient";
const AuthContext = createContext(null);
export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  useEffect(() => {
    requisicao("/auth/me")
      .then(setUsuario)
      .catch((e) => {
        if (e.status !== 401) setErro(e.message);
      })
      .finally(() => setCarregando(false));
  }, []);
  async function login(username, password) {
    await renovarCsrf();
    await requisicao("/auth/login", { method: "POST", body: { username, password }, form: true });
    await renovarCsrf();
    setUsuario(await requisicao("/auth/me"));
    setErro("");
  }
  async function logout() {
    await requisicao("/auth/logout", { method: "POST" });
    setUsuario(null);
    await renovarCsrf();
  }
  return (
    <AuthContext.Provider value={{ usuario, setUsuario, carregando, login, logout, erro }}>
      {children}
    </AuthContext.Provider>
  );
}
export const useAuth = () => useContext(AuthContext);
