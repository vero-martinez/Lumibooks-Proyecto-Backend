package com.lumibooks.backend.mapper;

import org.springframework.stereotype.Component;

import com.lumibooks.backend.dto.response.AuthorPublicResponse;
import com.lumibooks.backend.entity.Author;

@Component
public class AuthorMapper {

    // ============ Entity --> Response DTO ============
    public AuthorPublicResponse toPublicResponse(Author author){
        
        return AuthorPublicResponse.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .biography(author.getBiography())
                .profileImageUrl(author.getProfileImageUrl())
                .build();

    }
    
}
