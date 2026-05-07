package com.lumibooks.backend.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Clase encargada de manejar las excepciones globales de la aplicación.
 * Permite retornar respuestas HTTP personalizadas para distintos tipos
 * de errores como recursos no encontrados, solicitudes inválidas,
 * errores de autorización y errores internos del servidor.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    // Manejar ResourceNotFoundException (404 Not Found)
    @ExceptionHandler(ResourceNotFoundException.class)
    // Respuesta personalizada para recursos no encontrados
    public ResponseEntity<?> handleResourceNotFound(
            ResourceNotFoundException ex, // Excepción capturada
            WebRequest request) { // Información de la solicitud

        Map<String, Object> body = new HashMap<>(); // Crear un cuerpo de respuesta personalizado
        body.put("timestamp", LocalDateTime.now()); // Agregar la marca de tiempo
        body.put("status", HttpStatus.NOT_FOUND.value()); // Agregar el código de estado HTTP
        body.put("error", "No encontrado"); // Agregar una descripción del error
        body.put("message", ex.getMessage()); // Agregar el mensaje de la excepción
        body.put("path", request.getDescription(false).replace("uri=", "")); // Agregar la ruta de la solicitud

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Manejar UnauthorizedException (401 Unauthorized)
    @ExceptionHandler(UnauthorizedException.class)
    // Respuesta personalizada para errores de autorización
    public ResponseEntity<?> handleUnauthorized(
            UnauthorizedException ex, 
            WebRequest request) { 

        Map<String, Object> body = new HashMap<>(); 
        body.put("timestamp", LocalDateTime.now()); 
        body.put("status", HttpStatus.UNAUTHORIZED.value()); 
        body.put("error", "No autorizado"); 
        body.put("message", ex.getMessage()); 
        body.put("path", request.getDescription(false).replace("uri=", "")); 

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    // Manejar BadRequestException (400 Bad Request)
    @ExceptionHandler(BadRequestException.class)
    // Respuesta personalizada para solicitudes incorrectas
    public ResponseEntity<?> handleBadRequest(
            BadRequestException ex, 
            WebRequest request) { 

        Map<String, Object> body = new HashMap<>(); 
        body.put("timestamp", LocalDateTime.now()); 
        body.put("status", HttpStatus.BAD_REQUEST.value()); 
        body.put("error", "Solicitud inválida"); 
        body.put("message", ex.getMessage()); 
        body.put("path", request.getDescription(false).replace("uri=", "")); 

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Manejar cualquier otra excepción no controlada (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    // Respuesta personalizada para errores internos del servidor
    public ResponseEntity<?> handleInternalServerError(
            Exception ex,
            WebRequest request) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Error interno del servidor");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
