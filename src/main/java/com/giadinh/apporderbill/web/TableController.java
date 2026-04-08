package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.table.TableComponent;
import com.giadinh.apporderbill.table.usecase.dto.*;
import com.giadinh.apporderbill.web.security.ApiAuthorizationService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho Table feature.
 */
@RestController
@RequestMapping("/api/v1/tables")
@Tag(name = "Tables", description = "Table management APIs")
public class TableController {

    private final TableComponent tableComponent;
    private final ApiAuthorizationService authorizationService;

    public TableController(TableComponent tableComponent, ApiAuthorizationService authorizationService) {
        this.tableComponent = tableComponent;
        this.authorizationService = authorizationService;
    }

    @GetMapping
    public ResponseEntity<List<TableOutput>> getAllTables(@RequestHeader(value = "X-Username", required = false) String username) {
        authorizationService.requireView(username, "Manage Tables");
        return ResponseEntity.ok(tableComponent.getAllTables());
    }

    @PostMapping
    public ResponseEntity<AddTableOutput> addTable(@RequestHeader(value = "X-Username", required = false) String username,
                                                   @RequestBody AddTableInput input) {
        authorizationService.requireOperate(username, "Manage Tables");
        return ResponseEntity.status(HttpStatus.CREATED).body(tableComponent.addTable(input));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTable(@RequestHeader(value = "X-Username", required = false) String username,
                                            @RequestBody DeleteTableInput input) {
        authorizationService.requireOperate(username, "Manage Tables");
        tableComponent.deleteTable(input);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/clear")
    public ResponseEntity<Void> clearTable(@RequestHeader(value = "X-Username", required = false) String username,
                                           @RequestBody ClearTableInput input) {
        authorizationService.requireOperate(username, "Manage Tables");
        tableComponent.clearTable(input);
        return ResponseEntity.noContent().build();
    }
}
