package com.autoloca.back.service;

import com.autoloca.back.dto.Dados;
import com.autoloca.back.model.Automovel;
import com.autoloca.back.model.Usuario;
import com.autoloca.back.repository.AutomovelRepository;
import com.autoloca.back.repository.UsuarioRepository;
import java.util.List;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.autoloca.back.exception.RegraNegocioException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AutomovelService {
  private final AutomovelRepository automovelRepository;
  private final UsuarioRepository usuarioRepository;
  private final UsuarioService usuarioService;

  public AutomovelService(
      AutomovelRepository automovelRepository,
      UsuarioRepository usuarioRepository,
      UsuarioService usuarioService) {
    this.automovelRepository = automovelRepository;
    this.usuarioRepository = usuarioRepository;
    this.usuarioService = usuarioService;
  }

  public List<Automovel> listar() {
    return automovelRepository.findAll();
  }

  public Automovel salvar(Long id, Dados.Carro dados) {
    return salvar(id, dados, null);
  }

  public Automovel salvar(Long id, Dados.Carro dados, MultipartFile foto) {
    usuarioService.exigirAgente();
    Automovel automovel;
    if (id == null) {
      automovel = new Automovel();
    } else {
      automovel = automovelRepository.buscarComBloqueio(id).orElseThrow();
    }

    Usuario proprietario = usuarioRepository.findById(dados.proprietarioId()).orElseThrow();
    automovel.setMatricula(dados.matricula().trim());
    automovel.setAno(dados.ano());
    automovel.setMarca(dados.marca().trim());
    automovel.setModelo(dados.modelo().trim());
    automovel.setPlaca(dados.placa());
    automovel.setDiaria(dados.diaria());
    automovel.setProprietario(proprietario);
    if (foto != null && !foto.isEmpty()) atualizarFoto(automovel, foto);
    return automovelRepository.save(automovel);
  }

  public Automovel buscar(Long id) {
    return automovelRepository.findById(id).orElseThrow();
  }

  private void atualizarFoto(Automovel carro, MultipartFile arquivo) {
    if (arquivo.getSize() > 3 * 1024 * 1024) {
      throw new RegraNegocioException("A foto deve ter no máximo 3 MB.");
    }
    try (var entrada = ImageIO.createImageInputStream(arquivo.getInputStream())) {
      var leitores = ImageIO.getImageReaders(entrada);
      if (!leitores.hasNext()) throw new RegraNegocioException("Selecione uma foto JPG ou PNG válida.");
      var leitor = leitores.next();
      try {
        leitor.setInput(entrada);
        String formato = leitor.getFormatName().toLowerCase(java.util.Locale.ROOT);
        if (!formato.equals("jpeg") && !formato.equals("png")) {
          throw new RegraNegocioException("Selecione uma foto JPG ou PNG.");
        }
        if (leitor.getWidth(0) > 6000 || leitor.getHeight(0) > 6000) {
          throw new RegraNegocioException("A foto deve ter até 6000 pixels em cada lado.");
        }
        leitor.read(0);
        carro.setFoto(arquivo.getBytes());
        carro.setFotoTipo("image/" + formato);
      } finally {
        leitor.dispose();
      }
    } catch (IOException e) {
      throw new RegraNegocioException("Não foi possível ler a foto. Selecione outro arquivo.");
    }
  }

  public void excluir(Long id) {
    usuarioService.exigirAgente();
    Automovel automovel = automovelRepository.findById(id).orElseThrow();
    automovelRepository.delete(automovel);
  }

  public List<Usuario> proprietarios() {
    usuarioService.exigirAgente();
    return usuarioRepository.findAll();
  }
}
