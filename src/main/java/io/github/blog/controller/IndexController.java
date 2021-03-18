package io.github.blog.controller;

import static io.github.blog.config.ModelMapperConfiguration.TypeMapName.ARTICLE_TO_DTO_WITH_RAW_TEXT_RENDER;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.blog.dto.ArticleDTO;
import io.github.blog.repository.ArticleRepository;

@Controller
public class IndexController {

    @Autowired
    private ArticleRepository articleRepository;

	@Autowired
    private ModelMapper modelMapper;

    @GetMapping("/")
    public String index(Model model,
        @PageableDefault(sort = "createDate", direction = Direction.DESC) Pageable pageable) {

        var articles = articleRepository.findAll(pageable)
			.map(article -> modelMapper.map(article, ArticleDTO.class, ARTICLE_TO_DTO_WITH_RAW_TEXT_RENDER.getName()));

        model.addAttribute("articles", articles);
        return "index";
    }
}