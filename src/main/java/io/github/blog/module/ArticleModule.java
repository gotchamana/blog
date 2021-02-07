package io.github.blog.module;

import static java.util.stream.Collectors.toSet;

import java.util.*;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.Renderer;
import org.modelmapper.*;

import io.github.blog.dto.ArticleDTO;
import io.github.blog.entity.*;

public class ArticleModule implements org.modelmapper.Module {

    private Parser parser;
    private Renderer renderer;

    public ArticleModule(Parser parser, Renderer renderer) {
        this.parser = parser;
        this.renderer = renderer;
    }

	@Override
	public void setupModule(ModelMapper modelMapper) {
        configEntityToDTO(modelMapper);
        configDTOToEntity(modelMapper);
	}

    private void configEntityToDTO(ModelMapper modelMapper) {
        Converter<String, String> toHtml = context ->
            Optional.ofNullable(context.getSource())
                .map(markdown -> renderer.render(parser.parse(markdown)))
                .orElse(null);

        Converter<Set<Tag>, Set<String>> toTagTexts = context ->
            Optional.ofNullable(context.getSource())
                .map(tags -> tags.stream().map(Tag::getText).collect(toSet()))
                .orElse(Set.of());

        var typeMap = modelMapper.createTypeMap(Article.class, ArticleDTO.class);
        typeMap.addMappings(mapper ->
            mapper.using(toHtml).map(Article::getContent , ArticleDTO::setContent));
        typeMap.addMappings(mapper -> mapper.using(toTagTexts).map(Article::getTags, ArticleDTO::setTags));
    }

	private void configDTOToEntity(ModelMapper modelMapper) {
        Converter<Set<String>, Set<Tag>> toTags = context ->
            Optional.ofNullable(context.getSource())
                .map(tagTexts -> tagTexts.stream().map(Tag::new).collect(toSet()))
                .orElse(Set.of());

        var typeMap = modelMapper.createTypeMap(ArticleDTO.class, Article.class);
        typeMap.addMappings(mapper -> mapper.using(toTags).map(ArticleDTO::getTags, Article::setTags));
    }
}