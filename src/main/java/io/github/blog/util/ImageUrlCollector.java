package io.github.blog.util;

import static java.util.stream.Collectors.*;

import java.net.*;
import java.util.*;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ImageUrlCollector extends AbstractVisitor {

    private static final Parser MARKDOWN_PARSER = Parser.builder().build();

    private Set<URI> imageUrls = new LinkedHashSet<>();

    public ImageUrlCollector(String markdown) {
        var node = MARKDOWN_PARSER.parse(markdown);
        node.accept(this);
    }

    @Override
    public void visit(Image image) {
        log.debug("Found image: {}", image);

        try {
            imageUrls.add(new URI(image.getDestination()));
        } catch (URISyntaxException e) {
            log.warn("Invalid image uri syntax: {}", image.getDestination(), e);
        }
    }

    public List<URL> getImageUrls() {
        return imageUrls.stream()
            .map(uri -> {
                try {
                    return uri.toURL();
                } catch (IllegalArgumentException | MalformedURLException e) {
                    log.warn("Invalid image url: {}", uri.toString(), e);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(toUnmodifiableList());
    }
}