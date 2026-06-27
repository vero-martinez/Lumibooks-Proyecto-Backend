package com.lumibooks.backend.dto.publisher.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO utilizado para exponer editoriales.
 */
@Getter
@Builder
public class PublisherPublicResponse {

    private Long id;
    private String name;
    
}