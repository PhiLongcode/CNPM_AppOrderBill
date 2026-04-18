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
import com.giadinh.apporderbill.shared.error.DomainMessages;
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

        if (dateColumn != null) {
            dateColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDate()));
        }
        if (revenueColumn != null) {
            revenueColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRevenue()));
        }
        if (billsColumn != null) {
            billsColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getBills()));
        }
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
        // Load payments first, then stats that depend on them
        onRefreshPaymentsClick();
        onRefreshClick();
    }

    // Track current filter mode
    private enum Period { TODAY, WEEK, MONTH, CUSTOM }
    private Period currentPeriod = Period.TODAY;

    @FXML private javafx.scene.control.ToggleButton todayBtn;
    @FXML private javafx.scene.control.ToggleButton weekBtn;
    @FXML private javafx.scene.control.ToggleButton monthBtn;
    @FXML private javafx.scene.control.ToggleButton customBtn;

    @FXML
    private void onTodayClick() {
        currentPeriod = Period.TODAY;
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        onRefreshClick();
        onRefreshPaymentsClick();
    }

    @FXML
    private void onWeekClick() {
        currentPeriod = Period.WEEK;
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1L);
        startDatePicker.setValue(weekStart);
        endDatePicker.setValue(weekStart.plusDays(6));
        onRefreshClick();
        loadPaymentsByCurrentPeriod();
    }

    @FXML
    private void onMonthClick() {
        currentPeriod = Period.MONTH;
        LocalDate today = LocalDate.now();
        startDatePicker.setValue(today.withDayOfMonth(1));
        endDatePicker.setValue(today.withDayOfMonth(today.lengthOfMonth()));
        onRefreshClick();
        loadPaymentsByCurrentPeriod();
    }

    @FXML
    private void onCustomClick() {
        currentPeriod = Period.CUSTOM;
        // Just change mode; user picks dates then hits Làm mới
    }

    @FXML
    private void onRefreshClick() {
        if (getDailyRevenueUseCase == null) return;

        com.giadinh.apporderbill.reporting.usecase.dto.RevenueSummaryOutput summary;
        switch (currentPeriod) {
            case WEEK -> summary = getWeeklyRevenueUseCase.execute(LocalDate.now());
            case MONTH -> summary = getMonthlyRevenueUseCase.execute(LocalDate.now());
            case CUSTOM -> {
                LocalDate s = startDatePicker.getValue();
                LocalDate e = endDatePicker.getValue();
                if (s == null || e == null) {
                    summary = getDailyRevenueUseCase.execute(LocalDate.now());
                } else {
                    summary = getRevenueByDateRangeUseCase.execute(
                        new com.giadinh.apporderbill.reporting.usecase.dto.GetRevenueByDateRangeInput(
                            s.atStartOfDay(), e.atTime(java.time.LocalTime.MAX)));
                    loadPaymentsByCurrentPeriod();
                }
            }
            default -> summary = getDailyRevenueUseCase.execute(LocalDate.now());
        }

        long amount = summary.getTotalRevenue();

        // Populate daily revenue table grouped by date
        if (dailyRevenueTable != null) {
            var grouped = summary.getItems().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    com.giadinh.apporderbill.reporting.usecase.dto.RevenueOutput::getPeriod,
                    java.util.stream.Collectors.summarizingLong(
                        com.giadinh.apporderbill.reporting.usecase.dto.RevenueOutput::getAmount)));
            var rows = grouped.entrySet().stream()
                .map(entry -> new DailyRevenueRow(
                    entry.getKey(),
                    String.format("%,d VNĐ", entry.getValue().getSum()),
                    (int) entry.getValue().getCount()))
                .sorted((a2, b2) -> b2.getDate().compareTo(a2.getDate()))
                .toList();
            dailyRevenueTable.getItems().setAll(rows);
        }

        int bills = paymentRows.size();
        totalRevenueLabel.setText(String.format("%,d VNĐ", amount));
        totalBillsLabel.setText(String.valueOf(bills));
        averageBillLabel.setText(String.format("%,d VNĐ", bills == 0 ? 0 : amount / bills));
    }

    private void loadPaymentsByCurrentPeriod() {
        LocalDate s = startDatePicker.getValue();
        LocalDate e = endDatePicker.getValue();
        if (s != null && e != null && getPaymentsByDateRangeUseCase != null) {
            paymentRows.setAll(getPaymentsByDateRangeUseCase.execute(s.atStartOfDay(), e.atTime(23, 59, 59)));
        }
    }


    @FXML
    private void onLoadPaymentsClick() {
        if (getPaymentsByDateRangeUseCase == null) return;
        LocalDate start = paymentStartDatePicker.getValue();
        LocalDate end = paymentEndDatePicker.getValue();
        if (start == null || end == null) {
            showInfo(msg("ui.dashboard.select_date_range"));
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
            showInfo(msg("ui.dashboard.select_payment_to_view"));
            return;
        }
        if (getPaymentDetailUseCase == null) return;
        try {
            PaymentDetailOutput detail = getPaymentDetailUseCase.execute(selected.getPaymentId());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("payment-detail-dialog.fxml"));
            ClassLoader cl = getClass().getClassLoader();
            if (cl != null) {
                loader.setClassLoader(cl);
            }
            Scene scene = new Scene(loader.load());
            PaymentDetailDialogController controller = loader.getController();
            controller.setPaymentDetail(detail);
            Stage stage = new Stage();
            stage.setTitle(msg("ui.dashboard.payment_detail_title"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception ex) {
            showInfo(msg("ui.dashboard.open_payment_detail_failed", ex.getMessage()));
        }
    }

    @FXML
    private void onDeleteSelectedClick() {
        PaymentSummaryOutput selected = paymentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo(msg("ui.dashboard.select_payment_to_remove"));
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
            showInfo(msg("ui.dashboard.select_date_range_to_delete"));
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

    private String msg(String key, Object... args) {
        return DomainMessages.formatKey(key, args);
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

