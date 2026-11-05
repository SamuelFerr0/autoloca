import { NavLink, Outlet } from "react-router-dom";
import { useAcao } from "../hooks/useAcao";
import { useAuth } from "../hooks/useAuth";
import { Message } from "./UI";

export default function Layout() {
  const { usuario, logout } = useAuth();
  const { busy, erro, executar } = useAcao();
  const cliente = usuario.perfil === "CLIENTE";
  const links = [
    ["/", "Início"],
    ["/frota", "Frota"],
    ["/pedidos", cliente ? "Meus pedidos" : "Pedidos"],
    ["/contratos", "Contratos"],
    ["/clientes", cliente ? "Meu cadastro" : "Clientes"],
  ];
  const perfis = { CLIENTE: "Cliente", EMPRESA: "Empresa", BANCO: "Banco" };

  return (
    <div className="app-shell">
      <header className="site-header">
        <NavLink to="/" className="brand">
          <img src="/logo.png" alt="" />
          AutoLoca
        </NavLink>
        <div className="account">
          <span>
            {usuario.nome} ({perfis[usuario.perfil]})
          </span>
          <button disabled={busy} onClick={() => executar(logout)}>
            Sair
          </button>
        </div>
      </header>
      <nav className="main-nav" aria-label="Menu principal">
        {links.map(([to, label]) => (
          <NavLink key={to} to={to} end={to === "/"}>
            {label}
          </NavLink>
        ))}
      </nav>
      <main className="content">
        <Message erro={erro} />
        <Outlet />
      </main>
      <footer>
        AutoLoca · Sistema de aluguel de carros · <NavLink to="/design">Design system</NavLink>
      </footer>
    </div>
  );
}
