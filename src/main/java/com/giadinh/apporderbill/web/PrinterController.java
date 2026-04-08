package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.printer.PrinterComponent;
import com.giadinh.apporderbill.printer.usecase.dto.*;
import com.giadinh.apporderbill.web.security.ApiAuthorizationService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho Printer feature.
 */
@RestController
@RequestMapping("/api/v1/printer")
@Tag(name = "Printer", description = "Printer configuration and template APIs")
public class PrinterController {

    private final PrinterComponent printerComponent;
    private final ApiAuthorizationService authorizationService;

    public PrinterController(PrinterComponent printerComponent, ApiAuthorizationService authorizationService) {
        this.printerComponent = printerComponent;
        this.authorizationService = authorizationService;
    }

    @GetMapping("/configs")
    public ResponseEntity<List<PrinterConfigOutput>> getAllPrinterConfigs(@RequestHeader(value = "X-Username", required = false) String username) {
        authorizationService.requireView(username, "Print Receipt");
        return ResponseEntity.ok(printerComponent.getAllPrinterConfigs());
    }

    @PutMapping("/configs")
    public ResponseEntity<PrinterConfigOutput> updatePrinterConfig(@RequestHeader(value = "X-Username", required = false) String username,
                                                                   @RequestBody UpdatePrinterConfigInput input) {
        authorizationService.requireOperate(username, "Print Receipt");
        return ResponseEntity.ok(printerComponent.updatePrinterConfig(input));
    }

    @GetMapping("/templates/{type}")
    public ResponseEntity<PrintTemplateOutput> getPrintTemplate(@RequestHeader(value = "X-Username", required = false) String username,
                                                                @PathVariable String type) {
        authorizationService.requireView(username, "Print Receipt");
        return printerComponent.getPrintTemplate(type)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/templates")
    public ResponseEntity<PrintTemplateOutput> updatePrintTemplate(@RequestHeader(value = "X-Username", required = false) String username,
                                                                   @RequestBody UpdatePrintTemplateInput input) {
        authorizationService.requireOperate(username, "Print Receipt");
        return ResponseEntity.ok(printerComponent.updatePrintTemplate(input));
    }
}
