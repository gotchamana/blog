package io.github.blog.service;

import java.util.Optional;

import org.springframework.data.domain.*;

import io.github.blog.entity.Article;

public interface ArticleService {
    Optional<Article> findById(Long id);
    Page<Article> search(String query, Pageable pageable);
    Article save(Article article);
}