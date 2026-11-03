package com.autoloca.back.model;

import jakarta.persistence.*;

@Entity
@Table(name = "contrato")
public class Contrato {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @OneToOne(optional = false)
  @JoinColumn(nullable = false, unique = true)
  private Pedido pedido;

  public Pedido getPedido() {
    return pedido;
  }

  public void setPedido(Pedido pedido) {
    this.pedido = pedido;
  }

  @ManyToOne(optional = false)
  @JoinColumn(nullable = false)
  private Usuario proprietario;

  public Usuario getProprietario() {
    return proprietario;
  }

  public void setProprietario(Usuario proprietario) {
    this.proprietario = proprietario;
  }

  @OneToOne(cascade = CascadeType.ALL)
  private ContratoCredito credito;

  public ContratoCredito getCredito() {
    return credito;
  }

  public void setCredito(ContratoCredito credito) {
    this.credito = credito;
  }

  @Column(nullable = false)
  private java.time.LocalDateTime assinadoEm;

  public java.time.LocalDateTime getAssinadoEm() {
    return assinadoEm;
  }

  public void setAssinadoEm(java.time.LocalDateTime assinadoEm) {
    this.assinadoEm = assinadoEm;
  }
}
