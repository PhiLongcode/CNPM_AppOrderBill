package com.giadinh.apporderbill.customer.repository;

import com.giadinh.apporderbill.customer.model.Customer;
import com.giadinh.apporderbill.shared.util.SqliteConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteCustomerRepository implements CustomerRepository {
    private final SqliteConnectionProvider connectionProvider;

    public SqliteCustomerRepository(SqliteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        createTableIfNeeded();
    }

    private void createTableIfNeeded() {
        String sql = """
                CREATE TABLE IF NOT EXISTS customers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    phone TEXT NOT NULL UNIQUE,
                    points INTEGER NOT NULL DEFAULT 0
                )
                """;
        try (Connection c = connectionProvider.getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException("Không thể tạo bảng customers", e);
        }
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> out = new ArrayList<>();
        String sql = "SELECT id, name, phone, points FROM customers ORDER BY id DESC";
        try (Connection c = connectionProvider.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                out.add(new Customer(rs.getLong("id"), rs.getString("name"), rs.getString("phone"), rs.getInt("points")));
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc danh sách khách hàng", e);
        }
        return out;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        if (id == null) return Optional.empty();
        String sql = "SELECT id, name, phone, points FROM customers WHERE id = ?";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Customer(rs.getLong("id"), rs.getString("name"), rs.getString("phone"), rs.getInt("points")));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tìm khách hàng theo id", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findByPhone(String phone) {
        if (phone == null || phone.isBlank()) return Optional.empty();
        String sql = "SELECT id, name, phone, points FROM customers WHERE phone = ?";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Customer(rs.getLong("id"), rs.getString("name"), rs.getString("phone"), rs.getInt("points")));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tìm khách hàng theo số điện thoại", e);
        }
        return Optional.empty();
    }

    @Override
    public Customer save(Customer customer) {
        if (customer.getId() == null || customer.getId() <= 0) {
            String sql = "INSERT INTO customers(name, phone, points) VALUES (?, ?, ?)";
            try (Connection c = connectionProvider.getConnection();
                    PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, customer.getName());
                ps.setString(2, customer.getPhone());
                ps.setInt(3, customer.getPoints());
                ps.executeUpdate();
                return findByPhone(customer.getPhone()).orElse(customer);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi thêm khách hàng", e);
            }
        }

        String sql = "UPDATE customers SET name = ?, phone = ?, points = ? WHERE id = ?";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getPhone());
            ps.setInt(3, customer.getPoints());
            ps.setLong(4, customer.getId());
            ps.executeUpdate();
            return customer;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi cập nhật khách hàng", e);
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) return;
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xóa khách hàng", e);
        }
    }
}

