package com.lumibooks.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumibooks.backend.dto.author.response.AuthorDetailResponse;
import com.lumibooks.backend.dto.author.response.AuthorPublicResponse;
import com.lumibooks.backend.service.AuthorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public/authors")
@RequiredArgsConstructor
public class AuthorPublicController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<Page<AuthorPublicResponse>> getAuthors(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "firstName") Pageable pageable) {
        return ResponseEntity.ok(authorService.getAuthorsPublic(search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDetailResponse> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorByIdPublic(id));
    }

}