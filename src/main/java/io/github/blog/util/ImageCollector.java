package io.github.blog.util;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.*;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.parser.Parser;

import io.github.blog.entity.Image;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ImageCollector extends AbstractVisitor {

    private static final Parser MARKDOWN_PARSER = Parser.builder().build();

    private Set<String> imageIds = new LinkedHashSet<>();

    public ImageCollector(String markdown) {
        var node = MARKDOWN_PARSER.parse(markdown);
        node.accept(this);
    }

    @Override
    public void visit(org.commonmark.node.Image image) {
        log.debug("Found image: {}", image);

        var url = image.getDestination();
        var imageId = url.substring(url.lastIndexOf('/') + 1);
        imageIds.add(imageId);
    }

    public List<Image> getImages() {
        return imageIds.stream()
            .map(this::toImage)
            .collect(toUnmodifiableList());
    }

    private Image toImage(String id) {
        var image = new Image();
        image.setId(id);
        return image;
    }
}