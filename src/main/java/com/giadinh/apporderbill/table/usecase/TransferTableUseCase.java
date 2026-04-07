package com.giadinh.apporderbill.table.usecase;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.DomainMessages;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import com.giadinh.apporderbill.table.service.TableTransferService;
import com.giadinh.apporderbill.table.usecase.dto.TransferTableInput;
import com.giadinh.apporderbill.table.usecase.dto.TransferTableOutput;

public class TransferTableUseCase {

    private final TableTransferService tableTransferService;

    public TransferTableUseCase(TableTransferService tableTransferService) {
        this.tableTransferService = tableTransferService;
    }

    public TransferTableOutput execute(TransferTableInput input) {
        try {
            tableTransferService.transferOrder(input.getOldTableId(), input.getNewTableId());
            return new TransferTableOutput(DomainMessages.formatKey("success.transferTableById"));
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException(ErrorCode.INTERNAL_ERROR, null, e.getMessage(), null);
        }
    }
}
