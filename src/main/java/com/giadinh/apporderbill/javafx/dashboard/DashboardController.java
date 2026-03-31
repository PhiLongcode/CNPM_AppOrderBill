package com.giadinh.apporderbill.javafx.dashboard;

import com.giadinh.apporderbill.billing.usecase.DeletePaymentsByDateRangeUseCase;
import com.giadinh.apporderbill.billing.usecase.GetPaymentDetailUseCase;
import com.giadinh.apporderbill.billing.usecase.GetPaymentsByDateRangeUseCase;
import com.giadinh.apporderbill.billing.usecase.GetTodayPaymentsUseCase;
import com.giadinh.apporderbill.billing.usecase.ReprintReceiptUseCase;
import com.giadinh.apporderbill.billing.usecase.dto.PaymentDetailOutput;
import com.giadinh.apporderbill.billing.usecase.dto.PaymentSummaryOutput;
import com.giadinh.apporderbill.reporting.usecase.GetDailyRevenueUseCase;
import com.giadinh.apporderbill.reporting.usecase.GetMonthlyRevenueUseCase;
import com.giadinh.apporderbill.reporting.usecase.GetRevenueByDateRangeUseCase;
import com.giadinh.apporderbill.reporting.usecase.GetWeeklyRevenueUseCase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DashboardController {
    @FXML private Label totalRevenueLabel;
    @FXML private Label totalBillsLabel;
    @FXML private Label averageBillLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private DatePicker paymentStartDatePicker;
    @FXML private DatePicker paymentEndDatePicker;
    @FXML private TableView<DailyRevenueRow> dailyRevenueTable;
    @FXML private TableColumn<DailyRevenueRow, String> dateColumn;
    @FXML private TableColumn<DailyRevenueRow, String> revenueColumn;
    @FXML private TableColumn<DailyRevenueRow, Integer> billsColumn;
    @FXML private TableView<PaymentSummaryOutput> paymentsTable;
    @FXML private TableColumn<PaymentSummaryOutput, Long> paymentIdColumn;
    @FXML private TableColumn<PaymentSummaryOutput, String> paymentTableColumn;
    @FXML private TableColumn<PaymentSummaryOutput, Long> paymentAmountColumn;
    @FXML private TableColumn<PaymentSummaryOutput, String> paymentMethodColumn;
    @FXML private TableColumn<PaymentSummaryOutput, String> paymentTimeColumn;

    private final ObservableList<PaymentSummaryOutput> paymentRows = FXCollections.observableArrayList();
    private GetDailyRevenueUseCase getDailyRevenueUseCase;
    private GetWeeklyRevenueUseCase getWeeklyRevenueUseCase;
    private GetMonthlyRevenueUseCase getMonthlyRevenueUseCase;
    private GetRevenueByDateRangeUseCase getRevenueByDateRangeUseCase;
    private GetTodayPaymentsUseCase getTodayPaymentsUseCase;
    private GetPaymentsByDateRangeUseCase getPaymentsByDateRangeUseCase;
    private GetPaymentDetailUseCase getPaymentDetailUseCase;
    private DeletePaymentsByDateRangeUseCase deletePaymentsByDateRangeUseCase;
    private ReprintReceiptUseCase reprintReceiptUseCase;

    @FXML
    public void initialize() {
        paymentsTable.setItems(paymentRows);
        paymentIdColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getPaymentId()));
        paymentTableColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTableNumber()));
        paymentAmountColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getFinalAmount()));
        paymentMethodColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPaymentMethod()));
        paymentTimeColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getPaidAt() == null ? "" : c.getValue().getPaidAt().toString()));
    }

    public void init(
            GetDailyRevenueUseCase a,
            GetWeeklyRevenueUseCase b,
            GetMonthlyRevenueUseCase c,
            GetRevenueByDateRangeUseCase d,
            GetTodayPaymentsUseCase e,
            GetPaymentsByDateRangeUseCase f,
            GetPaymentDetailUseCase g,
            DeletePaymentsByDateRangeUseCase h,
            ReprintReceiptUseCase i) {
        this.getDailyRevenueUseCase = a;
        this.getWeeklyRevenueUseCase = b;
        this.getMonthlyRevenueUseCase = c;
        this.getRevenueByDateRangeUseCase = d;
        this.getTodayPaymentsUseCase = e;
        this.getPaymentsByDateRangeUseCase = f;
        this.getPaymentDetailUseCase = g;
        this.deletePaymentsByDateRangeUseCase = h;
        this.reprintReceiptUseCase = i;
        onRefreshClick();
        onRefreshPaymentsClick();
    }

    @FXML
    private void onRefreshClick() {
        if (getDailyRevenueUseCase == null) return;
        long amount = getDailyRevenueUseCase.execute(LocalDate.now()).getTotalRevenue();
        totalRevenueLabel.setText(amount + " VNĐ");
        int bills = paymentRows.size();
        totalBillsLabel.setText(String.valueOf(bills));
        averageBillLabel.setText((bills == 0 ? 0 : amount / bills) + " VNĐ");
    }

    @FXML
    private void onLoadPaymentsClick() {
        if (getPaymentsByDateRangeUseCase == null) return;
        LocalDate start = paymentStartDatePicker.getValue();
        LocalDate end = paymentEndDatePicker.getValue();
        if (start == null || end == null) {
            showInfo("Vui lòng chọn khoảng ngày.");
            return;
        }
        paymentRows.setAll(getPaymentsByDateRangeUseCase.execute(start.atStartOfDay(), end.atTime(23, 59, 59)));
    }

    @FXML
    private void onRefreshPaymentsClick() {
        if (getTodayPaymentsUseCase == null) return;
        paymentRows.setAll(getTodayPaymentsUseCase.execute());
    }

    @FXML
    private void onViewDetailClick() {
        PaymentSummaryOutput selected = paymentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Vui lòng chọn hóa đơn cần xem.");
            return;
        }
        if (getPaymentDetailUseCase == null) return;
        try {
            PaymentDetailOutput detail = getPaymentDetailUseCase.execute(selected.getPaymentId());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("payment-detail-dialog.fxml"));
            Scene scene = new Scene(loader.load());
            PaymentDetailDialogController controller = loader.getController();
            controller.setPaymentDetail(detail);
            Stage stage = new Stage();
            stage.setTitle("Chi tiết hóa đơn");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception ex) {
            showInfo("Không thể mở chi tiết hóa đơn: " + ex.getMessage());
        }
    }

    @FXML
    private void onDeleteSelectedClick() {
        PaymentSummaryOutput selected = paymentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Vui lòng chọn hóa đơn cần xóa khỏi danh sách.");
            return;
        }
        paymentRows.remove(selected);
    }

    @FXML
    private void onDeleteByDateRangeClick() {
        if (deletePaymentsByDateRangeUseCase == null) return;
        LocalDate start = paymentStartDatePicker.getValue();
        LocalDate end = paymentEndDatePicker.getValue();
        if (start == null || end == null) {
            showInfo("Vui lòng chọn khoảng ngày để xóa.");
            return;
        }
        deletePaymentsByDateRangeUseCase.execute(start.atStartOfDay(), end.atTime(23, 59, 59));
        onLoadPaymentsClick();
    }

    private void showInfo(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class DailyRevenueRow {
        private final String date;
        private final String revenue;
        private final int bills;

        public DailyRevenueRow(String date, String revenue, int bills) {
            this.date = date;
            this.revenue = revenue;
            this.bills = bills;
        }
        public String getDate() { return date; }
        public String getRevenue() { return revenue; }
        public int getBills() { return bills; }
    }
}

