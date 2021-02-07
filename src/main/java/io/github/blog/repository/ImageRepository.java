package io.github.blog.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import io.github.blog.entity.Image;

public interface ImageRepository extends CrudRepository<Image, String> {

    @Modifying
    @Transactional
    @Query(value =
        "DELETE FROM image " +
        "WHERE id IN (" +
            "(SELECT id FROM image) EXCEPT (SELECT image_id FROM article_image)" +
        ")", nativeQuery = true)
    void deleteOrthanImages();
}
