package com.autoloca.back.service;

import com.autoloca.back.model.*;
import java.io.*;
import java.math.*;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

public final class DocumentoContrato {
  private DocumentoContrato() {}

  private static String texto(String s) {
    return s == null || s.isBlank() ? "Não informado no cadastro original" : s.trim();
  }

  private static String moeda(BigDecimal v) {
    return NumberFormat.getCurrencyInstance(Locale.of("pt", "BR")).format(v);
  }

  private static String data(LocalDate d) {
    return d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
  }

  public static void preparar(Contrato c, boolean legado) {
    Pedido p = c.getPedido();
    Cliente cliente = p.getCliente();
    Automovel carro = p.getAutomovel();
    Usuario locador = carro.getAnunciante() != null ? carro.getAnunciante() : p.getAgente();
    if (locador == null) {
      if (!legado)
        throw new IllegalStateException("Contrato sem identificação do agente responsável.");
      locador = new Usuario();
      locador.setNome("Agente não identificado no registro original");
    }
    if (c.getNumero() == null)
      c.setNumero(
          (legado ? "LEGADO-" : "AL-")
              + LocalDate.now().getYear()
              + "-"
              + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
    c.setLocatarioNome(cliente.getUsuario().getNome());
    c.setVeiculoDescricao(carro.getMarca() + " " + carro.getModelo() + " - " + carro.getPlaca());
    c.setValorAluguel(p.getValorTotal());
    BigDecimal credito = c.getCredito() == null ? BigDecimal.ZERO : c.getCredito().getValor();
    String documento =
        "CONTRATO PARTICULAR DE LOCAÇÃO DE AUTOMÓVEL\nNúmero: "
            + c.getNumero()
            + "\nEmissão: "
            + c.getAssinadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            + "\n\n";
    if (legado)
      documento +=
          "REGISTRO ANTERIOR: documento consolidado em "
              + data(LocalDate.now())
              + " a partir dos dados disponíveis nessa data. Este documento não recupera alterações"
              + " cadastrais anteriores à consolidação.\n\n";
    documento +=
        "1. IDENTIFICAÇÃO DAS PARTES\nLOCADOR: "
            + texto(locador.getNome())
            + ", CNPJ "
            + texto(locador.getCnpj())
            + ", endereço "
            + texto(locador.getEnderecoEmpresa())
            + ", representado por "
            + texto(locador.getRepresentante())
            + ".\nLOCATÁRIO: "
            + texto(cliente.getUsuario().getNome())
            + ", CPF "
            + cliente.getCpf()
            + ", RG "
            + cliente.getRg()
            + ", profissão "
            + cliente.getProfissao()
            + ", residente em "
            + cliente.getEndereco()
            + ".\n\n";
    documento +=
        "2. OBJETO E IDENTIFICAÇÃO DO VEÍCULO\nLocação de "
            + carro.getMarca()
            + " "
            + carro.getModelo()
            + ", ano "
            + carro.getAno()
            + ", placa "
            + carro.getPlaca()
            + ", matrícula "
            + carro.getMatricula()
            + ". O proprietário registrado neste contrato é "
            + c.getProprietario().getNome()
            + " ("
            + c.getProprietario().getPerfil()
            + ").\n\n";
    documento +=
        "3. PRAZO\nRetirada em "
            + data(p.getInicio())
            + " e devolução em "
            + data(p.getFim())
            + ". O período corresponde a "
            + java.time.temporal.ChronoUnit.DAYS.between(p.getInicio(), p.getFim())
            + " diárias. A data de devolução não integra a contagem das diárias. O local e as"
            + " condições de entrega e vistoria deverão ser conferidos pelas partes antes da"
            + " retirada.\n\n";
    documento +=
        "4. PREÇO E PAGAMENTO\nValor total da locação: "
            + moeda(p.getValorTotal())
            + ". Valor coberto pelo crédito: "
            + moeda(credito)
            + ". Saldo não financiado: "
            + moeda(p.getValorTotal().subtract(credito))
            + ", a ser quitado ao locador até a retirada. O valor desta locação e o período ficam"
            + " fixados neste instrumento.\n\n";
    if (c.getCredito() != null) {
      ContratoCredito cr = c.getCredito();
      BigDecimal parcela =
          cr.getValor().divide(BigDecimal.valueOf(cr.getParcelas()), 2, RoundingMode.DOWN);
      BigDecimal ultima =
          cr.getValor().subtract(parcela.multiply(BigDecimal.valueOf(cr.getParcelas() - 1)));
      documento +=
          "5. CRÉDITO ASSOCIADO\nCrédito solicitado pelo locatário e aprovado por "
              + cr.getBanco().getNome()
              + ", CNPJ "
              + texto(cr.getBanco().getCnpj())
              + ", no valor de "
              + moeda(cr.getValor())
              + ". Pagamento em "
              + cr.getParcelas()
              + " parcelas mensais, de "
              + moeda(parcela)
              + ", com a última parcela de "
              + moeda(ultima)
              + " para ajuste de centavos. Primeiro vencimento: "
              + data(p.getInicio().plusMonths(1))
              + "; demais vencimentos nos meses subsequentes. Não há juros ou encargos financeiros"
              + " adicionais cadastrados neste instrumento.\n\n";
    } else
      documento +=
          "5. CRÉDITO ASSOCIADO\nEsta locação não possui contrato de crédito associado.\n\n";
    documento +=
        "6. OBRIGAÇÕES DO LOCADOR\n"
            + "Entregar o automóvel em condições de uso, apresentar suas condições na vistoria e"
            + " disponibilizar a documentação necessária à circulação.\n\n"
            + "7. OBRIGAÇÕES DO LOCATÁRIO\n"
            + "Utilizar o automóvel de acordo com a legislação e a finalidade contratada, zelar"
            + " pela conservação e devolver o veículo no prazo, nas condições registradas na"
            + " vistoria e com o nível de combustível acordado na entrega. É vedada a cessão ou"
            + " sublocação sem autorização do locador. Danos, multas e despesas de responsabilidade"
            + " do locatário deverão ser comprovados e apurados conforme a legislação"
            + " aplicável.\n\n"
            + "8. ALTERAÇÕES, CANCELAMENTO E DISPOSIÇÕES GERAIS\n"
            + "Depois da formalização, o texto e os valores deste documento não são editados no"
            + " sistema. Uma alteração ou rescisão posterior depende de acordo separado entre as"
            + " partes, preservando este registro. Este instrumento não presume entrega do veículo"
            + " ou quitação de valores. Condições não registradas deverão ser pactuadas pelas"
            + " partes, respeitando a legislação aplicável.\n\n"
            + "9. ASSINATURAS\n"
            + "As partes deverão conferir os dados e as condições antes de assinar. A emissão pelo"
            + " sistema não constitui assinatura digital.\n\n"
            + "Local e data: ______________________________________________\n\n"
            + "Locador / representante: ____________________________________\n\n"
            + "Locatário: _________________________________________________\n\n"
            + "Testemunha 1 - Nome e CPF: __________________________________\n\n"
            + "Testemunha 2 - Nome e CPF: __________________________________\n";
    c.setDocumento(documento);
    c.setArquivoPdf(pdf(documento));
  }

  public static byte[] pdf(String texto) {
    try (PDDocument doc = new PDDocument();
        InputStream fonte = DocumentoContrato.class.getResourceAsStream("/fonts/DejaVuSans.ttf");
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      PDType0Font font = PDType0Font.load(doc, fonte, true);
      PDPageContentStream stream = null;
      float y = 0;
      for (String paragrafo : texto.split("\n", -1)) {
        List<String> linhas = new ArrayList<>();
        String linha = "";
        for (String palavra : paragrafo.split(" ")) {
          String candidata = linha.isEmpty() ? palavra : linha + " " + palavra;
          if (font.getStringWidth(candidata) / 1000 * 10 > 475 && !linha.isEmpty()) {
            linhas.add(linha);
            linha = palavra;
          } else linha = candidata;
        }
        linhas.add(linha);
        for (String l : linhas) {
          if (stream == null || y < 60) {
            if (stream != null) stream.close();
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            stream = new PDPageContentStream(doc, page);
            y = 785;
          }
          stream.beginText();
          stream.setFont(font, 10);
          stream.newLineAtOffset(60, y);
          stream.showText(l);
          stream.endText();
          y -= 15;
        }
      }
      if (stream != null) stream.close();
      doc.getDocumentInformation().setTitle("Contrato de locação - AutoLoca");
      doc.save(out);
      return out.toByteArray();
    } catch (IOException e) {
      throw new IllegalStateException("Não foi possível emitir o contrato em PDF.", e);
    }
  }
}
