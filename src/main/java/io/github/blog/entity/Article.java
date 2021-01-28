package io.github.blog.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;
import javax.persistence.CascadeType;

import org.hibernate.annotations.*;

import lombok.Data;

@Data
@javax.persistence.Entity
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

    @Transient
    private byte[] coverPicture;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createDate;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updateDate;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "article_id")
    private List<Image> images;

    @PostLoad
    public void loadCoverPicture() {
        if (!images.isEmpty())
            coverPicture = images.get(0).getImage();
    }
}
