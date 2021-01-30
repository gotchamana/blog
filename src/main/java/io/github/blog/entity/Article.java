package io.github.blog.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.*;

import io.github.blog.util.ImageCollector;
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
    private Image coverPicture;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createDate;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updateDate;

    @ManyToMany
    @JoinTable(name = "article_image", inverseJoinColumns = @JoinColumn(name = "image_id"))
    private List<Image> images;

    @PostLoad
    public void loadCoverPicture() {
        if (!images.isEmpty())
            coverPicture = new ImageCollector(content).getImages().get(0);
    }
}
