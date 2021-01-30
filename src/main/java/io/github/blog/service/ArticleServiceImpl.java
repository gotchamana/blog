package io.github.blog.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.blog.entity.*;
import io.github.blog.repository.*;
import io.github.blog.util.ImageCollector;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ImageRepository imageRepository;

	@Override
	public Optional<Article> findById(Long id) {
		return articleRepository.findById(id);
	}

	@Override
	public Article save(Article article) {
        article.setImages(getUsedImages(article));

        article = articleRepository.save(article);
        imageRepository.deleteOrthanImages();

		return article;
	}

    private List<Image> getUsedImages(Article article) {
        var imageCollector = new ImageCollector(article.getContent());
        return imageCollector.getImages();
    }
}