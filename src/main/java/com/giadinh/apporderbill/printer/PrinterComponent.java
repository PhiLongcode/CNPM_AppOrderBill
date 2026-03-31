package com.giadinh.apporderbill.printer;

import com.giadinh.apporderbill.printer.usecase.dto.PrintTemplateOutput;
import com.giadinh.apporderbill.printer.usecase.dto.PrinterConfigOutput;
import com.giadinh.apporderbill.printer.usecase.dto.UpdatePrintTemplateInput;
import com.giadinh.apporderbill.printer.usecase.dto.UpdatePrinterConfigInput;

import java.util.List;
import java.util.Optional;

public interface PrinterComponent {
    List<PrinterConfigOutput> getAllPrinterConfigs();
    PrinterConfigOutput updatePrinterConfig(UpdatePrinterConfigInput input);
    Optional<PrintTemplateOutput> getPrintTemplate(String type);
    PrintTemplateOutput updatePrintTemplate(UpdatePrintTemplateInput input);
}

