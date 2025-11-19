package Core_Records.Operations;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Core_Records.Rate;

public class RateDAO {
    private Connection conn;
    
    public RateDAO(Connection conn) {
        this.conn = conn;
    }
    
    // Create a new rate record
    public boolean addRate(Rate rate) {
        String sql = "INSERT INTO RATE (itemID, gameID, price, effectiveDate) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setObject(1, rate.getItemID());
            pstmt.setObject(2, rate.getGameID());
            pstmt.setBigDecimal(3, rate.getPrice());
            pstmt.setDate(4, rate.getEffectiveDate());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    rate.setRateID(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Read/View a single rate record by ID
    public Rate getRateByID(int rateID) {
        String sql = "SELECT * FROM RATE WHERE rateID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rateID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractRateFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Update an existing rate record
    public boolean updateRate(Rate rate) {
        String sql = "UPDATE RATE SET itemID = ?, gameID = ?, price = ?, effectiveDate = ? WHERE rateID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, rate.getItemID());
            pstmt.setObject(2, rate.getGameID());
            pstmt.setBigDecimal(3, rate.getPrice());
            pstmt.setDate(4, rate.getEffectiveDate());
            pstmt.setInt(5, rate.getRateID());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Delete a rate record
    public boolean deleteRate(int rateID) {
        String sql = "DELETE FROM RATE WHERE rateID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rateID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // List all rates with optional filters
    public List<Rate> listRates(String filterType, Integer filterID, Date filterDate) {
        StringBuilder sql = new StringBuilder("SELECT * FROM RATE WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (filterType != null && filterType.equalsIgnoreCase("ITEM") && filterID != null) {
            sql.append(" AND itemID = ?");
            params.add(filterID);
        } else if (filterType != null && filterType.equalsIgnoreCase("GAME") && filterID != null) {
            sql.append(" AND gameID = ?");
            params.add(filterID);
        }
        
        if (filterDate != null) {
            sql.append(" AND effectiveDate <= ?");
            params.add(filterDate);
        }
        
        sql.append(" ORDER BY effectiveDate DESC");
        
        List<Rate> rates = new ArrayList<>();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                rates.add(extractRateFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return rates;
    }
    
    // View a rate record with related transaction records
    public RateWithTransactions getRateWithTransactions(int rateID) {
        Rate rate = getRateByID(rateID);
        if (rate == null) return null;
        
        RateWithTransactions result = new RateWithTransactions();
        result.rate = rate;
        result.transactions = new ArrayList<>();
        
        String sql = "SELECT rt.transactionID, rt.playerID, p.username, rt.type, rt.amount, rt.transDate " +
                     "FROM ROBUX_TRANSACTION rt " +
                     "JOIN PLAYER p ON rt.playerID = p.playerID " +
                     "WHERE rt.rateAppliedID = ? " +
                     "ORDER BY rt.transDate DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rateID);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                TransactionInfo info = new TransactionInfo();
                info.transactionID = rs.getInt("transactionID");
                info.playerID = rs.getInt("playerID");
                info.username = rs.getString("username");
                info.type = rs.getString("type");
                info.amount = rs.getBigDecimal("amount");
                info.transDate = rs.getTimestamp("transDate");
                result.transactions.add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    // Get the applicable rate for a specific item or game
    public Rate getApplicableRate(String referenceType, int referenceID, Date effectiveDate) {
        String sql;
        
        if (referenceType.equalsIgnoreCase("ITEM")) {
            sql = "SELECT * FROM RATE WHERE itemID = ? AND effectiveDate <= ? " +
                  "ORDER BY effectiveDate DESC LIMIT 1";
        } else if (referenceType.equalsIgnoreCase("GAME")) {
            sql = "SELECT * FROM RATE WHERE gameID = ? AND effectiveDate <= ? " +
                  "ORDER BY effectiveDate DESC LIMIT 1";
        } else {
            return null;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, referenceID);
            pstmt.setDate(2, effectiveDate);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractRateFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Helper method to extract Rate from ResultSet
    private Rate extractRateFromResultSet(ResultSet rs) throws SQLException {
        Rate rate = new Rate();
        rate.setRateID(rs.getInt("rateID"));
        
        int itemID = rs.getInt("itemID");
        rate.setItemID(rs.wasNull() ? null : itemID);
        
        int gameID = rs.getInt("gameID");
        rate.setGameID(rs.wasNull() ? null : gameID);
        
        rate.setPrice(rs.getBigDecimal("price"));
        rate.setEffectiveDate(rs.getDate("effectiveDate"));
        
        return rate;
    }
    
    // Inner classes for viewing rates with related data
    public static class RateWithTransactions {
        public Rate rate;
        public List<TransactionInfo> transactions;
    }
    
    public static class TransactionInfo {
        public int transactionID;
        public int playerID;
        public String username;
        public String type;
        public BigDecimal amount;
        public Timestamp transDate;
        
        @Override
        public String toString() {
            return "Transaction #" + transactionID + " - " + username + 
                   " (" + type + "): " + amount + " on " + transDate;
        }
    }
}