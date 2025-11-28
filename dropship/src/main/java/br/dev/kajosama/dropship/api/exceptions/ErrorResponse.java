package br.dev.kajosama.dropship.api.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author Sam_Umbra
 * @Description A record representing a standardized error response for the API.
 *              This record provides a consistent structure for conveying error
 *              information
 *              back to clients, including a timestamp, HTTP status code, error
 *              type,
 *              a descriptive message, the request path, and optional detailed
 *              validation errors.
 */
public record ErrorResponse(
        /**
         * The timestamp when the error occurred.
         * Formatted as "yyyy-MM-dd HH:mm:ss".
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timestamp,
        /**
         * The HTTP status code associated with the error (e.g., 400, 404, 500).
         */
        int status,
        /**
         * A short, descriptive error type (e.g., "Bad Request", "Not Found", "Internal
         * Server Error").
         */
        String error,
        /**
         * A detailed message explaining the error.
         */
        String message,
        /**
         * The path of the request that caused the error.
         */
        String path,
        /**
         * An optional map of detailed validation errors, typically used for
         * field-specific validation failures. The keys are field names and values are
         * error messages.
         */
        Map<String, String> details) {
    /**
     * Constructs a new {@code ErrorResponse} with the current timestamp and no
     * specific details.
     *
     * @param status  The HTTP status code.
     * @param error   The error type.
     * @param message The detailed error message.
     * @param path    The request path.
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }

    /**
     * Creates a new {@code ErrorResponse} instance with the current timestamp and
     * provided details.
     * This static factory method is useful for creating instances with detailed
     * validation errors.
     *
     * @param status  The HTTP status code.
     * @param error   The error type.
     * @param message The detailed error message.
     * @param path    The request path.
     * @param details An optional map of detailed validation errors.
     * @return A new {@code ErrorResponse} instance.
     */
    public static ErrorResponse of(int status, String error, String message, String path, Map<String, String> details) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, details);
    }

    /**
     * Creates a new {@code ErrorResponse} instance with the current timestamp and
     * no specific details.
     * This static factory method is useful for creating instances without detailed
     * validation errors.
     *
     * @param status  The HTTP status code.
     * @param error   The error type.
     * @param message The detailed error message.
     * @param path    The request path.
     * @return A new {@code ErrorResponse} instance.
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, null);
    }
}