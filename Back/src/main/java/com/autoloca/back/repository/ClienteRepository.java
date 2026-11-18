package com.autoloca.back.repository;

import com.autoloca.back.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
  @org.springframework.data.jpa.repository.Query(
      "select distinct p.cliente from Pedido p where p.automovel.anunciante.id = :agente")
  java.util.List<Cliente> findRelacionadosAoAgente(
      @org.springframework.data.repository.query.Param("agente") Long agente);

  java.util.Optional<Cliente> findByUsuarioId(Long id);
}
