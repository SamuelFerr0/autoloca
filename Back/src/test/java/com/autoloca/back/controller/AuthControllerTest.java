package com.autoloca.back.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.autoloca.back.exception.GlobalExceptionHandler;
import com.autoloca.back.model.Perfil;
import com.autoloca.back.model.Usuario;
import com.autoloca.back.service.AgenteService;
import com.autoloca.back.service.ClienteService;
import com.autoloca.back.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
  @Mock private AgenteService agenteService;
  @Mock private ClienteService clienteService;
  @Mock private UsuarioService usuarioService;
  @InjectMocks private AuthController authController;
  private MockMvc mockMvc;

  @BeforeEach
  void preparar() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(authController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void respostaNaoExibeSenha() throws Exception {
    Usuario usuario = new Usuario();
    usuario.setNome("Ana");
    usuario.setSenha("senha-codificada");
    when(usuarioService.atual()).thenReturn(usuario);

    mockMvc
        .perform(get("/api/auth/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nome").value("Ana"))
        .andExpect(jsonPath("$.senha").doesNotExist());
  }

  @Test
  void cadastroNaoAceitaQuatroRendimentos() throws Exception {
    String corpo =
        """
        {
          "username":"ana", "senha":"Senha123!",
          "cliente":{
            "nome":"Ana", "rg":"MG123", "cpf":"12345678909",
            "endereco":"Rua A, 10", "profissao":"Estudante",
            "rendimentos":[
              {"empregador":"A", "valor":100},
              {"empregador":"B", "valor":100},
              {"empregador":"C", "valor":100},
              {"empregador":"D", "valor":100}
            ]
          }
        }
        """;

    mockMvc
        .perform(post("/api/auth/cadastro").contentType(MediaType.APPLICATION_JSON).content(corpo))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(clienteService);
  }

  @Test
  void cadastroSemDadosRetorna400() throws Exception {
    mockMvc
        .perform(post("/api/auth/cadastro").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(clienteService);
  }

  @Test
  void cadastroAgenteRetorna201SemSenha() throws Exception {
    Usuario usuario = new Usuario();
    usuario.setNome("Empresa A");
    usuario.setPerfil(Perfil.EMPRESA);
    usuario.setSenha("hash-secreto");
    when(agenteService.cadastrar(any())).thenReturn(usuario);
    mockMvc
        .perform(
            post("/api/auth/cadastro/agente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"username":"empresa_a","senha":"Senha123!","nome":"Empresa A","perfil":"EMPRESA"}
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.perfil").value("EMPRESA"))
        .andExpect(jsonPath("$.senha").doesNotExist());
    verify(agenteService).cadastrar(any());
  }

  @Test
  void cadastroAgenteSemNomeRetorna400() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/cadastro/agente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"username":"banco_a","senha":"Senha123!","perfil":"BANCO"}
                    """))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(agenteService);
  }

  @Test
  void cadastroAgenteComPerfilInexistenteRetorna400() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/cadastro/agente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"username":"agente","senha":"Senha123!","nome":"Agente","perfil":"ADMIN"}
                    """))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(agenteService);
  }
}
