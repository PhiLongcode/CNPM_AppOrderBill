package com.giadinh.apporderbill.identity.model;

import java.util.Objects;

public class RoleGroup {
    private int id;
    private String name;
    private String description;

    public RoleGroup(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleGroup roleGroup = (RoleGroup) o;
        return id == roleGroup.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RoleGroup{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}
