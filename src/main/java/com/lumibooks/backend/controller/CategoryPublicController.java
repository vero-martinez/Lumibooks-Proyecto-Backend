package com.lumibooks.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.category.response.CategoryPublicResponse;
import com.lumibooks.backend.service.CategoryService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST público para la consulta de categorías.
 * Accesible sin autenticación.
 */
@RestController
@RequestMapping("/api/public/categories")
@RequiredArgsConstructor
public class CategoryPublicController {

    private final CategoryService categoryService;

    /** Lista todas las categorías activas. */
    @GetMapping
    public ResponseEntity<List<CategoryPublicResponse>> getAllActive() {
        return ResponseEntity.ok(categoryService.getAllActive());
    }
}