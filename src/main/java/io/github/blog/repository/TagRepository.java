package io.github.blog.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import io.github.blog.entity.Tag;

public interface TagRepository extends CrudRepository<Tag, Long> {
    
    @Query("SELECT t.text FROM Tag t")
    Set<String> findAllTagTexts();
}