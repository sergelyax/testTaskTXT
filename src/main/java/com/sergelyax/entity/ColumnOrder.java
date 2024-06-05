package com.sergelyax.entity;

public enum ColumnOrder {
    First(0),
    Second(1),
    Third(2);

    private int value;

    ColumnOrder(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
