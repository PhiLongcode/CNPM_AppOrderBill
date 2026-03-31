package com.giadinh.apporderbill.printer.usecase;

import com.giadinh.apporderbill.printer.repository.PrinterConfigRepository;
import com.giadinh.apporderbill.printer.usecase.dto.PrinterConfigOutput;

public class GetPrinterConfigUseCase {
    private final PrinterConfigRepository repository;

    public GetPrinterConfigUseCase(PrinterConfigRepository repository) {
        this.repository = repository;
    }

    public PrinterConfigOutput execute() {
        var c = repository.getCurrent();
        return new PrinterConfigOutput(
                c.getPrinterName(),
                c.getConnectionType(),
                c.getPaperSize(),
                c.getCopies(),
                c.isDefaultKitchen(),
                c.isDefaultReceipt());
    }
}

