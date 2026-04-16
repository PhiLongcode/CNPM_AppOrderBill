package com.giadinh.apporderbill.printer.usecase;

import com.giadinh.apporderbill.printer.model.PrinterConfig;
import com.giadinh.apporderbill.printer.repository.PrinterConfigRepository;
import com.giadinh.apporderbill.printer.usecase.dto.UpdatePrinterConfigInput;
import com.giadinh.apporderbill.shared.error.DomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdatePrinterConfigUseCaseTest {

    @Test
    void shouldNormalizeAndSaveValidConfig() {
        InMemoryRepo repo = new InMemoryRepo();
        UpdatePrinterConfigUseCase useCase = new UpdatePrinterConfigUseCase(repo);

        useCase.execute(new UpdatePrinterConfigInput("Front Desk", "windows", "80MM", 2, true, true));

        assertEquals("WINDOWS", repo.saved.getConnectionType());
        assertEquals("80mm", repo.saved.getPaperSize());
    }

    @Test
    void shouldThrowOnInvalidValues() {
        InMemoryRepo repo = new InMemoryRepo();
        UpdatePrinterConfigUseCase useCase = new UpdatePrinterConfigUseCase(repo);

        assertThrows(DomainException.class, () -> useCase.execute(new UpdatePrinterConfigInput("", "WINDOWS", "80mm", 1, true, true)));
        assertThrows(DomainException.class, () -> useCase.execute(new UpdatePrinterConfigInput("A", "SERIAL", "80mm", 1, true, true)));
        assertThrows(DomainException.class, () -> useCase.execute(new UpdatePrinterConfigInput("A", "WINDOWS", "76mm", 1, true, true)));
        assertThrows(DomainException.class, () -> useCase.execute(new UpdatePrinterConfigInput("A", "WINDOWS", "80mm", 0, true, true)));
    }

    private static class InMemoryRepo implements PrinterConfigRepository {
        private PrinterConfig saved = new PrinterConfig("Default Printer", "WINDOWS", "80mm", 1, true, true);

        @Override
        public PrinterConfig save(PrinterConfig config) {
            this.saved = config;
            return config;
        }

        @Override
        public PrinterConfig getCurrent() {
            return saved;
        }
    }
}
