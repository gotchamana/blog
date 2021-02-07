package io.github.blog.controller;

import java.util.Set;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.text.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.blog.dto.ArticleDTO;
import io.github.blog.module.ArticleModule;
import io.github.blog.repository.ArticleRepository;

@Controller
public class IndexController {

    private static class URLNodeRenderer implements NodeRenderer {

		private TextContentNodeRendererContext context;

		public URLNodeRenderer(TextContentNodeRendererContext context) {
			this.context = context;
		}

		@Override
		public Set<Class<? extends Node>> getNodeTypes() {
		    return Set.of(Image.class, Link.class);
		}

		@Override
        public void render(Node node) {
            if (node instanceof Link)
                context.getWriter().write(getLiteral(node));
        }

		private String getLiteral(Node node) {
		    var sb = new StringBuilder();
		    var getLiteral = ReflectionUtils.findMethod(node.getClass(), "getLiteral");

		    if (getLiteral != null)
		        sb.append(ReflectionUtils.invokeMethod(getLiteral, node));
		    else
		        for (var next = node.getFirstChild(); next != null; next = next.getNext())
		            sb.append(getLiteral(next));

		    return sb.toString();
		}
	}

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
        var renderer = TextContentRenderer.builder().nodeRendererFactory(URLNodeRenderer::new).build();
        modelMapper.registerModule(new ArticleModule(parser, renderer));
	}

	@GetMapping("/")
    public String index(Model model,
        @PageableDefault(sort = "createDate", direction = Direction.DESC) Pageable pageable) {

        var articles = articleRepository.findAll(pageable).map(article -> modelMapper.map(article, ArticleDTO.class));

        model.addAttribute("articles", articles);
        return "index";
    }
}