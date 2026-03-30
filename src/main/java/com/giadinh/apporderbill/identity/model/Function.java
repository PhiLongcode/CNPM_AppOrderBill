package com.giadinh.apporderbill.identity.model;

import java.util.Objects;

public class Function {
    private int id;
    private String name;
    private int moduleId;

    public Function(int id, String name, int moduleId) {
        this.id = id;
        this.name = name;
        this.moduleId = moduleId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Function function = (Function) o;
        return id == function.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Function{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", moduleId=" + moduleId +
               '}';
    }
}
