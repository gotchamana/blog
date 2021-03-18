package io.github.blog.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import io.github.blog.entity.Article;

public interface ArticleRepository extends PagingAndSortingRepository<Article, Long>, QueryByExampleExecutor<Article> {
}