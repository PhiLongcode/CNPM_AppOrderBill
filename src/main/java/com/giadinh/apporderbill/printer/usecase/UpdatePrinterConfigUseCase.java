package com.giadinh.apporderbill.printer.usecase;

import com.giadinh.apporderbill.printer.model.PrinterConfig;
import com.giadinh.apporderbill.printer.repository.PrinterConfigRepository;
import com.giadinh.apporderbill.printer.usecase.dto.PrinterConfigOutput;
import com.giadinh.apporderbill.printer.usecase.dto.UpdatePrinterConfigInput;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

import java.util.Locale;
import java.util.Set;

public class UpdatePrinterConfigUseCase {
    private final PrinterConfigRepository repository;

    public UpdatePrinterConfigUseCase(PrinterConfigRepository repository) {
        this.repository = repository;
    }

    public PrinterConfigOutput execute(UpdatePrinterConfigInput input) {
        validate(input);
        PrinterConfig saved = repository.save(new PrinterConfig(
                input.getPrinterName(),
                input.getConnectionType().trim().toUpperCase(Locale.ROOT),
                input.getPaperSize().trim().toLowerCase(Locale.ROOT),
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

    private void validate(UpdatePrinterConfigInput input) {
        if (input == null) {
            throw new DomainException(ErrorCode.PRINTER_CONFIG_INVALID);
        }
        if (input.getPrinterName() == null || input.getPrinterName().isBlank()) {
            throw new DomainException(ErrorCode.PRINTER_CONFIG_INVALID);
        }
        String connectionType = input.getConnectionType() == null ? "" : input.getConnectionType().trim().toUpperCase(Locale.ROOT);
        if (!Set.of("WINDOWS", "USB", "NETWORK").contains(connectionType)) {
            throw new DomainException(ErrorCode.PRINTER_CONFIG_INVALID);
        }
        String paperSize = input.getPaperSize() == null ? "" : input.getPaperSize().trim().toLowerCase(Locale.ROOT);
        if (!Set.of("58mm", "80mm").contains(paperSize)) {
            throw new DomainException(ErrorCode.PRINTER_CONFIG_INVALID);
        }
        if (input.getCopies() <= 0 || input.getCopies() > 10) {
            throw new DomainException(ErrorCode.PRINTER_CONFIG_INVALID);
        }
    }
}

