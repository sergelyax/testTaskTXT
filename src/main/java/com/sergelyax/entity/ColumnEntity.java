package com.sergelyax.entity;

import java.util.Objects;

public class ColumnEntity {

    private ColumnOrder columnOrder;
    private String columnNumber;
    private boolean isLegit;

    public ColumnEntity(ColumnOrder columnOrder, String columnNumber) {
        this.columnOrder = columnOrder;
        this.columnNumber = columnNumber;
        this.isLegit = !columnNumber.isEmpty();
    }

    public ColumnOrder getColumnOrder() {
        return columnOrder;
    }

    public String getColumnNumber() {
        return columnNumber;
    }

    public boolean isLegit() {
        return isLegit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnEntity that = (ColumnEntity) o;
        return columnOrder == that.columnOrder && columnNumber.equals(that.columnNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnOrder, columnNumber);
    }

    @Override
    public String toString() {
        return "\"" + columnNumber + "\"";
    }
}
