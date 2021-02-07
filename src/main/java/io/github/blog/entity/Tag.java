package io.github.blog.entity;

import javax.persistence.*;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
public class Tag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String text;

    public Tag(String text) {
        this.text = text;
    }
}