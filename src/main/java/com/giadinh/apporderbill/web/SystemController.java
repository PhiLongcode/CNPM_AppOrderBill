package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.system.SystemComponent;
import com.giadinh.apporderbill.system.usecase.dto.StorageUsageOutput;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cho System feature.
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

    private final SystemComponent systemComponent;

    public SystemController(SystemComponent systemComponent) {
        this.systemComponent = systemComponent;
    }

    @GetMapping("/storage")
    public ResponseEntity<StorageUsageOutput> checkStorageUsage() {
        return ResponseEntity.ok(systemComponent.checkStorageUsage());
    }
}
