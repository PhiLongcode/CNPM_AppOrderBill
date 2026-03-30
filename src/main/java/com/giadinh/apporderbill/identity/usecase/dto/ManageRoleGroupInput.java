package com.giadinh.apporderbill.identity.usecase.dto;

import java.util.Set;

public class ManageRoleGroupInput {
    private String name;
    private String description;
    private Set<Integer> functionIds; // IDs của các chức năng được gán quyền

    public ManageRoleGroupInput(String name, String description, Set<Integer> functionIds) {
        this.name = name;
        this.description = description;
        this.functionIds = functionIds;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<Integer> getFunctionIds() {
        return functionIds;
    }
}
