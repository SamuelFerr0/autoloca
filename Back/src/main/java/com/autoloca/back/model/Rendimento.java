package com.autoloca.back.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Embeddable
public class Rendimento {
  @Column(nullable = false)
  private String empregador;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal valor;

  public Rendimento() {}

  public Rendimento(String empregador, BigDecimal valor) {
    this.empregador = empregador;
    this.valor = valor;
  }

  public String getEmpregador() {
    return empregador;
  }

  public void setEmpregador(String empregador) {
    this.empregador = empregador;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }
}
