package io.github.blog.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import io.github.blog.entity.Image;

public interface ImageRepository extends CrudRepository<Image, String> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM image WHERE article_id IS NULL", nativeQuery = true)
    void deleteOrthanImages();
}
