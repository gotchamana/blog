package io.github.blog.controller;

import static io.github.blog.config.ModelMapperConfiguration.TypeMapName.*;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.*;

import java.io.IOException;
import java.util.*;

import javax.validation.Valid;

import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.Renderer;
import org.commonmark.renderer.html.HtmlRenderer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import io.github.blog.dto.*;
import io.github.blog.entity.*;
import io.github.blog.service.*;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/articles")
@Log4j2
public class ArticleController {
    
    private static final Parser MARKDOWN_PARSER;
    private static final Renderer HTML_RENDERER;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ModelMapper modelMapper;

    static {
        var extensions = List.of(TablesExtension.create());
        MARKDOWN_PARSER = Parser.builder().extensions(extensions).build();
        HTML_RENDERER = HtmlRenderer.builder().extensions(extensions).escapeHtml(true).build();
    }

    @GetMapping("/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        var article = modelMapper.map(articleService.findById(id).orElseThrow(), ArticleDTO.class,
            ARTICLE_TO_DTO_WITH_HTML_RENDER.getName());
        model.addAttribute("article", article);

        return "articles/article";
    }

    @PostMapping(value = "/render-markdown", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String renderMarkdown(@RequestBody String markdown) {
        log.debug("Upload markdown: {}", markdown);
        return HTML_RENDERER.render(MARKDOWN_PARSER.parse(markdown));
    }

    @GetMapping("/new")
    public String createNewArticleForm(@ModelAttribute(binding = false) ArticleDTO articleDTO) {
        articleDTO.setAuthor("王小明");
        return "articles/new-article";
    }

    @PostMapping("/new")
    public String createNewArticle(@Valid ArticleDTO articleDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Article binding error: {}", bindingResult.getAllErrors());
            return "articles/new-article";
        }

        var article = articleService.save(modelMapper.map(articleDTO, Article.class));
        return "redirect:/articles/" + article.getId();
    }

    @GetMapping("/edit/{id}")
    public String editArticleForm(@PathVariable Long id, Model model) {
        var article = modelMapper.map(articleService.findById(id).orElseThrow(), ArticleDTO.class,
            ARTICLE_TO_DTO_WITH_NO_RENDER.getName());
        model.addAttribute("articleDTO", article);
        return "articles/edit-article";
    }

    @PostMapping("/edit/{id}")
    public String editArticle(@PathVariable Long id, @Valid ArticleDTO articleDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Article binding error: {}", bindingResult.getAllErrors());
            return "articles/edit-article";
        }

        var article = articleService.findById(id).orElseThrow();
        modelMapper.map(articleDTO, article);
        articleService.save(article);

        return "redirect:/articles/" + id;
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
        image.setContent(uploadImage.getBytes());
        image = imageService.save(image);

        var path = fromMethodCall(on(ArticleController.class).downloadImage(image.getId()))
            .encode()
            .build()
            .getPath();
        return ResponseEntity.ok(Map.of("data", Map.of("filePath", path.substring(1))));
    }

    @GetMapping(value = "/download-image/{id}", produces = "image/*")
    @ResponseBody
    public Object downloadImage(@PathVariable String id) {
        return imageService.findById(id)
            .map(Image::getContent)
            .orElseThrow();
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadImageSizeExceeded() {
        return ResponseEntity.badRequest().body(Map.of("error", "fileTooLarge"));
    }

    @GetMapping("/search")
    public String search(@RequestParam("q") String query, @PageableDefault Pageable pageable, Model model) {
        var articles = articleService.search(query, pageable)
            .map(article -> modelMapper.map(article, ArticleDTO.class, ARTICLE_TO_DTO_WITH_RAW_TEXT_RENDER.getName()));
        model.addAttribute("articles", articles);

        return "articles/search-result";
    }
}