package io.github.blog.dto;

import java.time.LocalDateTime;

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

    private String coverPictureUrl;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}