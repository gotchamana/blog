package io.github.blog.controller;

import java.util.*;

import javax.validation.Valid;

import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import io.github.blog.dto.ArticleDTO;
import io.github.blog.entity.Article;
import io.github.blog.module.ArticleModule;
import io.github.blog.repository.ArticleRepository;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/articles")
@Log4j2
public class ArticleController {
    
    @Autowired
    private ArticleRepository articleRepository;

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
        var article = modelMapper.map(articleRepository.findById(id).orElseThrow(), ArticleDTO.class);
        model.addAttribute("article", article);

        return "articles/article";
    }

    @GetMapping("/new")
    public String createNewArticleForm(@ModelAttribute(binding = false) ArticleDTO articleDTO) {
        return "articles/newArticle";
    }

    @PostMapping("/new")
    public String createNewArticle(@Valid ArticleDTO articleDTO, BindingResult bindingResult) {
        log.debug("article: {}", articleDTO);
        return "articles/newArticle";
    }

    @PostMapping(value = "/upload-image", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Map<String, String>> uploadImage(@RequestPart("image") MultipartFile image) {
        log.debug("Image name: {}", image.getOriginalFilename());
        var uri = MvcUriComponentsBuilder.fromMethodName(ArticleController.class, "downloadImage", "test")
            .encode()
            .build()
            .getPath();
        return Map.of("data", Map.of("filePath", uri));
    }

    @GetMapping(value = "/download-image/{id}", produces = "image/*")
    @ResponseBody
    public byte[] downloadImage(@PathVariable String id) {
        return new byte[0];
    }
}
