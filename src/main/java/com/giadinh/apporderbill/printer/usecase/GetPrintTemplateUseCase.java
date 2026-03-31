package com.giadinh.apporderbill.printer.usecase;

import com.giadinh.apporderbill.printer.repository.PrintTemplateRepository;
import com.giadinh.apporderbill.printer.usecase.dto.PrintTemplateOutput;

public class GetPrintTemplateUseCase {
    private final PrintTemplateRepository repository;

    public GetPrintTemplateUseCase(PrintTemplateRepository repository) {
        this.repository = repository;
    }

    public PrintTemplateOutput execute(String type) {
        var t = repository.getByType(type);
        if (t == null) {
            return null;
        }
        return new PrintTemplateOutput(
                t.getTemplateType(),
                t.getStoreName(),
                t.getStoreAddress(),
                t.getStorePhone(),
                t.getHeader(),
                t.getFooter());
    }
}

