package com.autoloca.back.repository;

import com.autoloca.back.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutomovelRepository extends JpaRepository<Automovel, Long> {
  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @org.springframework.data.jpa.repository.Query("select a from Automovel a where a.id = :id")
  java.util.Optional<Automovel> buscarComBloqueio(
      @org.springframework.data.repository.query.Param("id") Long id);
}
