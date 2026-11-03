package com.autoloca.back.model;

import jakarta.persistence.*;

@Entity
@Table(name = "contratocredito")
public class ContratoCredito {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(optional = false)
  @JoinColumn(nullable = false)
  private Usuario banco;

  public Usuario getBanco() {
    return banco;
  }

  public void setBanco(Usuario banco) {
    this.banco = banco;
  }

  @Column(nullable = false, precision = 12, scale = 2)
  private java.math.BigDecimal valor;

  public java.math.BigDecimal getValor() {
    return valor;
  }

  public void setValor(java.math.BigDecimal valor) {
    this.valor = valor;
  }

  @Column(nullable = false)
  private Integer parcelas;

  public Integer getParcelas() {
    return parcelas;
  }

  public void setParcelas(Integer parcelas) {
    this.parcelas = parcelas;
  }
}
