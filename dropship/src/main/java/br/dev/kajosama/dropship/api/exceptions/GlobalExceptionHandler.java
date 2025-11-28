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

/**
 * @author Sam_Umbra
 * @Description Global exception handler for the API.
 *              This class provides centralized exception handling across all
 *              controllers,
 *              converting various exceptions into a standardized
 *              {@link ErrorResponse} format.
 *              It handles validation errors, entity not found errors, data
 *              integrity violations,
 *              transaction system errors, access denied exceptions, and generic
 *              exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link MethodArgumentNotValidException} which occurs when method
     * arguments
     * annotated with {@code @Valid} fail validation.
     * It extracts field-specific validation errors and returns a
     * {@link ErrorResponse} with HTTP status 400 (Bad Request).
     *
     * @param ex      The {@link MethodArgumentNotValidException} that was thrown.
     * @param request The current {@link HttpServletRequest}.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with
     *         validation details.
     */
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

    /**
     * Handles {@link EntityNotFoundException} which occurs when a requested entity
     * cannot be found in the database.
     * Returns a {@link ErrorResponse} with HTTP status 404 (Not Found).
     *
     * @param ex      The {@link EntityNotFoundException} that was thrown.
     * @param request The current {@link HttpServletRequest}.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with the
     *         error message.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request.getRequestURI(), null);
    }

    /**
     * Handles {@link EntityAlreadyExistsException} which occurs when an attempt is
     * made
     * to create an entity that already exists based on unique constraints.
     * Returns a {@link ErrorResponse} with HTTP status 409 (Conflict).
     *
     * @param ex      The {@link EntityAlreadyExistsException} that was thrown.
     * @param request The current {@link HttpServletRequest}.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with the
     *         conflict message.
     */
    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEntityAlreadyExists(EntityAlreadyExistsException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request.getRequestURI(), null);
    }

    /**
     * Handles {@link DataIntegrityViolationException} which occurs when a database
     * operation
     * violates integrity constraints (e.g., unique constraint, foreign key
     * constraint).
     * Returns a {@link ErrorResponse} with HTTP status 409 (Conflict).
     *
     * @param ex      The {@link DataIntegrityViolationException} that was thrown.
     * @param request The current {@link HttpServletRequest}.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with a
     *         data integrity violation message.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Data Integrity Violation",
                "Integrity Violation: " + getRootCauseMessage(ex),
                request.getRequestURI(), null);
    }

    /**
     * Handles {@link TransactionSystemException} which typically wraps lower-level
     * exceptions
     * that occur during a transaction, such as constraint violations during flush.
     * It attempts to extract the root cause message for a more specific error
     * description.
     * Returns a {@link ErrorResponse} with HTTP status 400 (Bad Request).
     *
     * @param ex      The {@link TransactionSystemException} that was thrown.
     * @param request The current {@link HttpServletRequest}.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with the
     *         transaction error message.
     */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ErrorResponse> handleTransactionSystemException(TransactionSystemException ex,
            HttpServletRequest request) {

        // Pega a causa raiz (SQLException, ConstraintViolationException etc.)
        Throwable rootCause = ex.getRootCause();

        // Mensagem a retornar: se houver rootCause, pega a mensagem dela, senão usa a
        // da exceção
        String message = rootCause != null ? rootCause.getMessage() : ex.getMessage();

        // Loga para debug detalhado
        log.error("Transaction failed", rootCause != null ? rootCause : ex);

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Transaction Error",
                message, // aqui você envia a mensagem real do erro
                request.getRequestURI(),
                null);
    }

    /**
     * Handles {@link AccessDeniedException} which occurs when an authenticated user
     * attempts to access a resource they do not have permission for.
     * Returns a {@link ErrorResponse} with HTTP status 403 (Forbidden).
     *
     * @param ex      The {@link AccessDeniedException} that was thrown.
     * @param request The current {@link HttpServletRequest}.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with an
     *         access denied message.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex,
            HttpServletRequest request) {

        String message = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage()
                : "You don't have permission to execute this action";

        return buildResponse(HttpStatus.FORBIDDEN, "Access Denied", message, request.getRequestURI(), null);
    }

    /**
     * Handles {@link AccountDeletedException} which is a custom exception
     * indicating
     * an operation was attempted on a deleted user account.
     * Returns a {@link ErrorResponse} with HTTP status 403 (Forbidden).
     *
     * @param ex      The {@link AccountDeletedException} that was thrown.
     * @param request The current {@link HttpServletRequest}.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with the
     *         account deleted message.
     */
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

    /**
     * Handles {@link IllegalArgumentException} which typically indicates an invalid
     * argument
     * was passed to a method. It attempts to parse field-specific errors from the
     * message
     * if the message follows a "field: error_message" format.
     * Returns a {@link ErrorResponse} with HTTP status 400 (Bad Request).
     *
     * @param ex      The {@link IllegalArgumentException} that was thrown.
     * @param request The current {@link HttpServletRequest}.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with the
     *         illegal argument message.
     */
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

    /**
     * Handles generic {@link Exception} instances that are not caught by more
     * specific handlers.
     * This serves as a fallback for unexpected errors.
     * Returns a {@link ErrorResponse} with HTTP status 500 (Internal Server Error).
     *
     * @param ex      The generic {@link Exception} that was thrown.
     * @param request The current {@link HttpServletRequest}.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with a
     *         generic error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(),
                request.getRequestURI(), null);
    }

    /**
     * Helper method to construct a {@link ResponseEntity} containing an
     * {@link ErrorResponse}.
     *
     * @param status           The HTTP status to return.
     * @param error            A short, descriptive error type.
     * @param message          A detailed message explaining the error.
     * @param path             The request path.
     * @param validationErrors An optional map of detailed validation errors.
     * @return A {@link ResponseEntity} configured with the provided error details.
     */
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message, String path,
            Map<String, String> validationErrors) {
        ErrorResponse body = validationErrors == null
                ? ErrorResponse.of(status.value(), error, message, path)
                : ErrorResponse.of(status.value(), error, message, path, validationErrors);

        return ResponseEntity.status(status).body(body);
    }

    /**
     * Recursively gets the root cause message of a {@link Throwable}.
     *
     * @param ex The {@link Throwable} to inspect.
     * @return The message of the innermost cause.
     */
    private String getRootCauseMessage(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root.getMessage();
    }
}
