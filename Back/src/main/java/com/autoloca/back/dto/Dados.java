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
      @NotNull @Size(max = 3) List<@Valid Renda> rendimentos) {}

  public record Cadastro(
      @NotBlank
          @Pattern(
              regexp = "[a-zA-Z0-9._-]{3,80}",
              message = "Usuário: use de 3 a 80 letras, números, pontos ou traços")
          String username,
      @NotBlank @Size(min = 8, max = 72) String senha,
      @NotNull @Valid PerfilCliente cliente) {}

  public record NovoPedido(
      @NotNull Long automovelId,
      @NotNull @FutureOrPresent LocalDate inicio,
      @NotNull @Future LocalDate fim) {}

  public record Avaliacao(boolean aprovado, @NotBlank @Size(max = 2000) String parecer) {}

  public record NovoContrato(
      @NotNull Long proprietarioId,
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
      @NotNull Long proprietarioId) {}
}
