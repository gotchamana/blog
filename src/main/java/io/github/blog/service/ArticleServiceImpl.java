package io.github.blog.service;

import java.util.*;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.blog.entity.Article;
import io.github.blog.repository.*;

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
        article = articleRepository.save(article);
        articleRepository.attachArticleToImages(article.getId(), getImageIds(article));
        imageRepository.deleteOrthanImages();

		return article;
	}

    private String[] getImageIds(Article article) {
        var parser = Parser.builder().build();
        var node = parser.parse(article.getContent());
        var imageIds = new ArrayList<String>();
        node.accept(new AbstractVisitor() {
            @Override
            public void visit(org.commonmark.node.Image image) {
                var url = image.getDestination();
                var imageId = url.substring(url.lastIndexOf('/') + 1);
                imageIds.add(imageId);

                visitChildren(image);
            }
        });

        return imageIds.toArray(new String[0]);
    }
}
