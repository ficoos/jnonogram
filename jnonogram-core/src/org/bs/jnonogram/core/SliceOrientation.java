package org.bs.jnonogram.core;

public enum SliceOrientation {
    Row("Row"),
    Column("Column");

    private final String name;

    private SliceOrientation(String s) {
        name = s;
    }

    @Override
    public String toString() {
        return name;
    }
}
