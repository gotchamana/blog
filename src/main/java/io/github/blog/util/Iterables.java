package io.github.blog.util;

import java.util.stream.*;

public final class Iterables {
    
    private Iterables() {}

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
