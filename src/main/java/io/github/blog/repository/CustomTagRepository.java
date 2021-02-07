package io.github.blog.repository;

import java.util.*;

import io.github.blog.entity.Tag;

public interface CustomTagRepository {
    Set<Tag> saveAllIfNotExists(Collection<Tag> tags);
}