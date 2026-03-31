package com.giadinh.apporderbill.system;

import com.giadinh.apporderbill.system.usecase.dto.StorageUsageOutput;

public interface SystemComponent {
    StorageUsageOutput checkStorageUsage();
}

