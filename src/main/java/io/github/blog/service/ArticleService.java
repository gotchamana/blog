package io.github.blog.service;

import java.util.Optional;

import io.github.blog.entity.Article;

public interface ArticleService {
    Optional<Article> findById(Long id);
    Article save(Article article);
}
