package com.giadinh.apporderbill.printer.usecase;

import com.giadinh.apporderbill.printer.model.PrinterConfig;
import com.giadinh.apporderbill.printer.repository.PrinterConfigRepository;
import com.giadinh.apporderbill.printer.usecase.dto.PrinterConfigOutput;
import com.giadinh.apporderbill.printer.usecase.dto.UpdatePrinterConfigInput;

public class UpdatePrinterConfigUseCase {
    private final PrinterConfigRepository repository;

    public UpdatePrinterConfigUseCase(PrinterConfigRepository repository) {
        this.repository = repository;
    }

    public PrinterConfigOutput execute(UpdatePrinterConfigInput input) {
        PrinterConfig saved = repository.save(new PrinterConfig(
                input.getPrinterName(),
                input.getConnectionType(),
                input.getPaperSize(),
                input.getCopies(),
                input.isDefaultKitchen(),
                input.isDefaultReceipt()));
        return new PrinterConfigOutput(
                saved.getPrinterName(),
                saved.getConnectionType(),
                saved.getPaperSize(),
                saved.getCopies(),
                saved.isDefaultKitchen(),
                saved.isDefaultReceipt());
    }
}

