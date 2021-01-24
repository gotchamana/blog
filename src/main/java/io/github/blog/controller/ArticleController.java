package io.github.blog.controller;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.io.IOException;
import java.util.*;

import javax.validation.Valid;

import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import io.github.blog.dto.*;
import io.github.blog.entity.*;
import io.github.blog.module.ArticleModule;
import io.github.blog.repository.ImageRepository;
import io.github.blog.service.ArticleService;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/articles")
@Log4j2
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;

    @Autowired
    private ImageRepository imageRepository;

    private ModelMapper modelMapper;

    public ArticleController() {
        modelMapper = new ModelMapper();
        configureModelMapper();
    }

    private void configureModelMapper() {
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        var extensions = List.of(TablesExtension.create());
        var parser = Parser.builder().extensions(extensions).build();
        var renderer = HtmlRenderer.builder().extensions(extensions).build();
        modelMapper.registerModule(new ArticleModule(parser, renderer));
	}

    @GetMapping("/{id}")
    public String getArticle(@PathVariable long id, Model model) {
        var article = modelMapper.map(articleService.findById(id).orElseThrow(), ArticleDTO.class);
        model.addAttribute("article", article);

        return "articles/article";
    }

    @GetMapping("/new")
    public String createNewArticleForm(@ModelAttribute(binding = false) ArticleDTO articleDTO) {
        articleDTO.setAuthor("王小明");
        return "articles/new-article";
    }

    @PostMapping("/new")
    public String createNewArticle(@Valid ArticleDTO articleDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Article binding error: {}", bindingResult);
            return "articles/new-article";
        }

        var article = articleService.save(modelMapper.map(articleDTO, Article.class));
        return "redirect:/articles/" + article.getId();
    }

    @PostMapping(value = "/upload-image", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Object> uploadImage(@Valid ImageDTO imageDTO, BindingResult bindingResult)
        throws IOException {

        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(Map.of("error", "noFileGiven"));

        var uploadImage = imageDTO.getImage();
        log.debug("Upload image name: {}, size: {} bytes, content type: {}", uploadImage.getOriginalFilename(),
            uploadImage.getSize(), uploadImage.getContentType());

        var image = new Image();
        image.setImage(uploadImage.getBytes());
        image = imageRepository.save(image);

        var path = MvcUriComponentsBuilder.fromMethodCall(on(ArticleController.class).downloadImage(image.getId()))
            .encode()
            .build()
            .getPath();
        return ResponseEntity.ok(Map.of("data", Map.of("filePath", path.substring(1))));
    }

    @GetMapping(value = "/download-image/{id}", produces = "image/*")
    @ResponseBody
    public Object downloadImage(@PathVariable String id) {
        return imageRepository.findById(id)
            .map(Image::getImage)
            .orElseThrow();
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadImageSizeExceeded() {
        return ResponseEntity.badRequest().body(Map.of("error", "fileTooLarge"));
    }
}
