package com.autoloca.back.repository;

import com.autoloca.back.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {
  java.util.List<Contrato> findByPedidoClienteUsuarioIdOrderByIdDesc(Long id);

  java.util.List<Contrato> findAllByOrderByIdDesc();
}
