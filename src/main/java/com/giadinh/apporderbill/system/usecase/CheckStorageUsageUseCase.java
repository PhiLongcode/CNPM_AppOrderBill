package com.giadinh.apporderbill.system.usecase;

import java.io.File;

public class CheckStorageUsageUseCase {
    private final Object connectionProvider;

    public CheckStorageUsageUseCase(Object connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public String execute() {
        boolean hasProvider = connectionProvider != null;
        File root = new File(".");
        long total = root.getTotalSpace();
        long free = root.getFreeSpace();
        long used = total - free;
        double usedGb = used / 1024.0 / 1024.0 / 1024.0;
        double freeGb = free / 1024.0 / 1024.0 / 1024.0;
        return String.format("Dung luong da dung: %.2f GB%nDung luong trong: %.2f GB%nNguon du lieu: %s",
                usedGb, freeGb, hasProvider ? "OK" : "N/A");
    }
}

