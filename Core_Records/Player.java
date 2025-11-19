package Core_Records;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Player {
    private int playerID;
    private String username;
    private String email;
    private Timestamp joinDate;
    private BigDecimal robuxBalance = BigDecimal.ZERO;

    public Player() {}

    public Player(String username, String email, Timestamp joinDate, BigDecimal robuxBalance) {
        this.username = username;
        this.email = email;
        this.joinDate = joinDate;
        this.robuxBalance = (robuxBalance == null ? BigDecimal.ZERO : robuxBalance);
    }

    public static Player fromResultSet(ResultSet rs) throws SQLException {
        Player p = new Player();
        p.setPlayerID(rs.getInt("playerID"));
        p.setUsername(rs.getString("username"));
        p.setEmail(rs.getString("email"));
        p.setJoinDate(rs.getTimestamp("joinDate"));
        BigDecimal bal = rs.getBigDecimal("robuxBalance");
        p.setRobuxBalance(bal == null ? BigDecimal.ZERO : bal);
        return p;
    }

    // Getters
    public int getPlayerID() { return playerID; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Timestamp getJoinDate() { return joinDate; }
    public BigDecimal getRobuxBalance() { return robuxBalance; }

    // Setters
    public void setPlayerID(int playerID) { this.playerID = playerID; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setJoinDate(Timestamp joinDate) { this.joinDate = joinDate; }
    public void setRobuxBalance(BigDecimal robuxBalance) {
        this.robuxBalance = (robuxBalance == null ? BigDecimal.ZERO : robuxBalance);
    }

    public boolean isValidForInsert() {
        return username != null && !username.trim().isEmpty()
            && email != null && email.contains("@");
    }

    public BigDecimal credit(BigDecimal amount) {
        if (amount == null) amount = BigDecimal.ZERO;
        robuxBalance = robuxBalance.add(amount);
        return robuxBalance;
    }

    public BigDecimal debit(BigDecimal amount) {
        if (amount == null) amount = BigDecimal.ZERO;
        if (robuxBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient robux balance");
        }
        robuxBalance = robuxBalance.subtract(amount);
        return robuxBalance;
    }
}