package com.autoloca.back.config;

import com.autoloca.back.repository.UsuarioRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

@Configuration
public class SecurityConfig {
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService userDetailsService(UsuarioRepository usuarios) {
    return username -> {
      var u =
          usuarios
              .findByUsername(username)
              .orElseThrow(() -> new UsernameNotFoundException("Usuário inválido"));
      return User.withUsername(u.getUsername())
          .password(u.getSenha())
          .roles(u.getPerfil().name())
          .build();
    };
  }

  @Bean
  CorsConfigurationSource cors(@Value("${app.cors-origins}") String origins) {
    var c = new CorsConfiguration();
    c.setAllowedOrigins(List.of(origins.split(",")));
    c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    c.setAllowedHeaders(List.of("Content-Type", "X-CSRF-TOKEN"));
    c.setAllowCredentials(true);
    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", c);
    return source;
  }

  @Bean
  SecurityFilterChain security(HttpSecurity http) throws Exception {
    return http.cors(c -> {})
        .authorizeHttpRequests(
            a ->
                a.requestMatchers(
                        "/api/auth/csrf",
                        "/api/auth/login",
                        "/api/auth/cadastro",
                        "/api/auth/cadastro/agente")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            e ->
                e.authenticationEntryPoint(
                        (req, res, ex) -> json(res, 401, "Faça login para continuar."))
                    .accessDeniedHandler(
                        (req, res, ex) ->
                            json(res, 403, "Acesso negado ou sessão expirada. Atualize a página.")))
        .formLogin(
            f ->
                f.loginProcessingUrl("/api/auth/login")
                    .successHandler((req, res, auth) -> json(res, 200, "Login realizado."))
                    .failureHandler(
                        (req, res, ex) -> json(res, 401, "Usuário ou senha inválidos.")))
        .logout(
            l ->
                l.logoutUrl("/api/auth/logout")
                    .logoutSuccessHandler((req, res, auth) -> res.setStatus(204)))
        .build();
  }

  private static void json(
      jakarta.servlet.http.HttpServletResponse res, int status, String mensagem)
      throws java.io.IOException {
    res.setStatus(status);
    res.setContentType("application/json;charset=UTF-8");
    res.getWriter().write("{\"mensagem\":\"" + mensagem + "\"}");
  }
}
