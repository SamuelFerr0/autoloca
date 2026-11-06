import { House, CarFront, ClipboardList, FileText, Users } from "lucide-react";
import { NavLink, Outlet } from "react-router-dom";
import { useAcao } from "../hooks/useAcao";
import { useAuth } from "../hooks/useAuth";
import { Message } from "./UI";

export default function Layout() {
  const { usuario, logout } = useAuth();
  const { busy, erro, executar } = useAcao();
  const cliente = usuario.perfil === "CLIENTE";
  const links = [
    ["/", "Início", House],
    ["/frota", "Frota", CarFront],
    ["/pedidos", cliente ? "Meus pedidos" : "Pedidos", ClipboardList],
    ["/contratos", "Contratos", FileText],
    ["/clientes", cliente ? "Meu cadastro" : "Clientes", Users],
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
        {links.map(([to, label, Icon]) => (
          <NavLink key={to} to={to} end={to === "/"}>
            <Icon size={17} aria-hidden="true" />
            {label}
          </NavLink>
        ))}
      </nav>
      <main className="content">
        <Message erro={erro} />
        <Outlet />
      </main>
      <footer>
        <div className="grid gap-8 md:grid-cols-3">
          <div>
            <strong className="text-2xl">AutoLoca</strong>
            <p className="mt-3">Um carro para cada caminho.</p>
          </div>
          <div>
            <strong>Seu aluguel</strong>
            <div className="mt-3 flex flex-col gap-2">
              <NavLink to="/frota">Conhecer a frota</NavLink>
              <NavLink to="/pedidos">Acompanhar pedidos</NavLink>
              <NavLink to="/contratos">Consultar contratos</NavLink>
            </div>
          </div>
          <div>
            <strong>Sobre o projeto</strong>
            <div className="mt-3 flex flex-col gap-2">
              <NavLink to="/design">Design system</NavLink>
              <a href="/carros/creditos.html">Créditos das imagens</a>
              <span>Projeto acadêmico · DIAW</span>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}
