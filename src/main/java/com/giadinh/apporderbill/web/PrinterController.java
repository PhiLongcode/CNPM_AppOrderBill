package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.printer.PrinterComponent;
import com.giadinh.apporderbill.printer.usecase.dto.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho Printer feature.
 */
@RestController
@RequestMapping("/api/printer")
public class PrinterController {

    private final PrinterComponent printerComponent;

    public PrinterController(PrinterComponent printerComponent) {
        this.printerComponent = printerComponent;
    }

    @GetMapping("/configs")
    public ResponseEntity<List<PrinterConfigOutput>> getAllPrinterConfigs() {
        return ResponseEntity.ok(printerComponent.getAllPrinterConfigs());
    }

    @PutMapping("/configs")
    public ResponseEntity<PrinterConfigOutput> updatePrinterConfig(@RequestBody UpdatePrinterConfigInput input) {
        return ResponseEntity.ok(printerComponent.updatePrinterConfig(input));
    }

    @GetMapping("/templates/{type}")
    public ResponseEntity<PrintTemplateOutput> getPrintTemplate(@PathVariable String type) {
        return printerComponent.getPrintTemplate(type)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/templates")
    public ResponseEntity<PrintTemplateOutput> updatePrintTemplate(@RequestBody UpdatePrintTemplateInput input) {
        return ResponseEntity.ok(printerComponent.updatePrintTemplate(input));
    }
}
