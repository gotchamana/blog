package io.github.blog.repository;

import org.springframework.data.repository.CrudRepository;

import io.github.blog.entity.Article;

public interface ArticleRepository extends CrudRepository<Article, Long> {
    
}
