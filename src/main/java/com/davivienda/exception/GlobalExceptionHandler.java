package com.davivienda.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la aplicación.
 * Captura las excepciones personalizadas y devuelve respuestas JSON con mensajes personalizados.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja las excepciones NoSuchResourceFoundException (404 Not Found)
     * 
     * @param ex La excepción capturada
     * @return Respuesta JSON con el mensaje de error personalizado
     */
    @ExceptionHandler(NoSuchResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleNoSuchResourceFoundException(NoSuchResourceFoundException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("error", "Not Found");
        errorResponse.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Maneja las excepciones BadResourceRequestException (400 Bad Request)
     * 
     * @param ex La excepción capturada
     * @return Respuesta JSON con el mensaje de error personalizado
     */
    @ExceptionHandler(BadResourceRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleBadResourceRequestException(BadResourceRequestException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja errores de tipo de parámetro incorrecto (400 Bad Request)
     * Ejemplo: cuando se envía "c" en lugar de un número para precioMax
     * 
     * @param ex La excepción capturada
     * @return Respuesta JSON con el mensaje de error personalizado
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Bad Request");
        
        String parameterName = ex.getName();
        String parameterValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        
        String message = String.format(
                "El parámetro '%s' tiene un valor inválido: '%s'. Se esperaba un valor de tipo %s.",
                parameterName, parameterValue, requiredType
        );
        
        errorResponse.put("message", message);
        errorResponse.put("parameter", parameterName);
        errorResponse.put("invalidValue", parameterValue);
        errorResponse.put("expectedType", requiredType);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja errores cuando faltan parámetros requeridos (400 Bad Request)
     * 
     * @param ex La excepción capturada
     * @return Respuesta JSON con el mensaje de error personalizado
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", String.format(
                "El parámetro requerido '%s' de tipo '%s' no está presente en la solicitud.",
                ex.getParameterName(), ex.getParameterType()
        ));
        errorResponse.put("parameter", ex.getParameterName());
        errorResponse.put("parameterType", ex.getParameterType());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}




