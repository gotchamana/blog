package io.github.blog.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ArticleDTO {
    
    private Long id;

    @NotBlank
    private String author;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String coverPicture;
    private LocalDate createDate;
    private LocalDate updateDate;
}
