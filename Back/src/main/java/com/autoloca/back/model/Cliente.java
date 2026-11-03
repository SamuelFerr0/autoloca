package com.autoloca.back.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class Cliente {
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
  private Usuario usuario;

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  @Column(nullable = false)
  private String rg;

  public String getRg() {
    return rg;
  }

  public void setRg(String rg) {
    this.rg = rg;
  }

  @Column(nullable = false, unique = true, length = 11)
  private String cpf;

  public String getCpf() {
    return cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf;
  }

  @Column(nullable = false)
  private String endereco;

  public String getEndereco() {
    return endereco;
  }

  public void setEndereco(String endereco) {
    this.endereco = endereco;
  }

  @Column(nullable = false)
  private String profissao;

  public String getProfissao() {
    return profissao;
  }

  public void setProfissao(String profissao) {
    this.profissao = profissao;
  }

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "rendimento", joinColumns = @JoinColumn(name = "cliente_id"))
  private java.util.List<Rendimento> rendimentos = new java.util.ArrayList<>();

  public java.util.List<Rendimento> getRendimentos() {
    return rendimentos;
  }

  public void setRendimentos(java.util.List<Rendimento> rendimentos) {
    this.rendimentos = rendimentos;
  }
}
