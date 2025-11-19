package Core_Records.Operations;

import Core_Records.Player;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO {
    private final String url;
    private final String username;
    private final String password;

    public PlayerDAO(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    // Helper to get a connection using DriverManager
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // Create: returns generated playerID
    public int insert(Player player) throws SQLException {
        String sql = "INSERT INTO PLAYER (username, email, joinDate, robuxBalance) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, player.getUsername());
            ps.setString(2, player.getEmail());
            if (player.getJoinDate() != null) ps.setTimestamp(3, player.getJoinDate());
            else ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setBigDecimal(4, player.getRobuxBalance() != null ? player.getRobuxBalance() : BigDecimal.ZERO);

            int affected = ps.executeUpdate();
            if (affected == 0) throw new SQLException("Creating player failed, no rows affected.");

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                else throw new SQLException("Creating player failed, no ID obtained.");
            }
        }
    }

    // Read by id (non-locking)
    public Player findById(int playerId) throws SQLException {
        String sql = "SELECT playerID, username, email, joinDate, robuxBalance FROM PLAYER WHERE playerID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Player.fromResultSet(rs);
                return null;
            }
        }
    }

    // Find by id and lock row FOR UPDATE (caller manages transaction)
    // Caller must open Connection and call conn.setAutoCommit(false) before calling this
    public Player findByIdForUpdate(Connection conn, int playerId) throws SQLException {
        String sql = "SELECT playerID, username, email, joinDate, robuxBalance FROM PLAYER WHERE playerID = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Player.fromResultSet(rs);
                return null;
            }
        }
    }

    // List all players
    public List<Player> findAll() throws SQLException {
        List<Player> list = new ArrayList<>();
        String sql = "SELECT playerID, username, email, joinDate, robuxBalance FROM PLAYER ORDER BY playerID";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(Player.fromResultSet(rs));
        }
        return list;
    }

    // Update full record
    public void update(Player player) throws SQLException {
        String sql = "UPDATE PLAYER SET username = ?, email = ?, joinDate = ?, robuxBalance = ? WHERE playerID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUsername());
            ps.setString(2, player.getEmail());
            ps.setTimestamp(3, player.getJoinDate());
            ps.setBigDecimal(4, player.getRobuxBalance());
            ps.setInt(5, player.getPlayerID());
            ps.executeUpdate();
        }
    }

    // Delete
    public void delete(int playerId) throws SQLException {
        String sql = "DELETE FROM PLAYER WHERE playerID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.executeUpdate();
        }
    }

    // Update balance using provided connection (use inside a transaction)
    public void updateBalance(Connection conn, int playerId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE PLAYER SET robuxBalance = ? WHERE playerID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, playerId);
            ps.executeUpdate();
        }
    }
}