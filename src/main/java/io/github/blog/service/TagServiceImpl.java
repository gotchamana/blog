package io.github.blog.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.blog.repository.TagRepository;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public Set<String> findAllTagTexts() {
        return tagRepository.findAllTagTexts();
    }
}