package com.lumibooks.backend.mapper;

import com.lumibooks.backend.dto.publisher.request.PublisherRequest;
import com.lumibooks.backend.dto.publisher.response.PublisherPublicResponse;
import com.lumibooks.backend.dto.publisher.response.PublisherSummaryResponse;
import com.lumibooks.backend.entity.Publisher;

/**
 * Mapper encargado de convertir entre Publisher y sus DTOs de request/response.
 */
public class PublisherMapper {

    private PublisherMapper() {
    }

    public static Publisher toEntity(PublisherRequest request) {
        return Publisher.builder()
                .name(request.getName())
                .build();
    }

    public static PublisherSummaryResponse toSummaryResponse(Publisher publisher) {
        return PublisherSummaryResponse.builder()
                .id(publisher.getId())
                .name(publisher.getName())
                .isActive(publisher.getIsActive())
                .createdAt(publisher.getCreatedAt())
                .updatedAt(publisher.getUpdatedAt())
                .build();
    }

    public static PublisherPublicResponse toPublicResponse(Publisher publisher) {
        return PublisherPublicResponse.builder()
                .id(publisher.getId())
                .name(publisher.getName())
                .build();
    }
}