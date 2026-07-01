package com.michaelespinosa.expensetracker.categories.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Color {

    private static final Pattern HEX_COLOR_PATTERN =
            Pattern.compile("^#[0-9A-Fa-f]{6}$");

    private final String value;

    private Color(String value) {
        this.value = value;
    }

    public static Color of(String value) {
        Objects.requireNonNull(value, "Color cannot be null");

        if(!HEX_COLOR_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Color must have format #RRGGBB"
            );
        }

        return new Color(value.toUpperCase());
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Color color)) return false;
        return value.equals(color.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
