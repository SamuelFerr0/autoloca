import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./hooks/useAuth";
import Layout from "./components/Layout";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Frota from "./pages/Frota";
import Pedidos from "./pages/Pedidos";
import Clientes from "./pages/Clientes";
import Contratos from "./pages/Contratos";
import DesignSystem from "./pages/DesignSystem";
function Conteudo() {
  const { usuario, carregando } = useAuth();
  if (carregando) return <div className="app-loading">Carregando AutoLoca…</div>;
  if (!usuario) return <Login />;
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Dashboard />} />
        <Route path="frota" element={<Frota />} />
        <Route path="pedidos" element={<Pedidos />} />
        <Route path="clientes" element={<Clientes />} />
        <Route path="contratos" element={<Contratos />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}
export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/design" element={<DesignSystem />} />
        <Route
          path="*"
          element={
            <AuthProvider>
              <Conteudo />
            </AuthProvider>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}
