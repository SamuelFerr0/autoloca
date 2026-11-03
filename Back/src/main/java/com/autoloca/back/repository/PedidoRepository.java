package com.autoloca.back.repository;

import com.autoloca.back.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
  java.util.List<Pedido> findByClienteUsuarioIdOrderByIdDesc(Long id);

  java.util.List<Pedido> findAllByOrderByIdDesc();

  boolean existsByClienteId(Long id);

  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @org.springframework.data.jpa.repository.Query("select p from Pedido p where p.id = :id")
  java.util.Optional<Pedido> buscarComBloqueio(
      @org.springframework.data.repository.query.Param("id") Long id);

  @org.springframework.data.jpa.repository.Query(
      "select count(p) from Pedido p where p.automovel.id=:automovel and p.status in :status and"
          + " p.inicio < :fim and p.fim > :inicio and p.id <> :ignorar")
  long contarConflitos(
      @org.springframework.data.repository.query.Param("automovel") Long automovel,
      @org.springframework.data.repository.query.Param("status")
          java.util.List<StatusPedido> status,
      @org.springframework.data.repository.query.Param("inicio") java.time.LocalDate inicio,
      @org.springframework.data.repository.query.Param("fim") java.time.LocalDate fim,
      @org.springframework.data.repository.query.Param("ignorar") Long ignorar);
}
