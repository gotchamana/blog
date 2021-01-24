package io.github.blog.module;

import java.io.IOException;
import java.util.*;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.Renderer;
import org.modelmapper.*;
import org.springframework.web.multipart.MultipartFile;

import io.github.blog.dto.ArticleDTO;
import io.github.blog.entity.Article;
import lombok.extern.log4j.Log4j2;

@Log4j2
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
        Converter<byte[], String> toBase64 = context ->
            Optional.ofNullable(context.getSource())
                .map(cover -> Base64.getEncoder().encodeToString(cover))
                .orElse(null);

        Converter<String, String> toHtml = context ->
            Optional.ofNullable(context.getSource())
                .map(markdown -> renderer.render(parser.parse(markdown)))
                .orElse(null);

		var typeMap = modelMapper.createTypeMap(Article.class, ArticleDTO.class);
        typeMap.addMappings(mapper -> mapper.using(toBase64).map(Article::getCoverPicture , ArticleDTO::setCoverPicture));
        typeMap.addMappings(mapper -> mapper.using(toHtml).map(Article::getContent , ArticleDTO::setContent));
	}

	private void configDTOToEntity(ModelMapper modelMapper) {
        Converter<MultipartFile, byte[]> toByteArray = context ->
            Optional.ofNullable(context.getSource())
                .map(multipart -> {
					try {
						return multipart.getBytes();
					} catch (IOException e) {
                        log.error("Mapping MultipartFile to byte[] failed", e);
                        return null;
					}
				})
                .orElse(null);

        var typeMap = modelMapper.createTypeMap(ArticleDTO.class, Article.class);
        typeMap.addMappings(mapper -> mapper.using(toByteArray).map(ArticleDTO::getCoverPicture, Article::setCoverPicture));
	}
}
