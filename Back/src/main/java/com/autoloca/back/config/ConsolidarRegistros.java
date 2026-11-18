package com.autoloca.back.config;

import com.autoloca.back.model.*;
import com.autoloca.back.repository.*;
import com.autoloca.back.service.DocumentoContrato;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ConsolidarRegistros implements CommandLineRunner {
  private final AutomovelRepository carros;
  private final PedidoRepository pedidos;
  private final ContratoRepository contratos;
  private final JdbcTemplate jdbc;

  public ConsolidarRegistros(
      AutomovelRepository carros,
      PedidoRepository pedidos,
      ContratoRepository contratos,
      JdbcTemplate jdbc) {
    this.carros = carros;
    this.pedidos = pedidos;
    this.contratos = contratos;
    this.jdbc = jdbc;
  }

  @Override
  @Transactional
  public void run(String... args) {
    for (Automovel carro : carros.findAll())
      if (carro.getAnunciante() == null) {
        Usuario dono = carro.getProprietario();
        if (dono.getPerfil() != Perfil.CLIENTE) carro.setAnunciante(dono);
        else
          pedidos
              .findFirstByAutomovelIdAndAgenteIsNotNullOrderByIdDesc(carro.getId())
              .ifPresent(p -> carro.setAnunciante(p.getAgente()));
      }
    for (Contrato contrato : contratos.findByDocumentoIsNull()) {
      DocumentoContrato.preparar(contrato, true);
      jdbc.update(
          "update contrato set"
              + " numero=?,locatario_nome=?,veiculo_descricao=?,valor_aluguel=?,documento=?,arquivo_pdf=?"
              + " where id=? and documento is null",
          contrato.getNumero(),
          contrato.getLocatarioNome(),
          contrato.getVeiculoDescricao(),
          contrato.getValorAluguel(),
          contrato.getDocumento(),
          contrato.getArquivoPdf(),
          contrato.getId());
    }
  }
}
