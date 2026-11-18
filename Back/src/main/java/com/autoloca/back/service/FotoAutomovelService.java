package com.autoloca.back.service;

import java.net.*;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Service
public class FotoAutomovelService {
  public record Foto(
      String url, String fonte, String autor, String licenca, String licencaUrl, String titulo) {}

  private static final Foto VAZIA = new Foto(null, null, null, null, null, null);
  private final Map<String, Foto> cache =
      Collections.synchronizedMap(
          new LinkedHashMap<>(128, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Foto> e) {
              return size() > 200;
            }
          });
  private final HttpClient http;
  private final JsonMapper mapper = new JsonMapper();

  public FotoAutomovelService() {
    var builder = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(4));
    String proxy = System.getenv("HTTPS_PROXY");
    if (proxy != null && !proxy.isBlank()) {
      URI u = URI.create(proxy);
      builder.proxy(
          ProxySelector.of(new InetSocketAddress(u.getHost(), u.getPort() > 0 ? u.getPort() : 80)));
    }
    http = builder.build();
  }

  public Foto buscar(String marca, String modelo) {
    String nome = (marca.trim() + " " + modelo.trim()).replaceAll("\\s+", " ");
    Foto existente = cache.get(nome.toLowerCase(Locale.ROOT));
    if (existente != null) return existente;
    try {
      String title = URLEncoder.encode(nome, java.nio.charset.StandardCharsets.UTF_8);
      JsonNode resposta =
          json(
              "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=pageimages&piprop=thumbnail%7Cname&pithumbsize=1000&redirects=1&titles="
                  + title);
      JsonNode pagina =
          resposta.path("query").path("pages").properties().iterator().next().getValue();
      String url = pagina.path("thumbnail").path("source").asText("");
      String arquivo = pagina.path("pageimage").asText("");
      if (url.isBlank()
          || arquivo.isBlank()
          || URI.create(url).getHost() == null
          || !URI.create(url).getHost().endsWith(".wikimedia.org")) return memorizar(nome, VAZIA);
      JsonNode detalhe =
          json(
              "https://commons.wikimedia.org/w/api.php?action=query&format=json&prop=imageinfo&iiprop=extmetadata%7Curl&titles="
                  + URLEncoder.encode("File:" + arquivo, java.nio.charset.StandardCharsets.UTF_8));
      JsonNode info =
          detalhe
              .path("query")
              .path("pages")
              .properties()
              .iterator()
              .next()
              .getValue()
              .path("imageinfo")
              .path(0);
      JsonNode meta = info.path("extmetadata");
      String autor = limpar(meta.path("Artist").path("value").asText("Wikimedia Commons"));
      String licenca = limpar(meta.path("LicenseShortName").path("value").asText(""));
      if (licenca.isBlank()) return memorizar(nome, VAZIA);
      return memorizar(
          nome,
          new Foto(
              url,
              info.path("descriptionurl").asText("https://commons.wikimedia.org"),
              autor,
              licenca,
              meta.path("LicenseUrl").path("value").asText("https://commons.wikimedia.org"),
              arquivo));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return VAZIA;
    } catch (Exception e) {
      return VAZIA;
    }
  }

  private Foto memorizar(String nome, Foto foto) {
    cache.put(nome.toLowerCase(Locale.ROOT), foto);
    return foto;
  }

  private String limpar(String s) {
    return HtmlUtils.htmlUnescape(s.replaceAll("<[^>]*>", " ")).replaceAll("\\s+", " ").trim();
  }

  private JsonNode json(String url) throws java.io.IOException, InterruptedException {
    var request =
        HttpRequest.newBuilder(URI.create(url))
            .timeout(Duration.ofSeconds(8))
            .header("User-Agent", "AutoLocaAcademic/1.0 (car rental coursework)")
            .GET()
            .build();
    var response = http.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() != 200) throw new java.io.IOException("API de fotos indisponível");
    return mapper.readTree(response.body());
  }
}
