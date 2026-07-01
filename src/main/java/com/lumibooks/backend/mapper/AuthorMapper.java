package com.lumibooks.backend.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.author.request.AuthorCreateRequest;
import com.lumibooks.backend.dto.author.request.AuthorUpdateRequest;
import com.lumibooks.backend.dto.author.response.AuthorAdminResponse;
import com.lumibooks.backend.dto.author.response.AuthorDetailResponse;
import com.lumibooks.backend.dto.author.response.AuthorPublicResponse;
import com.lumibooks.backend.dto.author.response.AuthorSummaryResponse;
import com.lumibooks.backend.entity.Author;

@Component
public class AuthorMapper {

    // ============ Entity --> Response DTO ============        

    public AuthorSummaryResponse toSummaryResponse(Author author) {
        return AuthorSummaryResponse.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .profileImageUrl(author.getProfileImageUrl())
                .isActive(author.getIsActive())
                .createdAt(author.getCreatedAt())
                .build();
    }

    public AuthorAdminResponse toAdminResponse(Author author) {
        return AuthorAdminResponse.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .biography(author.getBiography())
                .profileImageUrl(author.getProfileImageUrl())
                .isActive(author.getIsActive())
                .createdAt(author.getCreatedAt())
                .updatedAt(author.getUpdatedAt())
                .build();
    }

    public AuthorDetailResponse toDetailResponse(Author author) {
        return AuthorDetailResponse.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .biography(author.getBiography())
                .profileImageUrl(author.getProfileImageUrl())
                .build();
    }

    public AuthorPublicResponse toPublicResponse(Author author) {
        return AuthorPublicResponse.builder()
                .id(author.getId())
                .fullName(author.getFullName())
                .profileImageUrl(author.getProfileImageUrl())
                .build();
    }

    // ============ Request DTO --> Entity ============

    public Author toEntity(AuthorCreateRequest request) {
        return Author.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .biography(request.getBiography())
                .build();
    }
    
    public void updateEntity(AuthorUpdateRequest request, Author author) {
        Optional.ofNullable(request.getFirstName()).ifPresent(author::setFirstName);
        Optional.ofNullable(request.getLastName()).ifPresent(author::setLastName);
        Optional.ofNullable(request.getBiography()).ifPresent(author::setBiography);
    }

}