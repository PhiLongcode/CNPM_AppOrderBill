package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.catalog.repository.MenuItemRepository;
import com.giadinh.apporderbill.catalog.service.ExcelService;
import com.giadinh.apporderbill.catalog.usecase.CreateMenuItemUseCase;
import com.giadinh.apporderbill.catalog.usecase.DeleteMenuItemUseCase;
import com.giadinh.apporderbill.catalog.usecase.GetActiveMenuItemsUseCase;
import com.giadinh.apporderbill.catalog.usecase.GetAllMenuItemsUseCase;
import com.giadinh.apporderbill.catalog.usecase.ImportMenuFromExcelUseCase;
import com.giadinh.apporderbill.catalog.usecase.dto.CreateMenuItemInput;
import com.giadinh.apporderbill.catalog.usecase.dto.DeleteMenuItemInput;
import com.giadinh.apporderbill.catalog.usecase.dto.ImportMenuFromExcelInput;
import com.giadinh.apporderbill.catalog.usecase.dto.ImportMenuFromExcelOutput;
import com.giadinh.apporderbill.catalog.usecase.dto.MenuItemOutput;
import com.giadinh.apporderbill.catalog.usecase.dto.UpdateMenuItemInput;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho Menu feature.
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final GetAllMenuItemsUseCase getAllMenuItemsUseCase;
    private final GetActiveMenuItemsUseCase getActiveMenuItemsUseCase;
    private final CreateMenuItemUseCase createMenuItemUseCase;
    private final com.giadinh.apporderbill.catalog.usecase.UpdateMenuItemUseCase updateMenuItemUseCase;
    private final DeleteMenuItemUseCase deleteMenuItemUseCase;
    private final ImportMenuFromExcelUseCase importMenuFromExcelUseCase;

    public MenuController(MenuItemRepository menuItemRepository) {
        this.getAllMenuItemsUseCase = new GetAllMenuItemsUseCase(menuItemRepository);
        this.getActiveMenuItemsUseCase = new GetActiveMenuItemsUseCase(menuItemRepository);
        this.createMenuItemUseCase = new CreateMenuItemUseCase(menuItemRepository);
        this.updateMenuItemUseCase = new com.giadinh.apporderbill.catalog.usecase.UpdateMenuItemUseCase(menuItemRepository);
        this.deleteMenuItemUseCase = new DeleteMenuItemUseCase(menuItemRepository);
        this.importMenuFromExcelUseCase = new ImportMenuFromExcelUseCase(menuItemRepository, new ExcelService());
    }

    @GetMapping
    public ResponseEntity<List<MenuItemOutput>> getAllMenuItems() {
        return ResponseEntity.ok(getAllMenuItemsUseCase.execute());
    }

    @GetMapping("/active")
    public ResponseEntity<List<MenuItemOutput>> getActiveMenuItems() {
        return ResponseEntity.ok(getActiveMenuItemsUseCase.execute());
    }

    @PostMapping
    public ResponseEntity<MenuItemOutput> createMenuItem(@RequestBody CreateMenuItemInput input) {
        return ResponseEntity.ok(createMenuItemUseCase.execute(input));
    }

    @PutMapping
    public ResponseEntity<MenuItemOutput> updateMenuItem(@RequestBody UpdateMenuItemInput input) {
        return ResponseEntity.ok(updateMenuItemUseCase.execute(input));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMenuItem(@RequestBody DeleteMenuItemInput input) {
        deleteMenuItemUseCase.execute(input);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<ImportMenuFromExcelOutput> importMenuFromExcel(@RequestBody ImportMenuFromExcelInput input) {
        return ResponseEntity.ok(importMenuFromExcelUseCase.execute(input));
    }
}
