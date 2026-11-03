package com.autoloca.back.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(RegraNegocioException.class)
  ResponseEntity<?> regra(RegraNegocioException erroValidacao) {
    return erro(422, erroValidacao.getMessage());
  }

  @ExceptionHandler(java.util.NoSuchElementException.class)
  ResponseEntity<?> ausente() {
    return erro(404, "Registro não encontrado.");
  }

  @ExceptionHandler(AccessDeniedException.class)
  ResponseEntity<?> proibido() {
    return erro(403, "Você não tem acesso a esta operação.");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<?> validacao(MethodArgumentNotValidException erroValidacao) {
    List<String> mensagens = new ArrayList<>();
    for (FieldError campo : erroValidacao.getBindingResult().getFieldErrors()) {
      mensagens.add(campo.getField() + ": " + campo.getDefaultMessage());
    }
    return erro(400, String.join("; ", mensagens));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<?> formato() {
    return erro(400, "Revise o formato dos dados enviados.");
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<?> duplicado() {
    return erro(
        409,
        "Usuário, CPF, placa ou matrícula já cadastrado, ou registro vinculado a outro cadastro.");
  }

  private ResponseEntity<?> erro(int status, String mensagem) {
    return ResponseEntity.status(status).body(Map.of("mensagem", mensagem));
  }
}
