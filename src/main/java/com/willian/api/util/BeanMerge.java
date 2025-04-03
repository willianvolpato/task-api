package com.willian.api.util;

import java.lang.reflect.Field;

public class BeanMerge {

    private BeanMerge() {
        // Prevent instantiation
    }

    public static <T> T mergeObjects(T original, T updates) {
        if (original == null || updates == null) return original;

        for (Field field : updates.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(updates);
                if (value != null) {
                    field.set(original, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error merging objects", e);
            }
        }
        return original;
    }
}
