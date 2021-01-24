package io.github.blog.dto;

import org.springframework.web.multipart.MultipartFile;

import io.github.blog.validation.NotEmpty;
import lombok.Data;

@Data
public class ImageDTO {
    
    @NotEmpty
    private MultipartFile image;
}
