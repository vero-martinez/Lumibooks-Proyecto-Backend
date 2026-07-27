package com.lumibooks.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /** Lista categorías activas, opcionalmente filtradas por nombre. */
    @GetMapping
    public ResponseEntity<List<CategoryPublicResponse>> getAllActive(
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(categoryService.getAllActive(name));
    }
}