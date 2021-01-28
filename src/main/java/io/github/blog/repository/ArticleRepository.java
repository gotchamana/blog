package io.github.blog.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import io.github.blog.entity.Article;

public interface ArticleRepository extends PagingAndSortingRepository<Article, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE image SET article_id = ?1 WHERE id IN ?2", nativeQuery = true)
    void attachArticleToImages(Long articleId, String... imageIds);
}
