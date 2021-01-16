package io.github.blog.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ArticleDTO {
    
    private Long id;
    private String author;
    private String title;
    private String content;
    private String coverPicture;
    private LocalDate createDate;
    private LocalDate updateDate;
}
