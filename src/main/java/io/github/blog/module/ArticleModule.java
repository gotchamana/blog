package io.github.blog.module;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.*;

import java.util.Optional;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.Renderer;
import org.modelmapper.*;

import io.github.blog.controller.ArticleController;
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
        Converter<byte[], String> toCoverPictureUrl = context ->
            Optional.ofNullable(context.getSource())
                .filter(cover -> cover.length != 0)
                .map(cover -> {
                    var article = (Article) context.getParent().getSource();
                    return fromMethodCall(on(ArticleController.class).downloadCoverPicture(article.getId()))
                        .encode()
                        .build()
                        .toUriString();
                })
                .orElse(null);

        Converter<String, String> toHtml = context ->
            Optional.ofNullable(context.getSource())
                .map(markdown -> renderer.render(parser.parse(markdown)))
                .orElse(null);

		var typeMap = modelMapper.createTypeMap(Article.class, ArticleDTO.class);
        typeMap.addMappings(mapper -> mapper.using(toCoverPictureUrl).map(Article::getCoverPicture, ArticleDTO::setCoverPictureUrl));
        typeMap.addMappings(mapper -> mapper.using(toHtml).map(Article::getContent , ArticleDTO::setContent));
	}

	private void configDTOToEntity(ModelMapper modelMapper) {
	}
}
