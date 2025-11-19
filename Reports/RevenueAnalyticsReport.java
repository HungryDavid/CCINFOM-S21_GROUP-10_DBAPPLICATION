package Reports;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RevenueAnalyticsReport {
    private Connection conn;
    
    public RevenueAnalyticsReport(Connection conn) {
        this.conn = conn;
    }
    
    /**
     * Generate Revenue Analytics Report by Day
     * 
     * @param year The year to generate report for
     * @param month The month to generate report for
     * @param day The day to generate report for
     * @return RevenueReport object containing all revenue data
     */
    public RevenueReport generateDailyReport(int year, int month, int day) {
        String dateStr = String.format("%04d-%02d-%02d", year, month, day);
        
        RevenueReport report = new RevenueReport();
        report.reportType = "Daily";
        report.period = dateStr;
        report.details = new ArrayList<>();
        
        // Query for purchase revenue
        String purchaseSql = "SELECT " +
                           "DATE(purchaseDate) as reportDate, " +
                           "COUNT(*) as transactionCount, " +
                           "SUM(priceAtPurchase) as totalRevenue " +
                           "FROM PURCHASE " +
                           "WHERE DATE(purchaseDate) = ? " +
                           "GROUP BY DATE(purchaseDate)";
        
        // Query for robux transaction revenue (only 'spend' type contributes to revenue)
        String robuxSql = "SELECT " +
                        "DATE(transDate) as reportDate, " +
                        "COUNT(*) as transactionCount, " +
                        "SUM(amount) as totalRevenue " +
                        "FROM ROBUX_TRANSACTION " +
                        "WHERE DATE(transDate) = ? AND type = 'spend' " +
                        "GROUP BY DATE(transDate)";
        
        try {
            // Get purchase revenue
            BigDecimal purchaseRevenue = BigDecimal.ZERO;
            int purchaseCount = 0;
            
            try (PreparedStatement pstmt = conn.prepareStatement(purchaseSql)) {
                pstmt.setString(1, dateStr);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    purchaseCount = rs.getInt("transactionCount");
                    purchaseRevenue = rs.getBigDecimal("totalRevenue");
                    if (purchaseRevenue == null) purchaseRevenue = BigDecimal.ZERO;
                }
            }
            
            // Get robux transaction revenue
            BigDecimal robuxRevenue = BigDecimal.ZERO;
            int robuxCount = 0;
            
            try (PreparedStatement pstmt = conn.prepareStatement(robuxSql)) {
                pstmt.setString(1, dateStr);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    robuxCount = rs.getInt("transactionCount");
                    robuxRevenue = rs.getBigDecimal("totalRevenue");
                    if (robuxRevenue == null) robuxRevenue = BigDecimal.ZERO;
                }
            }
            
            // Calculate totals
            report.totalPurchaseRevenue = purchaseRevenue;
            report.totalRobuxRevenue = robuxRevenue;
            report.totalRevenue = purchaseRevenue.add(robuxRevenue);
            report.totalTransactions = purchaseCount + robuxCount;
            
            // Get detailed breakdown
            report.details = getDetailedRevenueBreakdown(dateStr, "day");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return report;
    }
    
    /**
     * Generate Revenue Analytics Report by Month
     */
    public RevenueReport generateMonthlyReport(int year, int month) {
        String periodStr = String.format("%04d-%02d", year, month);
        
        RevenueReport report = new RevenueReport();
        report.reportType = "Monthly";
        report.period = periodStr;
        report.details = new ArrayList<>();
        
        String purchaseSql = "SELECT " +
                           "COUNT(*) as transactionCount, " +
                           "SUM(priceAtPurchase) as totalRevenue " +
                           "FROM PURCHASE " +
                           "WHERE YEAR(purchaseDate) = ? AND MONTH(purchaseDate) = ?";
        
        String robuxSql = "SELECT " +
                        "COUNT(*) as transactionCount, " +
                        "SUM(amount) as totalRevenue " +
                        "FROM ROBUX_TRANSACTION " +
                        "WHERE YEAR(transDate) = ? AND MONTH(transDate) = ? AND type = 'spend'";
        
        try {
            BigDecimal purchaseRevenue = BigDecimal.ZERO;
            int purchaseCount = 0;
            
            try (PreparedStatement pstmt = conn.prepareStatement(purchaseSql)) {
                pstmt.setInt(1, year);
                pstmt.setInt(2, month);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    purchaseCount = rs.getInt("transactionCount");
                    purchaseRevenue = rs.getBigDecimal("totalRevenue");
                    if (purchaseRevenue == null) purchaseRevenue = BigDecimal.ZERO;
                }
            }
            
            BigDecimal robuxRevenue = BigDecimal.ZERO;
            int robuxCount = 0;
            
            try (PreparedStatement pstmt = conn.prepareStatement(robuxSql)) {
                pstmt.setInt(1, year);
                pstmt.setInt(2, month);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    robuxCount = rs.getInt("transactionCount");
                    robuxRevenue = rs.getBigDecimal("totalRevenue");
                    if (robuxRevenue == null) robuxRevenue = BigDecimal.ZERO;
                }
            }
            
            report.totalPurchaseRevenue = purchaseRevenue;
            report.totalRobuxRevenue = robuxRevenue;
            report.totalRevenue = purchaseRevenue.add(robuxRevenue);
            report.totalTransactions = purchaseCount + robuxCount;
            
            report.details = getDetailedRevenueBreakdownMonth(year, month);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return report;
    }
    
    /**
     * Generate Revenue Analytics Report by Year
     */
    public RevenueReport generateYearlyReport(int year) {
        RevenueReport report = new RevenueReport();
        report.reportType = "Yearly";
        report.period = String.valueOf(year);
        report.details = new ArrayList<>();
        
        String purchaseSql = "SELECT " +
                           "COUNT(*) as transactionCount, " +
                           "SUM(priceAtPurchase) as totalRevenue " +
                           "FROM PURCHASE " +
                           "WHERE YEAR(purchaseDate) = ?";
        
        String robuxSql = "SELECT " +
                        "COUNT(*) as transactionCount, " +
                        "SUM(amount) as totalRevenue " +
                        "FROM ROBUX_TRANSACTION " +
                        "WHERE YEAR(transDate) = ? AND type = 'spend'";
        
        try {
            BigDecimal purchaseRevenue = BigDecimal.ZERO;
            int purchaseCount = 0;
            
            try (PreparedStatement pstmt = conn.prepareStatement(purchaseSql)) {
                pstmt.setInt(1, year);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    purchaseCount = rs.getInt("transactionCount");
                    purchaseRevenue = rs.getBigDecimal("totalRevenue");
                    if (purchaseRevenue == null) purchaseRevenue = BigDecimal.ZERO;
                }
            }
            
            BigDecimal robuxRevenue = BigDecimal.ZERO;
            int robuxCount = 0;
            
            try (PreparedStatement pstmt = conn.prepareStatement(robuxSql)) {
                pstmt.setInt(1, year);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    robuxCount = rs.getInt("transactionCount");
                    robuxRevenue = rs.getBigDecimal("totalRevenue");
                    if (robuxRevenue == null) robuxRevenue = BigDecimal.ZERO;
                }
            }
            
            report.totalPurchaseRevenue = purchaseRevenue;
            report.totalRobuxRevenue = robuxRevenue;
            report.totalRevenue = purchaseRevenue.add(robuxRevenue);
            report.totalTransactions = purchaseCount + robuxCount;
            
            report.details = getDetailedRevenueBreakdownYear(year);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return report;
    }
    
    /**
     * Get detailed revenue breakdown by player for a specific day
     */
    private List<RevenueDetail> getDetailedRevenueBreakdown(String date, String period) {
        List<RevenueDetail> details = new ArrayList<>();
        
        String sql = "SELECT p.playerID, p.username, " +
                   "(SELECT COALESCE(SUM(priceAtPurchase), 0) FROM PURCHASE " +
                   " WHERE playerID = p.playerID AND DATE(purchaseDate) = ?) as purchaseTotal, " +
                   "(SELECT COALESCE(SUM(amount), 0) FROM ROBUX_TRANSACTION " +
                   " WHERE playerID = p.playerID AND DATE(transDate) = ? AND type = 'spend') as robuxTotal " +
                   "FROM PLAYER p " +
                   "HAVING purchaseTotal > 0 OR robuxTotal > 0 " +
                   "ORDER BY (purchaseTotal + robuxTotal) DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            pstmt.setString(2, date);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                RevenueDetail detail = new RevenueDetail();
                detail.playerID = rs.getInt("playerID");
                detail.username = rs.getString("username");
                detail.purchaseRevenue = rs.getBigDecimal("purchaseTotal");
                detail.robuxRevenue = rs.getBigDecimal("robuxTotal");
                detail.totalRevenue = detail.purchaseRevenue.add(detail.robuxRevenue);
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return details;
    }
    
    /**
     * Get detailed revenue breakdown for a month
     */
    private List<RevenueDetail> getDetailedRevenueBreakdownMonth(int year, int month) {
        List<RevenueDetail> details = new ArrayList<>();
        
        String sql = "SELECT p.playerID, p.username, " +
                   "(SELECT COALESCE(SUM(priceAtPurchase), 0) FROM PURCHASE " +
                   " WHERE playerID = p.playerID AND YEAR(purchaseDate) = ? AND MONTH(purchaseDate) = ?) as purchaseTotal, " +
                   "(SELECT COALESCE(SUM(amount), 0) FROM ROBUX_TRANSACTION " +
                   " WHERE playerID = p.playerID AND YEAR(transDate) = ? AND MONTH(transDate) = ? AND type = 'spend') as robuxTotal " +
                   "FROM PLAYER p " +
                   "HAVING purchaseTotal > 0 OR robuxTotal > 0 " +
                   "ORDER BY (purchaseTotal + robuxTotal) DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            pstmt.setInt(3, year);
            pstmt.setInt(4, month);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                RevenueDetail detail = new RevenueDetail();
                detail.playerID = rs.getInt("playerID");
                detail.username = rs.getString("username");
                detail.purchaseRevenue = rs.getBigDecimal("purchaseTotal");
                detail.robuxRevenue = rs.getBigDecimal("robuxTotal");
                detail.totalRevenue = detail.purchaseRevenue.add(detail.robuxRevenue);
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return details;
    }
    
    /**
     * Get detailed revenue breakdown for a year
     */
    private List<RevenueDetail> getDetailedRevenueBreakdownYear(int year) {
        List<RevenueDetail> details = new ArrayList<>();
        
        String sql = "SELECT p.playerID, p.username, " +
                   "(SELECT COALESCE(SUM(priceAtPurchase), 0) FROM PURCHASE " +
                   " WHERE playerID = p.playerID AND YEAR(purchaseDate) = ?) as purchaseTotal, " +
                   "(SELECT COALESCE(SUM(amount), 0) FROM ROBUX_TRANSACTION " +
                   " WHERE playerID = p.playerID AND YEAR(transDate) = ? AND type = 'spend') as robuxTotal " +
                   "FROM PLAYER p " +
                   "HAVING purchaseTotal > 0 OR robuxTotal > 0 " +
                   "ORDER BY (purchaseTotal + robuxTotal) DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, year);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                RevenueDetail detail = new RevenueDetail();
                detail.playerID = rs.getInt("playerID");
                detail.username = rs.getString("username");
                detail.purchaseRevenue = rs.getBigDecimal("purchaseTotal");
                detail.robuxRevenue = rs.getBigDecimal("robuxTotal");
                detail.totalRevenue = detail.purchaseRevenue.add(detail.robuxRevenue);
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return details;
    }
    
    /**
     * Get peak earnings information
     */
    public PeakEarningsInfo getPeakEarnings(int year) {
        PeakEarningsInfo info = new PeakEarningsInfo();
        
        String sql = "SELECT DATE(transDate) as earningDate, " +
                   "SUM(CASE WHEN transDate IN (SELECT purchaseDate FROM PURCHASE) " +
                   "    THEN (SELECT priceAtPurchase FROM PURCHASE WHERE purchaseDate = transDate LIMIT 1) " +
                   "    ELSE amount END) as dailyRevenue " +
                   "FROM (SELECT purchaseDate as transDate, priceAtPurchase FROM PURCHASE " +
                   "      WHERE YEAR(purchaseDate) = ? " +
                   "      UNION ALL " +
                   "      SELECT transDate, amount FROM ROBUX_TRANSACTION " +
                   "      WHERE YEAR(transDate) = ? AND type = 'spend') combined " +
                   "GROUP BY DATE(transDate) " +
                   "ORDER BY dailyRevenue DESC " +
                   "LIMIT 1";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, year);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                info.date = rs.getDate("earningDate");
                info.revenue = rs.getBigDecimal("dailyRevenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return info;
    }
    
    // Inner classes for report structure
    public static class RevenueReport {
        public String reportType;
        public String period;
        public BigDecimal totalPurchaseRevenue;
        public BigDecimal totalRobuxRevenue;
        public BigDecimal totalRevenue;
        public int totalTransactions;
        public List<RevenueDetail> details;
        
        public void printReport() {
            System.out.println("========================================");
            System.out.println("  REVENUE ANALYTICS REPORT (" + reportType + ")");
            System.out.println("  Period: " + period);
            System.out.println("========================================");
            System.out.println("Total Purchase Revenue: " + totalPurchaseRevenue);
            System.out.println("Total Robux Transaction Revenue: " + totalRobuxRevenue);
            System.out.println("TOTAL REVENUE: " + totalRevenue);
            System.out.println("Total Transactions: " + totalTransactions);
            System.out.println("----------------------------------------");
            System.out.println("Revenue Breakdown by Player:");
            
            for (RevenueDetail detail : details) {
                System.out.println("  " + detail.username + " (ID: " + detail.playerID + ")");
                System.out.println("    Purchases: " + detail.purchaseRevenue);
                System.out.println("    Robux Transactions: " + detail.robuxRevenue);
                System.out.println("    Total: " + detail.totalRevenue);
            }
            System.out.println("========================================");
        }
    }
    
    public static class RevenueDetail {
        public int playerID;
        public String username;
        public BigDecimal purchaseRevenue;
        public BigDecimal robuxRevenue;
        public BigDecimal totalRevenue;
    }
    
    public static class PeakEarningsInfo {
        public Date date;
        public BigDecimal revenue;
        
        @Override
        public String toString() {
            return "Peak Earnings: " + revenue + " on " + date;
        }
    }
}