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

  @com.fasterxml.jackson.annotation.JsonIgnore
  public Pedido getPedido() {
    return pedido;
  }

  public void setPedido(Pedido pedido) {
    this.pedido = pedido;
  }

  @ManyToOne(optional = false)
  @JoinColumn(nullable = false)
  private Usuario proprietario;

  @com.fasterxml.jackson.annotation.JsonIgnore
  public Usuario getProprietario() {
    return proprietario;
  }

  public void setProprietario(Usuario proprietario) {
    this.proprietario = proprietario;
  }

  @OneToOne(cascade = CascadeType.ALL)
  private ContratoCredito credito;

  @com.fasterxml.jackson.annotation.JsonIgnore
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

  @Column(unique = true, updatable = false, length = 60)
  private String numero;

  public String getNumero() {
    return numero;
  }

  public void setNumero(String numero) {
    this.numero = numero;
  }

  @Column(updatable = false, length = 150)
  private String locatarioNome;

  public String getLocatarioNome() {
    return locatarioNome;
  }

  public void setLocatarioNome(String locatarioNome) {
    this.locatarioNome = locatarioNome;
  }

  @Column(updatable = false, length = 250)
  private String veiculoDescricao;

  public String getVeiculoDescricao() {
    return veiculoDescricao;
  }

  public void setVeiculoDescricao(String veiculoDescricao) {
    this.veiculoDescricao = veiculoDescricao;
  }

  @Column(updatable = false, precision = 12, scale = 2)
  private java.math.BigDecimal valorAluguel;

  public java.math.BigDecimal getValorAluguel() {
    return valorAluguel;
  }

  public void setValorAluguel(java.math.BigDecimal valorAluguel) {
    this.valorAluguel = valorAluguel;
  }

  @Column(updatable = false, columnDefinition = "text")
  private String documento;

  public String getDocumento() {
    return documento;
  }

  public void setDocumento(String documento) {
    this.documento = documento;
  }

  @com.fasterxml.jackson.annotation.JsonIgnore
  @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARBINARY)
  @Column(updatable = false, columnDefinition = "bytea")
  private byte[] arquivoPdf;

  public byte[] getArquivoPdf() {
    return arquivoPdf;
  }

  public void setArquivoPdf(byte[] arquivoPdf) {
    this.arquivoPdf = arquivoPdf;
  }
}
