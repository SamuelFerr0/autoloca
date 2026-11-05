package com.autoloca.back.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "automovel")
public class Automovel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(nullable = false, unique = true)
  private String matricula;

  public String getMatricula() {
    return matricula;
  }

  public void setMatricula(String matricula) {
    this.matricula = matricula;
  }

  @Column(nullable = false)
  private Integer ano;

  public Integer getAno() {
    return ano;
  }

  public void setAno(Integer ano) {
    this.ano = ano;
  }

  @Column(nullable = false)
  private String marca;

  public String getMarca() {
    return marca;
  }

  public void setMarca(String marca) {
    this.marca = marca;
  }

  @Column(nullable = false)
  private String modelo;

  public String getModelo() {
    return modelo;
  }

  public void setModelo(String modelo) {
    this.modelo = modelo;
  }

  @Column(nullable = false, unique = true)
  private String placa;

  public String getPlaca() {
    return placa;
  }

  public void setPlaca(String placa) {
    this.placa = placa;
  }

  @Column(nullable = false, precision = 12, scale = 2)
  private java.math.BigDecimal diaria;

  public java.math.BigDecimal getDiaria() {
    return diaria;
  }

  public void setDiaria(java.math.BigDecimal diaria) {
    this.diaria = diaria;
  }

  @JsonIgnore
  @JdbcTypeCode(SqlTypes.VARBINARY)
  @Column(length = 3145728)
  private byte[] foto;

  @JsonIgnore
  private String fotoTipo;

  @JsonIgnore
  public byte[] getFoto() { return foto; }

  public void setFoto(byte[] foto) { this.foto = foto; }

  @JsonIgnore
  public String getFotoTipo() { return fotoTipo; }

  public void setFotoTipo(String fotoTipo) { this.fotoTipo = fotoTipo; }

  public boolean isTemFoto() { return foto != null && foto.length > 0; }

  @ManyToOne(optional = false)
  @JoinColumn(nullable = false)
  private Usuario proprietario;

  public Usuario getProprietario() {
    return proprietario;
  }

  public void setProprietario(Usuario proprietario) {
    this.proprietario = proprietario;
  }
}
