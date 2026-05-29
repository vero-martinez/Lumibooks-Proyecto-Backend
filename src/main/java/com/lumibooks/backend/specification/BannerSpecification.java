package com.lumibooks.backend.specification;

import org.springframework.data.jpa.domain.Specification;

import com.lumibooks.backend.entity.Banner;

/**
 * Especificación para construir consultas dinámicas sobre la entidad Banner,
 * permitiendo filtrar por título y estado de activación.
 */
public class BannerSpecification {

    private BannerSpecification() {
    }
    
    // Especificación para filtrar banners cuyo título contenga una cadena dada (case-insensitive)
    public static Specification<Banner> titleContains(String title) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("title")),
                "%" + title.toLowerCase() + "%");
    }

    // Especificación para filtrar banners por su estado de activación
    public static Specification<Banner> hasActive(Boolean isActive) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isActive"), isActive);
    }

}