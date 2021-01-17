package io.github.blog.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import io.github.blog.entity.Article;

public interface ArticleRepository extends PagingAndSortingRepository<Article, Long> {
    
}
