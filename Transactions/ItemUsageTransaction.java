import java.sql.*;
import java.util.Scanner;

public class ItemUsageTransaction {

    private static final String url = "jdbc:mysql://localhost:3306/roblox_db";
    private static final String username = "root";
    private static final String password = "";

    // MENU
    public static void itemUsageMenu(Scanner scan) {
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n===== ITEM USAGE TRANSACTION =====");
            System.out.println("1. Record Item Usage");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");

            choice = scan.nextInt();
            scan.nextLine();

            switch (choice) {
                case 1:
                    recordItemUsage(scan);
                    break;
                case 0:
                    System.out.println("Returning...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    //   MAIN TRANSACTION LOGIC
    public static void recordItemUsage(Scanner scan) {

        System.out.println("\n=== RECORD ITEM USAGE ===");

        System.out.print("Enter Player ID: ");
        int playerID = Integer.parseInt(scan.nextLine());

        System.out.print("Enter Item ID: ");
        int itemID = Integer.parseInt(scan.nextLine());

        System.out.print("Enter Game ID: ");
        int gameID = Integer.parseInt(scan.nextLine());

        System.out.print("Enter Session ID: ");
        int sessionID = Integer.parseInt(scan.nextLine());

        System.out.print("Enter Effect/Result (ex: +10 speed): ");
        String effect = scan.nextLine();

        // 1. Check if player owns the item (PLAYER_ITEM)
        if (!playerOwnsItem(playerID, itemID)) {
            System.out.println("Player DOES NOT own this item! Transaction cancelled.");
            return;
        }

        // 2. Insert INTO ITEM_USAGE
        insertItemUsageRecord(playerID, itemID, gameID, sessionID, effect);

        // 3. Update item availability (if consumable)
        updateItemAvailability(itemID);

        System.out.println("Item usage recorded successfully!");
    }

    // 1. Validate ownership in PLAYER_ITEM
    private static boolean playerOwnsItem(int playerID, int itemID) {

        String sql = "SELECT quantity FROM PLAYER_ITEM WHERE playerID = ? AND itemID = ? AND quantity > 0";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, playerID);
            ps.setInt(2, itemID);

            return ps.executeQuery().next();  // true if exists AND quantity > 0

        } catch (Exception e) {
            System.out.println("Error checking ownership.");
            e.printStackTrace();
            return false;
        }
    }

    // 2. Insert record INTO ITEM_USAGE
    private static void insertItemUsageRecord(
            int playerID, int itemID, int gameID, int sessionID, String effect) {

        String sql = "INSERT INTO ITEM_USAGE (playerID, itemID, gameID, sessionID, usageDate, effectResult) "
                   + "VALUES (?, ?, ?, ?, NOW(), ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, playerID);
            ps.setInt(2, itemID);
            ps.setInt(3, gameID);
            ps.setInt(4, sessionID);
            ps.setString(5, effect);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error inserting item usage record.");
            e.printStackTrace();
        }
    }

    // 3. Update ITEM availability
    private static void updateItemAvailability(int itemID) {

        String sql =
            "UPDATE ITEM SET availability = availability - 1 "
          + "WHERE itemID = ? AND availability > 0";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemID);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error updating item availability.");
            e.printStackTrace();
        }
    }
}