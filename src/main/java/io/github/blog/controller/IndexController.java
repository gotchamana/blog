package io.github.blog.controller;

import java.util.*;
import java.util.stream.*;

import org.modelmapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.blog.dto.ArticleDTO;
import io.github.blog.entity.Article;
import io.github.blog.repository.ArticleRepository;
import io.github.blog.util.Iterables;

@Controller
public class IndexController {
    
    @Autowired
    private ArticleRepository articleRepository;

    private ModelMapper modelMapper;

    public IndexController() {
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

	@GetMapping("/")
    public String index(Model model) {
        var articles = Iterables.stream(articleRepository.findAll())
            .map(article -> modelMapper.map(article, ArticleDTO.class))
            .collect(Collectors.toList());

        model.addAttribute("articles", articles);
        return "index";
    }
}
