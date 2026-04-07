package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.table.TableComponent;
import com.giadinh.apporderbill.table.usecase.dto.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho Table feature.
 */
@RestController
@RequestMapping("/api/tables")
public class TableController {

    private final TableComponent tableComponent;

    public TableController(TableComponent tableComponent) {
        this.tableComponent = tableComponent;
    }

    @GetMapping
    public ResponseEntity<List<TableOutput>> getAllTables() {
        return ResponseEntity.ok(tableComponent.getAllTables());
    }

    @PostMapping
    public ResponseEntity<AddTableOutput> addTable(@RequestBody AddTableInput input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableComponent.addTable(input));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTable(@RequestBody DeleteTableInput input) {
        tableComponent.deleteTable(input);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/clear")
    public ResponseEntity<Void> clearTable(@RequestBody ClearTableInput input) {
        tableComponent.clearTable(input);
        return ResponseEntity.noContent().build();
    }
}
