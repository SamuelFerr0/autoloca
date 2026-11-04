package com.autoloca.back.config;

import com.autoloca.back.model.Automovel;
import com.autoloca.back.model.Cliente;
import com.autoloca.back.model.Perfil;
import com.autoloca.back.model.Rendimento;
import com.autoloca.back.model.Usuario;
import com.autoloca.back.repository.AutomovelRepository;
import com.autoloca.back.repository.ClienteRepository;
import com.autoloca.back.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DadosIniciais implements CommandLineRunner {
  private final UsuarioRepository usuarioRepository;
  private final ClienteRepository clienteRepository;
  private final AutomovelRepository automovelRepository;
  private final PasswordEncoder passwordEncoder;
  private final Environment environment;

  public DadosIniciais(
      UsuarioRepository usuarioRepository,
      ClienteRepository clienteRepository,
      AutomovelRepository automovelRepository,
      PasswordEncoder passwordEncoder,
      Environment environment) {
    this.usuarioRepository = usuarioRepository;
    this.clienteRepository = clienteRepository;
    this.automovelRepository = automovelRepository;
    this.passwordEncoder = passwordEncoder;
    this.environment = environment;
  }

  @Override
  @Transactional
  public void run(String... args) {
    provisionarAgente(Perfil.EMPRESA);
    provisionarAgente(Perfil.BANCO);
    if (!environment.getProperty("app.seed", Boolean.class, false)) {
      return;
    }

    Usuario empresa = criarUsuario("empresa", "Empresa123!", "AutoLoca Locadora", Perfil.EMPRESA);
    criarUsuario("banco", "Banco123!", "Banco Horizonte", Perfil.BANCO);
    Usuario usuarioCliente = criarUsuario("cliente", "Cliente123!", "Marina Alves", Perfil.CLIENTE);
    criarClienteLocal(usuarioCliente);
    criarFrotaLocal(empresa);
  }

  private void criarClienteLocal(Usuario usuario) {
    if (clienteRepository.findByUsuarioId(usuario.getId()).isPresent()) {
      return;
    }
    Cliente cliente = new Cliente();
    cliente.setUsuario(usuario);
    cliente.setCpf("12345678909");
    cliente.setRg("MG12345678");
    cliente.setEndereco("Rua das Acácias, 120 — Belo Horizonte, MG");
    cliente.setProfissao("Designer");
    cliente.getRendimentos().add(new Rendimento("Estúdio Horizonte", new BigDecimal("6500.00")));
    clienteRepository.save(cliente);
  }

  private void criarFrotaLocal(Usuario empresa) {
    if (automovelRepository.count() > 0) {
      return;
    }
    String[][] carros = {
      {"Fiat", "Argo", "149.90", "ABC1D23"},
      {"Volkswagen", "Polo", "179.90", "DEF4G56"},
      {"Chevrolet", "Onix", "169.90", "GHI7J89"},
      {"Jeep", "Renegade", "289.90", "JKL1M23"},
      {"Toyota", "Corolla", "259.90", "NOP4Q56"},
      {"Hyundai", "HB20", "159.90", "RST7U89"}
    };
    for (int indice = 0; indice < carros.length; indice++) {
      String[] dados = carros[indice];
      Automovel automovel = new Automovel();
      automovel.setMatricula("AUTO-00" + (indice + 1));
      automovel.setAno(2025);
      automovel.setMarca(dados[0]);
      automovel.setModelo(dados[1]);
      automovel.setDiaria(new BigDecimal(dados[2]));
      automovel.setPlaca(dados[3]);
      automovel.setProprietario(empresa);
      automovelRepository.save(automovel);
    }
  }

  private void provisionarAgente(Perfil perfil) {
    String username = environment.getProperty("BOOTSTRAP_" + perfil.name() + "_USER");
    String senha = environment.getProperty("BOOTSTRAP_" + perfil.name() + "_PASSWORD");
    if (username == null || senha == null) {
      return;
    }
    if (senha.length() < 12 || senha.getBytes(StandardCharsets.UTF_8).length > 72) {
      throw new IllegalArgumentException(
          "Senha de agente: mínimo 12 caracteres e máximo 72 bytes.");
    }
    String nome;
    if (perfil == Perfil.BANCO) {
      nome = "Banco parceiro";
    } else {
      nome = "Empresa parceira";
    }
    criarUsuario(username, senha, nome, perfil);
  }

  private Usuario criarUsuario(String username, String senha, String nome, Perfil perfil) {
    Optional<Usuario> existente = usuarioRepository.findByUsername(username);
    if (existente.isPresent()) {
      return existente.get();
    }
    Usuario usuario = new Usuario();
    usuario.setUsername(username);
    usuario.setSenha(passwordEncoder.encode(senha));
    usuario.setNome(nome);
    usuario.setPerfil(perfil);
    return usuarioRepository.save(usuario);
  }
}
