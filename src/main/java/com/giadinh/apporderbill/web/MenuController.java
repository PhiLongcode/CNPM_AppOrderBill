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
import com.giadinh.apporderbill.web.security.ApiAuthorizationService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho Menu feature.
 */
@RestController
@RequestMapping("/api/v1/menu")
@Tag(name = "Menu", description = "Manage menu items")
public class MenuController {

    private final GetAllMenuItemsUseCase getAllMenuItemsUseCase;
    private final GetActiveMenuItemsUseCase getActiveMenuItemsUseCase;
    private final CreateMenuItemUseCase createMenuItemUseCase;
    private final com.giadinh.apporderbill.catalog.usecase.UpdateMenuItemUseCase updateMenuItemUseCase;
    private final DeleteMenuItemUseCase deleteMenuItemUseCase;
    private final ImportMenuFromExcelUseCase importMenuFromExcelUseCase;
    private final ApiAuthorizationService authorizationService;

    public MenuController(MenuItemRepository menuItemRepository, ApiAuthorizationService authorizationService) {
        this.getAllMenuItemsUseCase = new GetAllMenuItemsUseCase(menuItemRepository);
        this.getActiveMenuItemsUseCase = new GetActiveMenuItemsUseCase(menuItemRepository);
        this.createMenuItemUseCase = new CreateMenuItemUseCase(menuItemRepository);
        this.updateMenuItemUseCase = new com.giadinh.apporderbill.catalog.usecase.UpdateMenuItemUseCase(menuItemRepository);
        this.deleteMenuItemUseCase = new DeleteMenuItemUseCase(menuItemRepository);
        this.importMenuFromExcelUseCase = new ImportMenuFromExcelUseCase(menuItemRepository, new ExcelService());
        this.authorizationService = authorizationService;
    }

    @GetMapping
    public ResponseEntity<List<MenuItemOutput>> getAllMenuItems(@RequestHeader(value = "X-Username", required = false) String username) {
        authorizationService.requireView(username, "Manage Menu Items");
        return ResponseEntity.ok(getAllMenuItemsUseCase.execute());
    }

    @GetMapping("/active")
    public ResponseEntity<List<MenuItemOutput>> getActiveMenuItems(@RequestHeader(value = "X-Username", required = false) String username) {
        authorizationService.requireView(username, "Manage Menu Items");
        return ResponseEntity.ok(getActiveMenuItemsUseCase.execute());
    }

    @PostMapping
    public ResponseEntity<MenuItemOutput> createMenuItem(@RequestHeader(value = "X-Username", required = false) String username,
                                                         @RequestBody CreateMenuItemInput input) {
        authorizationService.requireOperate(username, "Manage Menu Items");
        return ResponseEntity.ok(createMenuItemUseCase.execute(input));
    }

    @PutMapping
    public ResponseEntity<MenuItemOutput> updateMenuItem(@RequestHeader(value = "X-Username", required = false) String username,
                                                         @RequestBody UpdateMenuItemInput input) {
        authorizationService.requireOperate(username, "Manage Menu Items");
        return ResponseEntity.ok(updateMenuItemUseCase.execute(input));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMenuItem(@RequestHeader(value = "X-Username", required = false) String username,
                                               @RequestBody DeleteMenuItemInput input) {
        authorizationService.requireOperate(username, "Manage Menu Items");
        deleteMenuItemUseCase.execute(input);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<ImportMenuFromExcelOutput> importMenuFromExcel(@RequestHeader(value = "X-Username", required = false) String username,
                                                                          @RequestBody ImportMenuFromExcelInput input) {
        authorizationService.requireOperate(username, "Manage Menu Items");
        return ResponseEntity.ok(importMenuFromExcelUseCase.execute(input));
    }
}
