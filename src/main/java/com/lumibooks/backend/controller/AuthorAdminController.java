package com.lumibooks.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lumibooks.backend.dto.author.request.AuthorCreateRequest;
import com.lumibooks.backend.dto.author.request.AuthorUpdateRequest;
import com.lumibooks.backend.dto.author.response.AuthorAdminResponse;
import com.lumibooks.backend.dto.author.response.AuthorSummaryResponse;
import com.lumibooks.backend.service.AuthorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/authors")
@RequiredArgsConstructor
public class AuthorAdminController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<Page<AuthorSummaryResponse>> getAuthors(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(authorService.getAuthors(search, isActive, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorAdminResponse> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorByIdAdmin(id));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<AuthorAdminResponse> createAuthor(
            @RequestPart("data") @Valid AuthorCreateRequest authorRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(authorRequest, profileImage));
    }

    @PatchMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<AuthorAdminResponse> updateAuthor(
            @PathVariable Long id,
            @RequestPart(value = "data", required = false) @Valid AuthorUpdateRequest authorRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        if (authorRequest == null)
            authorRequest = new AuthorUpdateRequest();
        return ResponseEntity.ok(authorService.updateAuthor(id, authorRequest, profileImage));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Void> toggleAuthorStatus(@PathVariable Long id) {
        authorService.toggleAuthorStatus(id);
        return ResponseEntity.noContent().build();
    }

}