package com.marcfearby.utils;

public class Helper {


    /**
     * Try to parse a double value from the given string
     * @param str The string representation of a double-value
     * @param defaultValue The value to return if parsing fails
     * @return A valid double representation of 'str' or 'defaultValue'
     */
    public static Double getDoubleOrDefault(String str, Double defaultValue) {
        Double parsed = defaultValue;
        if (str != null && !str.isEmpty()) {
            try {
                parsed = Double.parseDouble(str);
            } catch (NumberFormatException ignored) { }
        }
        return parsed;
    }


    /**
     * Try to parse a double value from the given string
     * @param str The string representation of a double-value
     * @return A valid double representation of 'str' or 0
     */
    public static Double getDoubleOrZero(String str) {
        return getDoubleOrDefault(str, 0.0);
    }

}
