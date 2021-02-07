package io.github.blog.repository;

import static java.util.stream.Collectors.toList;

import java.util.*;

import javax.persistence.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import io.github.blog.entity.Tag;

public class TagRepositoryImpl implements CustomTagRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TagRepository tagRepository;

    @Override
    @Transactional
    public Set<Tag> saveAllIfNotExists(Collection<Tag> tags) {
        Objects.requireNonNull(tags, "Tags must not be null");
        if (tags.isEmpty()) return Set.of();

        var tagTexts = tags.stream()
            .map(Tag::getText)
            .collect(toList());

        createInsertIgnoreNativeQuery(tagTexts).executeUpdate();

        return tagRepository.findAllByTagTexts(tagTexts);
    }

    private Query createInsertIgnoreNativeQuery(List<String> tagTexts) {
        var sql = createInsertIgnorePreparedSQL(tagTexts.size());
        var query = entityManager.createNativeQuery(sql);

        for (var i = 0; i < tagTexts.size(); i++)
            query.setParameter(i + 1, tagTexts.get(i));

        return query;
    }

    private String createInsertIgnorePreparedSQL(int parammeterCount) {
        var sql = new StringBuilder("INSERT IGNORE tag (text) VALUES ");
        for (var i = 1; i <= parammeterCount; i++)
            sql.append("(?")
                .append(i)
                .append(')')
                .append(i == parammeterCount ? ';' : ',');

        return sql.toString();
    }
}