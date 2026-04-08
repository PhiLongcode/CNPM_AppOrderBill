package com.giadinh.apporderbill.catalog.repository;

import com.giadinh.apporderbill.catalog.model.Category;
import com.giadinh.apporderbill.shared.util.MySqlConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlCategoryRepository implements CategoryRepository {
    private final MySqlConnectionProvider connectionProvider;

    public MySqlCategoryRepository(MySqlConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Optional<Category> findById(int id) {
        String sql = "SELECT id, name, description FROM categories WHERE id = ?";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(new Category(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    @Override
    public Optional<Category> findByName(String name) {
        String sql = "SELECT id, name, description FROM categories WHERE lower(name) = lower(?) LIMIT 1";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(new Category(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    @Override
    public List<Category> findAll() {
        List<Category> out = new ArrayList<>();
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, name, description FROM categories ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(new Category(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
        } catch (Exception ignored) {}
        return out;
    }

    @Override
    public void save(Category category) {
        if (category.getId() <= 0) {
            String ins = "INSERT INTO categories(name, description) VALUES (?, ?)";
            try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(ins)) {
                ps.setString(1, category.getName());
                ps.setString(2, category.getDescription());
                ps.executeUpdate();
            } catch (Exception ignored) {}
            return;
        }
        String upd = "UPDATE categories SET name = ?, description = ? WHERE id = ?";
        try (Connection c = connectionProvider.getConnection(); PreparedStatement ps = c.prepareStatement(upd)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getId());
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    @Override
    public void delete(int id) {
        try (Connection c = connectionProvider.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM categories WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }
}
