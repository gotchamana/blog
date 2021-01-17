package io.github.blog.controller;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.blog.dto.ArticleDTO;
import io.github.blog.module.ArticleModule;
import io.github.blog.repository.ArticleRepository;

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

        var parser = Parser.builder().build();
        var renderer = TextContentRenderer.builder().build();
        modelMapper.registerModule(new ArticleModule(parser, renderer));
	}

	@GetMapping("/")
    public String index(Model model, @PageableDefault(sort = "createDate", direction=Direction.DESC) Pageable pageable) {
        var articles = articleRepository.findAll(pageable)
            .map(article -> modelMapper.map(article, ArticleDTO.class));

        model.addAttribute("articles", articles);
        return "index";
    }
}
