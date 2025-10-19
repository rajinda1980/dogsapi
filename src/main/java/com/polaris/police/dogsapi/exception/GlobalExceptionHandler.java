package com.polaris.police.dogsapi.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.polaris.police.dogsapi.model.response.FieldErrorResponse;
import com.polaris.police.dogsapi.model.response.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @Autowired
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Handle validation errors
     *
     * @param ex - Generated exception
     * @param webRequest - Web request
     * @return Message object
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest webRequest) {
        List<FieldErrorResponse> errorMessages =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(err ->
                                new FieldErrorResponse(err.getField(), messageSource.getMessage(err, LocaleContextHolder.getLocale())))
                        .collect(Collectors.toList());
        MessageDTO messageDTO = getMessageDTO(HttpStatus.BAD_REQUEST, errorMessages, webRequest);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDTO);
    }

    /**
     * Handles invalid JSON payloads like malformed body.
     *
     * @param ex - Generated exception
     * @param webRequest - Web request
     * @return message object
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MessageDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest webRequest) {

        Locale locale = LocaleContextHolder.getLocale();
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatEx) {
            Class<?> targetType = invalidFormatEx.getTargetType();

            // 1. Handling Enum values
            if (targetType.isEnum()) {
                return handleInvalidEnumValue(invalidFormatEx, webRequest, locale);
            }

            // 2. Handling date values
            if (targetType.equals(LocalDate.class)) {
                return handleInvalidDateFormat(webRequest, invalidFormatEx, locale);
            }
        }

        // 3. Default fallback for other JSON parsing issues
        FieldErrorResponse errorMessage = new FieldErrorResponse(
                "requestBody",
                messageSource.getMessage("error.malformed.json", null, "Malformed JSON request", locale)
        );
        MessageDTO messageDTO = getMessageDTO(HttpStatus.BAD_REQUEST, List.of(errorMessage), webRequest);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDTO);
    }

    /**
     * Handles invalid enum values.
     *
     * @param ex - Generated exception
     * @param webRequest - Web request
     * @param locale - Locale
     * @return message object
     */
    private ResponseEntity<MessageDTO> handleInvalidEnumValue(
            InvalidFormatException ex, WebRequest webRequest, Locale locale) {

        String fieldName = !ex.getPath().isEmpty() ? ex.getPath().get(0).getFieldName() : "unknown";
        String invalidValue = String.valueOf(ex.getValue());
        String allowedValues = Arrays.stream(ex.getTargetType().getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        String message = messageSource.getMessage("error.invalid.enum", new Object[]{invalidValue, fieldName, allowedValues}, locale);

        FieldErrorResponse errorMessage = new FieldErrorResponse(fieldName, message);
        MessageDTO messageDTO = getMessageDTO(HttpStatus.BAD_REQUEST, List.of(errorMessage), webRequest);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDTO);
    }

    /**
     * Handle invalid date format
     *
     * @param webRequest - Web request
     * @param invalidFormatEx - Generated exception
     * @param locale - Local
     * @return message object
     */
    private ResponseEntity<MessageDTO> handleInvalidDateFormat(WebRequest webRequest, InvalidFormatException invalidFormatEx, Locale locale) {
        String fieldName = invalidFormatEx.getPath().get(0).getFieldName();

        String message = messageSource.getMessage("error.invalid.date", new Object[]{fieldName}, locale);

        FieldErrorResponse fieldError = new FieldErrorResponse(fieldName, message);
        MessageDTO messageDTO = getMessageDTO(HttpStatus.BAD_REQUEST, List.of(fieldError), webRequest);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDTO);
    }

    /**
     * If resource does not exist
     *
     * @param ex - Generated exception
     * @param webRequest - Web Request
     * @return message object
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageDTO> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest webRequest) {
        MessageDTO messageDTO = getMessageDTO(HttpStatus.NOT_FOUND, ex.getMessage(), webRequest);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageDTO> handleException(Exception ex, WebRequest webRequest) {
        String message = messageSource.getMessage("internal.server.error", null, LocaleContextHolder.getLocale());
        MessageDTO messageDTO = getMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR, message, webRequest);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageDTO);
    }

    /**
     * Generate message object - For Field Errors
     *
     * @param status - Http Status
     * @param messages - Field errors
     * @param webRequest - Web request
     * @return message object
     */
    private MessageDTO getMessageDTO(HttpStatus status, List<FieldErrorResponse> messages, WebRequest webRequest) {
        return new MessageDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(), null,
                messages,
                webRequest.getDescription(false).replace("uri=", ""));
    }

    /**
     * Generate message object - For Single Message
     *
     * @param status - Http Status
     * @param message - Error message
     * @param webRequest - Web request
     * @return message object
     */
    private MessageDTO getMessageDTO(HttpStatus status, String message, WebRequest webRequest) {
        return new MessageDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                null,
                webRequest.getDescription(false).replace("uri=", ""));
    }
}
