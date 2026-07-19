package com.lumibooks.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.lumibooks.backend.dto.author.request.AuthorCreateRequest;
import com.lumibooks.backend.dto.author.request.AuthorUpdateRequest;
import com.lumibooks.backend.dto.author.response.AuthorAdminResponse;
import com.lumibooks.backend.dto.author.response.AuthorDetailResponse;
import com.lumibooks.backend.dto.author.response.AuthorPublicResponse;
import com.lumibooks.backend.dto.author.response.AuthorSummaryResponse;

public interface AuthorService {

    Page<AuthorSummaryResponse> getAuthors(String search, Boolean isActive, Pageable pageable);

    AuthorAdminResponse getAuthorByIdAdmin(Long id);

    AuthorAdminResponse createAuthor(AuthorCreateRequest request, MultipartFile profileImage);

    AuthorAdminResponse updateAuthor(Long id, AuthorUpdateRequest request, MultipartFile profileImage);

    void toggleAuthorStatus(Long id);

    Page<AuthorPublicResponse> getAuthorsPublic(String search, Pageable pageable);

    AuthorDetailResponse getAuthorByIdPublic(Long id);

    List<AuthorPublicResponse> getAllActive(String search);

}