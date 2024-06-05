package com.sergelyax.data;

import com.sergelyax.entity.MultiEntity;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Data {

    private List<MultiEntity> group;

    public Data(List<MultiEntity> group) {
        this.group = group;
    }

    public List<MultiEntity> getGroup() {
        return group;
    }

    public int size() {
        return group.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return group.equals(data.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        for (MultiEntity multiEntity : group) {
            joiner.add(multiEntity.toString());
        }
        return joiner.toString();
    }
}
