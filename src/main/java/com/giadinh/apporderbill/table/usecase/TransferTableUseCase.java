package com.giadinh.apporderbill.table.usecase;

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
            return new TransferTableOutput(true, "Chuyển bàn thành công.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new TransferTableOutput(false, "Lỗi chuyển bàn: " + e.getMessage());
        } catch (Exception e) {
            return new TransferTableOutput(false, "Đã xảy ra lỗi không xác định khi chuyển bàn: " + e.getMessage());
        }
    }
}
