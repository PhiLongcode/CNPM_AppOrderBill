package com.giadinh.apporderbill.printer;

import com.giadinh.apporderbill.printer.repository.PrintTemplateRepository;
import com.giadinh.apporderbill.printer.repository.PrinterConfigRepository;
import com.giadinh.apporderbill.printer.usecase.GetPrintTemplateUseCase;
import com.giadinh.apporderbill.printer.usecase.GetPrinterConfigUseCase;
import com.giadinh.apporderbill.printer.usecase.UpdatePrintTemplateUseCase;
import com.giadinh.apporderbill.printer.usecase.UpdatePrinterConfigUseCase;
import com.giadinh.apporderbill.printer.usecase.dto.PrintTemplateOutput;
import com.giadinh.apporderbill.printer.usecase.dto.PrinterConfigOutput;
import com.giadinh.apporderbill.printer.usecase.dto.UpdatePrintTemplateInput;
import com.giadinh.apporderbill.printer.usecase.dto.UpdatePrinterConfigInput;

import java.util.List;
import java.util.Optional;

public class PrinterComponentImpl implements PrinterComponent {
    private final GetPrinterConfigUseCase getPrinterConfigUseCase;
    private final UpdatePrinterConfigUseCase updatePrinterConfigUseCase;
    private final GetPrintTemplateUseCase getPrintTemplateUseCase;
    private final UpdatePrintTemplateUseCase updatePrintTemplateUseCase;

    public PrinterComponentImpl(PrinterConfigRepository printerConfigRepository,
            PrintTemplateRepository printTemplateRepository) {
        this.getPrinterConfigUseCase = new GetPrinterConfigUseCase(printerConfigRepository);
        this.updatePrinterConfigUseCase = new UpdatePrinterConfigUseCase(printerConfigRepository);
        this.getPrintTemplateUseCase = new GetPrintTemplateUseCase(printTemplateRepository);
        this.updatePrintTemplateUseCase = new UpdatePrintTemplateUseCase(printTemplateRepository);
    }

    @Override
    public List<PrinterConfigOutput> getAllPrinterConfigs() {
        return List.of(getPrinterConfigUseCase.execute());
    }

    @Override
    public PrinterConfigOutput updatePrinterConfig(UpdatePrinterConfigInput input) {
        return updatePrinterConfigUseCase.execute(input);
    }

    @Override
    public Optional<PrintTemplateOutput> getPrintTemplate(String type) {
        return Optional.ofNullable(getPrintTemplateUseCase.execute(type));
    }

    @Override
    public PrintTemplateOutput updatePrintTemplate(UpdatePrintTemplateInput input) {
        return updatePrintTemplateUseCase.execute(input);
    }
}

