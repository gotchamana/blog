package io.github.blog.controller;

import java.util.*;

import org.modelmapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import io.github.blog.dto.ArticleDTO;
import io.github.blog.entity.Article;
import io.github.blog.repository.ArticleRepository;

@Controller
@RequestMapping("/articles")
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

        Converter<byte[], String> toBase64 = context ->
            Optional.ofNullable(context.getSource())
                .map(cover -> Base64.getEncoder().encodeToString(cover))
                .orElse(null);

        var typeMap = modelMapper.createTypeMap(Article.class, ArticleDTO.class);
        typeMap.addMappings(mapper -> mapper.using(toBase64).map(Article::getCoverPicture , ArticleDTO::setCoverPicture));
	}

    @GetMapping("/{id}")
    public String getArticle(@PathVariable long id, Model model) {
        var article = modelMapper.map(articleRepository.findById(id).orElseThrow(), ArticleDTO.class);
        model.addAttribute("article", article);

        return "articles/article";
    }
}
