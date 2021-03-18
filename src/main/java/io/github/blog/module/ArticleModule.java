package io.github.blog.module;

import static io.github.blog.config.ModelMapperConfiguration.TypeMapName.*;
import static java.util.stream.Collectors.toSet;

import java.util.*;
import java.util.function.Function;

import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.*;
import org.modelmapper.*;
import org.springframework.util.ReflectionUtils;

import io.github.blog.dto.ArticleDTO;
import io.github.blog.entity.*;

public class ArticleModule implements org.modelmapper.Module {

	@Override
	public void setupModule(ModelMapper modelMapper) {
        configEntityToDTO(modelMapper);
        configDTOToEntity(modelMapper);
	}

    private void configEntityToDTO(ModelMapper modelMapper) {
        var extensions = List.of(TablesExtension.create());
        var markdownParser = Parser.builder().extensions(extensions).build();
        var htmlRenderer = HtmlRenderer.builder().extensions(extensions).escapeHtml(true).build();
        var rawTextRenderer = TextContentRenderer.builder().nodeRendererFactory(URLNodeRenderer::new).build();

        Converter<String, String> toRawText = context ->
            Optional.ofNullable(context.getSource())
                .map(markdown -> rawTextRenderer.render(markdownParser.parse(markdown)))
                .orElse(null);

        Converter<String, String> toHtml = context ->
            Optional.ofNullable(context.getSource())
                .map(markdown -> htmlRenderer.render(markdownParser.parse(markdown)))
                .orElse(null);

        Converter<Set<Tag>, Set<String>> toTagTexts = context ->
            Optional.ofNullable(context.getSource())
                .map(tags -> tags.stream().map(Tag::getText).collect(toSet()))
                .orElse(Set.of());

        Function<Converter<String, String>, ExpressionMap<Article, ArticleDTO>> mapContent = converter ->
            mapper -> mapper.using(converter).map(Article::getContent , ArticleDTO::setContent);

        ExpressionMap<Article, ArticleDTO> mapTags = mapper ->
            mapper.using(toTagTexts).map(Article::getTags, ArticleDTO::setTags);

        var typeMap = modelMapper.createTypeMap(Article.class, ArticleDTO.class,
            ARTICLE_TO_DTO_WITH_RAW_TEXT_RENDER.getName());
        typeMap.addMappings(mapContent.apply(toRawText));
        typeMap.addMappings(mapTags);

        typeMap = modelMapper.createTypeMap(Article.class, ArticleDTO.class,
            ARTICLE_TO_DTO_WITH_HTML_RENDER.getName());
        typeMap.addMappings(mapContent.apply(toHtml));
        typeMap.addMappings(mapTags);

        typeMap = modelMapper.createTypeMap(Article.class, ArticleDTO.class, ARTICLE_TO_DTO_WITH_NO_RENDER.getName());
        typeMap.addMappings(mapTags);
    }

	private void configDTOToEntity(ModelMapper modelMapper) {
        Converter<Set<String>, Set<Tag>> toTags = context ->
            Optional.ofNullable(context.getSource())
                .map(tagTexts -> tagTexts.stream().map(Tag::new).collect(toSet()))
                .orElse(Set.of());

        var typeMap = modelMapper.createTypeMap(ArticleDTO.class, Article.class);
        typeMap.addMappings(mapper -> mapper.using(toTags).map(ArticleDTO::getTags, Article::setTags));
    }

    private static class URLNodeRenderer implements NodeRenderer {

		private TextContentNodeRendererContext context;

		public URLNodeRenderer(TextContentNodeRendererContext context) {
			this.context = context;
		}

		@Override
		public Set<Class<? extends Node>> getNodeTypes() {
		    return Set.of(org.commonmark.node.Image.class, Link.class);
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
}