package io.github.blog.entity;

import java.time.LocalDateTime;
import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;

import org.hibernate.annotations.*;

import lombok.Data;

@Data
@Entity
public class Article {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob
    private String content;

    @Column(nullable = false, length = 2048)
    private String coverPictureUrl = "";

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createDate;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updateDate;

    @ManyToMany
    @JoinTable(name = "article_image", inverseJoinColumns = @JoinColumn(name = "image_id"))
    private List<Image> images;

    @ManyToMany
    @JoinTable(name = "article_tag", inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;
}