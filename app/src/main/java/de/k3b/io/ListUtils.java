/*
 * Copyright (c) 2017 by k3b.
 */
package de.k3b.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Helper to handle List-s
 * Created by k3b on 07.01.2017.
 */

public class ListUtils {
    private static final String DEFAULT_LIST_ELEMENT_DELIMITER = ",";

    @NonNull
    public static List<String> fromString(String stringWithElements) {
        return fromString(stringWithElements, DEFAULT_LIST_ELEMENT_DELIMITER);
    }

    @NonNull
    public static List<String> fromString(@NonNull String stringWithElements, String elementDelimiter) {
        return Arrays.asList(stringWithElements.split(elementDelimiter));
    }

    @NonNull
    public static String toString(List<?> list) {
        return toString(list, DEFAULT_LIST_ELEMENT_DELIMITER);
    }

    @NonNull
    public static List<String> toStringList(@NonNull Iterable<?> list) {
        ArrayList<String> result = new ArrayList<>();
        for (Object item : list) {
            if (item != null) result.add(item.toString());
        }
        return result;
    }

    @NonNull
    public static List<String> toStringList(@NonNull Object... list) {
        ArrayList<String> result = new ArrayList<>();
        for (Object item : list) {
            if (item != null) result.add(item.toString());
        }
        return result;
    }

    @NonNull
    public static String toString(List<?> list, String elementDelimiter) {
        StringBuilder result = new StringBuilder();
        if (list != null) {
            String nextDelim = "";
            for (Object instance : list) {
                if (instance != null) {
                    String instanceString = instance.toString().trim();
                    if (instanceString.length() > 0) {
                        result.append(nextDelim).append(instanceString);
                        nextDelim = elementDelimiter;
                    }
                }
            }
        }
        return result.toString();
    }

    public static String[] asStringArray(List<String> tags) {
        if ((tags == null) || (tags.size() == 0)) return null;
        return tags.toArray(new String[0]);
    }

    /**
     * return null if list has no elements
     */
    public static <T> List<T> emptyAsNull(List<T> list) {
        if ((list != null) && (list.size() > 0)) return list;
        return null;
    }
}