package com.autoloca.back.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class Dados {
  private Dados() {}

  public record Renda(
      @NotBlank @Size(max = 150) String empregador,
      @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal valor) {}

  public record PerfilCliente(
      @NotBlank @Size(max = 150) String nome,
      @NotBlank @Size(max = 30) String rg,
      @NotBlank @Pattern(regexp = "[0-9]{11}", message = "CPF deve conter 11 dígitos") String cpf,
      @NotBlank @Size(max = 250) String endereco,
      @NotBlank @Size(max = 100) String profissao,
      @NotNull @Size(max = 3) List<@NotNull @Valid Renda> rendimentos) {}

  public record Cadastro(
      @NotBlank
          @Pattern(
              regexp = "[a-zA-Z0-9._-]{3,80}",
              message = "Usuário: use de 3 a 80 letras, números, pontos ou traços")
          String username,
      @NotBlank @Size(min = 8, max = 72) String senha,
      @NotNull @Valid PerfilCliente cliente) {}

  public record CadastroAgente(
      @NotBlank
          @Pattern(
              regexp = "[a-zA-Z0-9._-]{3,80}",
              message = "Usuário: use de 3 a 80 letras, números, pontos ou traços")
          String username,
      @NotBlank @Size(min = 8, max = 72) String senha,
      @NotBlank @Size(max = 150) String nome,
      @NotNull com.autoloca.back.model.Perfil perfil,
      @Pattern(regexp = "[0-9]{14}") String cnpj,
      @Size(max = 250) String enderecoEmpresa,
      @Size(max = 150) String representante) {
    public CadastroAgente(
        String username, String senha, String nome, com.autoloca.back.model.Perfil perfil) {
      this(username, senha, nome, perfil, null, null, null);
    }
  }

  public record PerfilAgente(
      @NotBlank @Size(max = 150) String nome,
      @NotBlank @Pattern(regexp = "[0-9]{14}") String cnpj,
      @NotBlank @Size(max = 250) String enderecoEmpresa,
      @NotBlank @Size(max = 150) String representante) {}

  public record NovoPedido(
      @NotNull Long automovelId,
      @NotNull @FutureOrPresent LocalDate inicio,
      @NotNull @Future LocalDate fim,
      Boolean solicitaCredito,
      @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal valorCreditoSolicitado,
      @Min(1) @Max(60) Integer parcelasSolicitadas) {
    public NovoPedido {
      solicitaCredito = Boolean.TRUE.equals(solicitaCredito);
    }

    public NovoPedido(Long automovelId, LocalDate inicio, LocalDate fim) {
      this(automovelId, inicio, fim, false, null, null);
    }
  }

  public record Avaliacao(
      boolean aprovado,
      @NotBlank @Size(max = 2000) String parecer,
      @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal valorCreditoAprovado,
      @Min(1) @Max(60) Integer parcelasAprovadas) {
    public Avaliacao(boolean aprovado, String parecer) {
      this(aprovado, parecer, null, null);
    }
  }

  public record NovoContrato(
      Long proprietarioId,
      @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal valorCredito,
      @Min(1) @Max(60) Integer parcelas) {}

  public record Carro(
      @NotBlank @Size(max = 40) String matricula,
      @NotNull @Min(1900) @Max(2100) Integer ano,
      @NotBlank @Size(max = 80) String marca,
      @NotBlank @Size(max = 100) String modelo,
      @NotBlank
          @Pattern(
              regexp = "[A-Z]{3}[0-9][A-Z0-9][0-9]{2}",
              message = "Informe uma placa brasileira sem hífen")
          String placa,
      @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal diaria,
      Long proprietarioId) {}
}
