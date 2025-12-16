package com.vehicle.management.exception;

import com.vehicle.management.dto.response.AppErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<AppErrorResponseDTO> handleGenericException(Exception ex) {
//
//        AppErrorResponseDTO error = AppErrorResponseDTO.builder()
//                .status(500)
//                .message("Erro interno do servidor")
//                .details("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde ou entre em contato com o suporte.")
//                .build();
//
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<AppErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String parametro = ex.getName();
        String tipoEsperado = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "tipo inválido";

        AppErrorResponseDTO error = AppErrorResponseDTO.builder()
                .status(400)
                .message("Parâmetro inválido")
                .details(
                        "O parâmetro '" + parametro +
                                "' deve ser do tipo " + tipoEsperado
                )
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AppErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        AppErrorResponseDTO error = AppErrorResponseDTO.builder()
                .status(400)
                .message(ex.getMessage())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<AppErrorResponseDTO> handleConstraintViolation(ConstraintViolationException ex) {

        String details = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getMessage())
                .findFirst()
                .orElse("Parâmetro inválido");

        AppErrorResponseDTO error = AppErrorResponseDTO.builder()
                .status(400)
                .message("Parâmetro inválido")
                .details(details)
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AppErrorResponseDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        String details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Erro de validação");

        AppErrorResponseDTO error = AppErrorResponseDTO.builder()
                .status(400)
                .message("Erro de validação")
                .details(details)
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AppErrorResponseDTO> handleBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AppErrorResponseDTO.builder()
                        .status(401)
                        .message("Credenciais inválidas")
                        .details("Usuário ou senha incorretos")
                        .build());
    }
}