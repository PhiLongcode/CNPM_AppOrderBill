package com.giadinh.apporderbill.system;

import com.giadinh.apporderbill.system.usecase.CheckStorageUsageUseCase;
import com.giadinh.apporderbill.system.usecase.dto.StorageUsageOutput;

public class SystemComponentImpl implements SystemComponent {
    private final CheckStorageUsageUseCase checkStorageUsageUseCase;

    public SystemComponentImpl(Object connectionProvider) {
        this.checkStorageUsageUseCase = new CheckStorageUsageUseCase(connectionProvider);
    }

    @Override
    public StorageUsageOutput checkStorageUsage() {
        return new StorageUsageOutput(checkStorageUsageUseCase.execute());
    }
}

