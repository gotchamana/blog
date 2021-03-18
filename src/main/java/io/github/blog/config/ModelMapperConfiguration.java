package io.github.blog.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.*;

import io.github.blog.module.ArticleModule;
import lombok.Getter;

@Configuration
public class ModelMapperConfiguration {

    public enum TypeMapName {

        ARTICLE_TO_DTO_WITH_NO_RENDER("articleNoRender"),
        ARTICLE_TO_DTO_WITH_RAW_TEXT_RENDER("articleRawText"),
        ARTICLE_TO_DTO_WITH_HTML_RENDER("articleHtml");

        @Getter
        private String name;

        TypeMapName(String name) {
            this.name = name;
        }
    }

    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.registerModule(new ArticleModule());
        return modelMapper;
    }
}