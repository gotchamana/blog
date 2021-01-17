package io.github.blog.entity;

import java.time.LocalDate;

import javax.persistence.*;

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

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] coverPicture;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDate createDate;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDate updateDate;
}
