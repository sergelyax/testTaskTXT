package com.sergelyax.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class MultiColumnEntity {

    private List<ColumnEntity> columnEntities = new ArrayList<>();

    public MultiColumnEntity(String... columnValues) {
        for (ColumnOrder columnOrder : ColumnOrder.values()) {
            columnEntities.add(new ColumnEntity(columnOrder, columnValues[columnOrder.getIndex()]));
        }
    }

    public ColumnEntity getColumnEntity(ColumnOrder columnOrder) {
        return columnEntities.get(columnOrder.getIndex());
    }

    public List<ColumnEntity> getLegitColumnEntities() {
        return columnEntities.stream().filter(ColumnEntity::isLegit).collect(Collectors.toList());
    }

    public boolean isLegit() {
        return columnEntities.stream().anyMatch(ColumnEntity::isLegit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiColumnEntity that = (MultiColumnEntity) o;
        return columnEntities.equals(that.columnEntities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnEntities);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(";");
        for (ColumnEntity columnEntity : columnEntities) {
            joiner.add(columnEntity.toString());
        }
        return joiner.toString();
    }
}
