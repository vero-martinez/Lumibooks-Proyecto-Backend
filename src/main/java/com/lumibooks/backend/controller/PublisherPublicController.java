package com.lumibooks.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.publisher.response.PublisherPublicResponse;
import com.lumibooks.backend.service.PublisherService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST público para la consulta de editoriales.
 * Accesible sin autenticación.
 */
@RestController
@RequestMapping("/api/public/publishers")
@RequiredArgsConstructor
public class PublisherPublicController {

    private final PublisherService publisherService;

    /** Lista editoriales activas, opcionalmente filtradas por nombre. */
    @GetMapping
    public ResponseEntity<List<PublisherPublicResponse>> getAllActive(
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(publisherService.getAllActive(name));
    }
}