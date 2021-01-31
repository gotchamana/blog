package io.github.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.thymeleaf.dialect.springdata.SpringDataDialect;

@SpringBootApplication
@EnableJpaRepositories
@PropertySource("classpath:config.properties")
public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }

    @Bean
    public SpringDataDialect springDataDialect() {
        return new SpringDataDialect();
    }
}