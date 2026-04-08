package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.system.SystemComponent;
import com.giadinh.apporderbill.system.usecase.dto.StorageUsageOutput;
import com.giadinh.apporderbill.web.security.ApiAuthorizationService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cho System feature.
 */
@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "System", description = "System level APIs")
public class SystemController {

    private final SystemComponent systemComponent;
    private final ApiAuthorizationService authorizationService;

    public SystemController(SystemComponent systemComponent, ApiAuthorizationService authorizationService) {
        this.systemComponent = systemComponent;
        this.authorizationService = authorizationService;
    }

    @GetMapping("/storage")
    public ResponseEntity<StorageUsageOutput> checkStorageUsage(@RequestHeader(value = "X-Username", required = false) String username) {
        authorizationService.requireView(username, "Manage Users");
        return ResponseEntity.ok(systemComponent.checkStorageUsage());
    }
}
