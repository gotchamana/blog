package io.github.blog.service;

import java.util.Optional;

import io.github.blog.entity.Image;

public interface ImageService {
    Image save(Image image);
    Optional<Image> findById(String id);
}