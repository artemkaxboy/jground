package com.artemkaxboy.assertj;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ListUtils {

    /**
     * Reverse List
     */
    static <T> List<T> reverse(Collection<T> c) {
        List<T> result = new LinkedList<>();
        for (T o : c) {
            result.add(0, o);
        }
        return result;
    }
}
