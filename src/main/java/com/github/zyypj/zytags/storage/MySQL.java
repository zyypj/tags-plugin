package com.github.zyypj.zytags.storage;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Getter
public class MySQL {

    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    public MySQL(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
        createTable();
    }

    private void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS player_history (" +
                "player_uuid VARCHAR(36) PRIMARY KEY," +
                "kills INT DEFAULT 0," +
                "deaths INT DEFAULT 0," +
                "votes INT DEFAULT 0" +
                ")";
        String sql1 = "CREATE TABLE IF NOT EXISTS top_history (" +
                "category VARCHAR(50), " +
                "player_name VARCHAR(16), " +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (category, player_name)" +
                ")";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
        try (PreparedStatement statement = connection.prepareStatement(sql1)) {
            statement.executeUpdate();
        }
    }

    public void updateKDR(String playerUUID, int kills, int deaths) throws SQLException {
        String sql = "INSERT INTO player_history (player_uuid, kills, deaths) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE kills = VALUES(kills), deaths = VALUES(deaths)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID);
            statement.setInt(2, kills);
            statement.setInt(3, deaths);
            statement.executeUpdate();
        }
    }

    public void updateVotes(String playerUUID, int votes) throws SQLException {
        String sql = "INSERT INTO player_history (player_uuid, votes) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE votes = VALUES(votes)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID);
            statement.setInt(2, votes);
            statement.executeUpdate();
        }
    }

    public void saveTopHistory(String category, String playerName) throws SQLException {
        String sql = "INSERT INTO top_history (category, player_name) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE timestamp = CURRENT_TIMESTAMP";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, category);
            stmt.setString(2, playerName);
            stmt.executeUpdate();
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
