package com.lumibooks.backend.mapper;

import com.lumibooks.backend.dto.category.request.CategoryRequest;
import com.lumibooks.backend.dto.category.response.CategoryPublicResponse;
import com.lumibooks.backend.dto.category.response.CategorySummaryResponse;
import com.lumibooks.backend.entity.Category;

/**
 * Mapper encargado de convertir entre Category y sus DTOs de request/response.
 */
public class CategoryMapper {

    private CategoryMapper() {
    }

    public static Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .isActive(true)
                .build();
    }

    public static CategorySummaryResponse toSummaryResponse(Category category) {
        return CategorySummaryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public static CategoryPublicResponse toPublicResponse(Category category) {
        return CategoryPublicResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}