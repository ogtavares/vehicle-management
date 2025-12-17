package com.vehicle.management.exception;

import com.vehicle.management.dto.response.AppErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AppErrorResponseDTO> handleAccessDenied(AccessDeniedException ex) {

        AppErrorResponseDTO error = AppErrorResponseDTO.builder()
                .status(403)
                .message("Acesso negado")
                .details("Você não possui permissão para acessar este recurso.")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<AppErrorResponseDTO> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(AppErrorResponseDTO.builder()
                        .status(409)
                        .message(ex.getMessage())
                        .build());
    }

}