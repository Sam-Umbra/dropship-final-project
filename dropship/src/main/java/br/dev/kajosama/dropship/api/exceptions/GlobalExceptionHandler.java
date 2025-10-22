package br.dev.kajosama.dropship.api.exceptions;

import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage(),
                        (msg1, msg2) -> msg1));

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Failed", "Sent fields validation error",
                request.getRequestURI(), errors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEntityAlreadyExists(EntityAlreadyExistsException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Data Integrity Violation",
                "Integrity Violation: " + getRootCauseMessage(ex),
                request.getRequestURI(), null);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ErrorResponse> handleTransactionSystemException(TransactionSystemException ex,
            HttpServletRequest request) {

        // Pega a causa raiz (SQLException, ConstraintViolationException etc.)
        Throwable rootCause = ex.getRootCause();

        // Mensagem a retornar: se houver rootCause, pega a mensagem dela, senão usa a da exceção
        String message = rootCause != null ? rootCause.getMessage() : ex.getMessage();

        // Loga para debug detalhado
        log.error("Transaction failed", rootCause != null ? rootCause : ex);

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Transaction Error",
                message, // aqui você envia a mensagem real do erro
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex,
            HttpServletRequest request) {

        String message = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage()
                : "You don't have permission to execute this action";

        return buildResponse(HttpStatus.FORBIDDEN, "Access Denied", message, request.getRequestURI(), null);
    }

    @ExceptionHandler(AccountDeletedException.class)
    public ResponseEntity<ErrorResponse> handleAccountDeleted(AccountDeletedException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse.of(
                        HttpStatus.FORBIDDEN.value(),
                        "Forbidden",
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
            HttpServletRequest request) {
        String message = ex.getMessage();
        Map<String, String> fieldErrors = null;

        if (message != null && message.contains(":")) {
            String[] parts = message.split(":", 2);
            String field = parts[0].trim().toLowerCase();
            String errorMsg = parts[1].trim();
            fieldErrors = Map.of(field, errorMsg);
            message = "Validation Failed";
        }

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Failed", message, request.getRequestURI(),
                fieldErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(),
                request.getRequestURI(), null);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message, String path,
            Map<String, String> validationErrors) {
        ErrorResponse body = validationErrors == null
                ? ErrorResponse.of(status.value(), error, message, path)
                : ErrorResponse.of(status.value(), error, message, path, validationErrors);

        return ResponseEntity.status(status).body(body);
    }

    private String getRootCauseMessage(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root.getMessage();
    }
}
