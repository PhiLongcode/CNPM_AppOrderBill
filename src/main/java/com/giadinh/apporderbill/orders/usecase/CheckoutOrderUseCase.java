package com.giadinh.apporderbill.orders.usecase;

import com.giadinh.apporderbill.billing.model.Bill;
import com.giadinh.apporderbill.billing.model.BillStatus;
import com.giadinh.apporderbill.billing.repository.BillRepository;
import com.giadinh.apporderbill.orders.model.Order;
import com.giadinh.apporderbill.orders.model.OrderStatus;
import com.giadinh.apporderbill.orders.repository.OrderRepository;
import com.giadinh.apporderbill.orders.usecase.dto.CheckoutOrderInput;
import com.giadinh.apporderbill.orders.usecase.dto.CheckoutOrderOutput;
import com.giadinh.apporderbill.table.model.Table;
import com.giadinh.apporderbill.table.repository.TableRepository;

public class CheckoutOrderUseCase {

    private final OrderRepository orderRepository;
    private final BillRepository billRepository;
    private final TableRepository tableRepository;

    public CheckoutOrderUseCase(OrderRepository orderRepository, BillRepository billRepository, TableRepository tableRepository) {
        this.orderRepository = orderRepository;
        this.billRepository = billRepository;
        this.tableRepository = tableRepository;
    }

    public CheckoutOrderOutput execute(CheckoutOrderInput input) {
        try {
            Order order = orderRepository.findById(input.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại."));

            if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.IN_PROGRESS) {
                return new CheckoutOrderOutput(false, "Đơn hàng không thể thanh toán ở trạng thái hiện tại.", null);
            }

            // Tạo hóa đơn
            Bill bill = new Bill(order.getOrderId(), order.getTotalAmount());
            bill.processPayment(input.getAmountPaid());
            billRepository.save(bill);

            // Cập nhật trạng thái đơn hàng
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);

            // Giải phóng bàn nếu có
            if (order.getTableId() != null) {
                Table table = tableRepository.findById(order.getTableId())
                        .orElseThrow(() -> new IllegalStateException("Bàn của đơn hàng không tồn tại."));
                table.clearTable();
                tableRepository.save(table);
            }

            return new CheckoutOrderOutput(true, "Thanh toán và giải phóng bàn thành công.", bill.getBillId());

        } catch (IllegalArgumentException | IllegalStateException e) {
            return new CheckoutOrderOutput(false, "Lỗi thanh toán: " + e.getMessage(), null);
        } catch (Exception e) {
            return new CheckoutOrderOutput(false, "Đã xảy ra lỗi không xác định khi thanh toán: " + e.getMessage(), null);
        }
    }
}
