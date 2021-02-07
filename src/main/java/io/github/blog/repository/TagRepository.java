package io.github.blog.repository;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import io.github.blog.entity.Tag;

public interface TagRepository extends CrudRepository<Tag, Long>, CustomTagRepository {
    
    @Query("SELECT t.text FROM Tag t")
    Set<String> findAllTagTexts();

    @Query("SELECT t FROM Tag t WHERE t.text IN ?1")
    Set<Tag> findAllByTagTexts(Collection<String> tagTexts);

    @Modifying
    @Transactional
    @Query(value =
        "DELETE FROM tag " +
        "WHERE NOT EXISTS (" +
            "SELECT * " +
            "FROM article_tag " +
            "WHERE id = tag_id" +
        ")", nativeQuery = true)
    void deleteOrthanTags();
}