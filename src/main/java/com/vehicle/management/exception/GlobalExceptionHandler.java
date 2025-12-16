package com.vehicle.management.exception;

import com.vehicle.management.dto.response.AppErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<AppErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String parametro = ex.getName();
        String tipoEsperado = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "tipo inválido";

        AppErrorResponse error = AppErrorResponse.builder()
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
    public ResponseEntity<AppErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        AppErrorResponse error = AppErrorResponse.builder()
                .status(400)
                .message(ex.getMessage())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<AppErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {

        String details = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getMessage())
                .findFirst()
                .orElse("Parâmetro inválido");

        AppErrorResponse error = AppErrorResponse.builder()
                .status(400)
                .message("Parâmetro inválido")
                .details(details)
                .build();

        return ResponseEntity.badRequest().body(error);
    }
}