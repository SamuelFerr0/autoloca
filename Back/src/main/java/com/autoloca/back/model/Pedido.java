package com.autoloca.back.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pedido")
public class Pedido {
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
  private Cliente cliente;

  public Cliente getCliente() {
    return cliente;
  }

  public void setCliente(Cliente cliente) {
    this.cliente = cliente;
  }

  @ManyToOne(optional = false)
  @JoinColumn(nullable = false)
  private Automovel automovel;

  public Automovel getAutomovel() {
    return automovel;
  }

  public void setAutomovel(Automovel automovel) {
    this.automovel = automovel;
  }

  @Column(nullable = false)
  private java.time.LocalDate inicio;

  public java.time.LocalDate getInicio() {
    return inicio;
  }

  public void setInicio(java.time.LocalDate inicio) {
    this.inicio = inicio;
  }

  @Column(nullable = false)
  private java.time.LocalDate fim;

  public java.time.LocalDate getFim() {
    return fim;
  }

  public void setFim(java.time.LocalDate fim) {
    this.fim = fim;
  }

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatusPedido status;

  public StatusPedido getStatus() {
    return status;
  }

  public void setStatus(StatusPedido status) {
    this.status = status;
  }

  @Column(nullable = false, precision = 12, scale = 2)
  private java.math.BigDecimal valorTotal;

  public java.math.BigDecimal getValorTotal() {
    return valorTotal;
  }

  public void setValorTotal(java.math.BigDecimal valorTotal) {
    this.valorTotal = valorTotal;
  }

  @Column(length = 2000)
  private String parecer;

  public String getParecer() {
    return parecer;
  }

  public void setParecer(String parecer) {
    this.parecer = parecer;
  }

  @ManyToOne private Usuario agente;

  public Usuario getAgente() {
    return agente;
  }

  public void setAgente(Usuario agente) {
    this.agente = agente;
  }

  @Column(nullable = false)
  private java.time.LocalDateTime criadoEm;

  public java.time.LocalDateTime getCriadoEm() {
    return criadoEm;
  }

  public void setCriadoEm(java.time.LocalDateTime criadoEm) {
    this.criadoEm = criadoEm;
  }

  @Column(nullable = false, columnDefinition = "boolean default false")
  private boolean solicitaCredito;

  public boolean isSolicitaCredito() {
    return solicitaCredito;
  }

  public void setSolicitaCredito(boolean solicitaCredito) {
    this.solicitaCredito = solicitaCredito;
  }

  @Column(precision = 12, scale = 2)
  private java.math.BigDecimal valorCreditoSolicitado;

  public java.math.BigDecimal getValorCreditoSolicitado() {
    return valorCreditoSolicitado;
  }

  public void setValorCreditoSolicitado(java.math.BigDecimal valorCreditoSolicitado) {
    this.valorCreditoSolicitado = valorCreditoSolicitado;
  }

  private Integer parcelasSolicitadas;

  public Integer getParcelasSolicitadas() {
    return parcelasSolicitadas;
  }

  public void setParcelasSolicitadas(Integer parcelasSolicitadas) {
    this.parcelasSolicitadas = parcelasSolicitadas;
  }

  @Column(precision = 12, scale = 2)
  private java.math.BigDecimal valorCreditoAprovado;

  public java.math.BigDecimal getValorCreditoAprovado() {
    return valorCreditoAprovado;
  }

  public void setValorCreditoAprovado(java.math.BigDecimal valorCreditoAprovado) {
    this.valorCreditoAprovado = valorCreditoAprovado;
  }

  private Integer parcelasAprovadas;

  public Integer getParcelasAprovadas() {
    return parcelasAprovadas;
  }

  public void setParcelasAprovadas(Integer parcelasAprovadas) {
    this.parcelasAprovadas = parcelasAprovadas;
  }
}
