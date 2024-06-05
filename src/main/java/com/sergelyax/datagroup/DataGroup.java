package com.sergelyax.datagroup;

import com.sergelyax.entity.MultiColumnEntity;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class DataGroup {

    private List<MultiColumnEntity> entities;

    public DataGroup(List<MultiColumnEntity> entities) {
        this.entities = entities;
    }

    public List<MultiColumnEntity> getEntities() {
        return entities;
    }

    public int getSize() {
        return entities.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataGroup dataGroup = (DataGroup) o;
        return entities.equals(dataGroup.entities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entities);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        for (MultiColumnEntity entity : entities) {
            joiner.add(entity.toString());
        }
        return joiner.toString();
    }
}
