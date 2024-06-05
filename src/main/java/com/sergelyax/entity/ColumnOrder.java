package com.sergelyax.entity;

public enum ColumnOrder {
    First(0),
    Second(1),
    Third(2);

    private final int index;

    ColumnOrder(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
