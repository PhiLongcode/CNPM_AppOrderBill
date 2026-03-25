package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.menu.MenuComponent;
import com.giadinh.apporderbill.menu.usecase.dto.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho Menu feature.
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuComponent menuComponent;

    public MenuController(MenuComponent menuComponent) {
        this.menuComponent = menuComponent;
    }

    @GetMapping
    public ResponseEntity<List<MenuItemOutput>> getAllMenuItems() {
        return ResponseEntity.ok(menuComponent.getAllMenuItems());
    }

    @GetMapping("/active")
    public ResponseEntity<List<MenuItemOutput>> getActiveMenuItems() {
        return ResponseEntity.ok(menuComponent.getActiveMenuItems());
    }

    @PostMapping
    public ResponseEntity<MenuItemOutput> createMenuItem(@RequestBody CreateMenuItemInput input) {
        return ResponseEntity.ok(menuComponent.createMenuItem(input));
    }

    @PutMapping
    public ResponseEntity<MenuItemOutput> updateMenuItem(@RequestBody UpdateMenuItemInput input) {
        return ResponseEntity.ok(menuComponent.updateMenuItem(input));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMenuItem(@RequestBody DeleteMenuItemInput input) {
        menuComponent.deleteMenuItem(input);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<ImportMenuFromExcelOutput> importMenuFromExcel(@RequestBody ImportMenuFromExcelInput input) {
        return ResponseEntity.ok(menuComponent.importMenuFromExcel(input));
    }
}
