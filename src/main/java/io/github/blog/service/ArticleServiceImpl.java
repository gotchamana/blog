package io.github.blog.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.blog.entity.Article;
import io.github.blog.repository.ArticleRepository;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

	@Override
	public Optional<Article> findById(Long id) {
		return articleRepository.findById(id);
	}

	@Override
	public Article save(Article article) {
		return articleRepository.save(article);
	}
}
