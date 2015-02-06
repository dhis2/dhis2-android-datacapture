package org.hisp.dhis.mobile.datacapture.utils;

public class Utils {
    private Utils() { }

    public static <T> T isNull(T obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }

        return obj;
    }
}