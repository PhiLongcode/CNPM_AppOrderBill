package com.giadinh.apporderbill.customer.repository;

import com.giadinh.apporderbill.customer.model.Customer;
import com.giadinh.apporderbill.shared.util.MySqlConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlCustomerRepository implements CustomerRepository {
    private final MySqlConnectionProvider connectionProvider;

    public MySqlCustomerRepository(MySqlConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        createTableIfNeeded();
    }

    private void createTableIfNeeded() {
        String sql = """
                CREATE TABLE IF NOT EXISTS customers (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    phone VARCHAR(32) NOT NULL UNIQUE,
                    points INT NOT NULL DEFAULT 0
                )
                """;
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (Exception ignored) {
        }
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> out = new ArrayList<>();
        String sql = "SELECT id, name, phone, points FROM customers ORDER BY id DESC";
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Customer(rs.getLong("id"), rs.getString("name"), rs.getString("phone"), rs.getInt("points")));
            }
        } catch (Exception ignored) {
        }
        return out;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        String sql = "SELECT id, name, phone, points FROM customers WHERE id = ?";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Customer(rs.getLong("id"), rs.getString("name"), rs.getString("phone"), rs.getInt("points")));
                }
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findByPhone(String phone) {
        String sql = "SELECT id, name, phone, points FROM customers WHERE phone = ?";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Customer(rs.getLong("id"), rs.getString("name"), rs.getString("phone"), rs.getInt("points")));
                }
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    @Override
    public Customer save(Customer customer) {
        if (customer.getId() == null || customer.getId() <= 0) {
            String ins = "INSERT INTO customers(name, phone, points) VALUES (?, ?, ?)";
            try (Connection c = connectionProvider.getConnection();
                 PreparedStatement ps = c.prepareStatement(ins)) {
                ps.setString(1, customer.getName());
                ps.setString(2, customer.getPhone());
                ps.setInt(3, customer.getPoints());
                ps.executeUpdate();
            } catch (Exception ignored) {
            }
            return findByPhone(customer.getPhone()).orElse(customer);
        }

        String upd = "UPDATE customers SET name = ?, phone = ?, points = ? WHERE id = ?";
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement(upd)) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getPhone());
            ps.setInt(3, customer.getPoints());
            ps.setLong(4, customer.getId());
            ps.executeUpdate();
        } catch (Exception ignored) {
        }
        return customer;
    }

    @Override
    public void delete(Long id) {
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM customers WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception ignored) {
        }
    }
}
