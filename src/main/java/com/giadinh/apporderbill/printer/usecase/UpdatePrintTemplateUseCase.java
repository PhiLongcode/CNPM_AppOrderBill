package com.giadinh.apporderbill.printer.usecase;

import com.giadinh.apporderbill.printer.model.PrintTemplate;
import com.giadinh.apporderbill.printer.repository.PrintTemplateRepository;
import com.giadinh.apporderbill.printer.usecase.dto.PrintTemplateOutput;
import com.giadinh.apporderbill.printer.usecase.dto.UpdatePrintTemplateInput;

public class UpdatePrintTemplateUseCase {
    private final PrintTemplateRepository repository;

    public UpdatePrintTemplateUseCase(PrintTemplateRepository repository) {
        this.repository = repository;
    }

    public PrintTemplateOutput execute(UpdatePrintTemplateInput input) {
        PrintTemplate saved = repository.save(new PrintTemplate(
                input.getTemplateType(),
                input.getStoreName(),
                input.getStoreAddress(),
                input.getStorePhone(),
                input.getHeader(),
                input.getFooter()));
        return new PrintTemplateOutput(
                saved.getTemplateType(),
                saved.getStoreName(),
                saved.getStoreAddress(),
                saved.getStorePhone(),
                saved.getHeader(),
                saved.getFooter());
    }
}

