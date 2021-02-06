package io.github.blog.service;

import static java.util.stream.Collectors.toList;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import io.github.blog.entity.*;
import io.github.blog.repository.*;
import io.github.blog.util.ImageUrlCollector;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class ArticleServiceImpl implements ArticleService {

    @Value("${article.cover-picture.min-width}")
    private int coverPictureMinWidth;

    @Value("${article.cover-picture.min-height}")
    private int coverPictureMinHeight;

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
        var imageUrls = new ImageUrlCollector(article.getContent()).getImageUrls();

        article.setImages(getUsedUploadImages(imageUrls));
        article = articleRepository.save(article);
        setArticleCoverPictureUrl(article, imageUrls);

        imageRepository.deleteOrthanImages();

        return article;
    }

    private void setArticleCoverPictureUrl(Article article, List<URL> imageUrls) {
        CompletableFuture.runAsync(() -> 
            imageUrls.stream()
                .dropWhile(url -> getImage(url)
                    .filter(image ->
                        image.getWidth() >= coverPictureMinWidth && image.getHeight() >= coverPictureMinHeight)
                    .isEmpty()
                )
                .findFirst()
                .ifPresentOrElse(url -> {
                    log.debug("Article with id {} and title {} sets {} as cover picture url", article.getId(),
                        article.getTitle(), url);
                    article.setCoverPictureUrl(url.toString());
                    articleRepository.save(article);
                }, () -> {
                    log.debug("Article with id {} and title {} has no cover picture", article.getId(),
                        article.getTitle());
                    article.setCoverPictureUrl("");
                    articleRepository.save(article);
                })
        ).exceptionally(e -> {
            log.warn("Setting cover picture url for article: {} failed", article, e);
            return null;
        });
    }

    private Optional<BufferedImage> getImage(URL url) {
        try {
            return Optional.ofNullable(ImageIO.read(url));
        } catch (IOException e) {
            log.warn("Can't read image from url: {}", url, e);
            return Optional.empty();
        }
    }

    private List<Image> getUsedUploadImages(List<URL> imageUrls) {
        var serverUrl = toURL(fromCurrentContextPath().build().toUriString());
        return imageUrls.stream()
            .filter(imageUrl -> isUploadImage(imageUrl, serverUrl))
            .map(this::toImage)
            .collect(toList());
    }

    private URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed server url: " + url, e);
        }
    }

    private boolean isUploadImage(URL imageUrl, URL serverUrl) {
        if (imageUrl == null) return false;

        var isSameHost = imageUrl.getHost().equals(serverUrl.getHost());
        var imageUrlPort = imageUrl.getPort() == -1 ? imageUrl.getDefaultPort() : imageUrl.getPort();
        var serverUrlPort = serverUrl.getPort() == -1 ? serverUrl.getDefaultPort() : serverUrl.getPort();
        var isSamePort = imageUrlPort == serverUrlPort;

        return isSameHost && isSamePort;
    }

    private Image toImage(URL imageUrl) {
        var urlPath = imageUrl.getPath();
        var imageId = urlPath.substring(urlPath.lastIndexOf('/') + 1);

        var image = new Image();
        image.setId(imageId);

        return image;
    }
}