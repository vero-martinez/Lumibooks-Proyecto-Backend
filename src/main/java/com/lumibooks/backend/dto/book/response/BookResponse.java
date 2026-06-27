package com.lumibooks.backend.dto.book.response;

import java.math.BigDecimal;
import java.util.List;

import com.lumibooks.backend.dto.response.AuthorPublicResponse;
import com.lumibooks.backend.enums.BookFormat;
import com.lumibooks.backend.enums.BookLanguage;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO que representa la información a detalle de un libro al ser creado o editado
 */
@Getter
@Builder
public class BookResponse {

    private Long id;
    private String coverImageUrl;
    private String title;
    private List<AuthorPublicResponse> authors;
    private String description;
    private BigDecimal price;
    private String isbn;
    private Integer pageCount;
    private String publisherName;
    private BookLanguage language;
    private BookFormat format;
    private Short editionYear;
    private List<String> categories;

}