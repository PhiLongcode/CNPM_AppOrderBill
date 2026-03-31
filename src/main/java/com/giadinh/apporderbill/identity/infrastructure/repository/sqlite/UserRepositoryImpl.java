package com.giadinh.apporderbill.identity.infrastructure.repository.sqlite;

import com.giadinh.apporderbill.identity.model.User;
import com.giadinh.apporderbill.identity.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Connection connection;

    public UserRepositoryImpl(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, passwordHash TEXT NOT NULL, roleGroupId INTEGER NOT NULL, FOREIGN KEY (roleGroupId) REFERENCES rolegroups(id))");
        } catch (SQLException e) {
            System.err.println("Error creating users table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT id, username, passwordHash, roleGroupId FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("passwordHash"),
                        rs.getInt("roleGroupId")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by id: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, passwordHash, roleGroupId FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("passwordHash"),
                        rs.getInt("roleGroupId")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, passwordHash, roleGroupId FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("passwordHash"),
                        rs.getInt("roleGroupId")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public void save(User user) {
        if (user.getId() == 0) { // New user
            String sql = "INSERT INTO users (username, passwordHash, roleGroupId) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPasswordHash());
                pstmt.setInt(3, user.getRoleGroupId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error saving new user: " + e.getMessage());
                e.printStackTrace();
            }
        } else { // Existing user
            String sql = "UPDATE users SET username = ?, passwordHash = ?, roleGroupId = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPasswordHash());
                pstmt.setInt(3, user.getRoleGroupId());
                pstmt.setInt(4, user.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error updating user: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
