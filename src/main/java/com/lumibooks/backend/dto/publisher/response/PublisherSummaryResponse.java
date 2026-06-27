package com.lumibooks.backend.dto.publisher.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para mostrar las editoriales en panel admin
 */
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class PublisherSummaryResponse {

    private Long id;
    private String name;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}