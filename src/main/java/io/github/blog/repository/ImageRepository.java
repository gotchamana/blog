package io.github.blog.repository;

import org.springframework.data.repository.CrudRepository;

import io.github.blog.entity.Image;

public interface ImageRepository extends CrudRepository<Image, String> {
}
