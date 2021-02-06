package io.github.blog.entity;

import javax.persistence.*;

import lombok.Data;

@Entity
@Data
public class Tag {
    
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String text;
}