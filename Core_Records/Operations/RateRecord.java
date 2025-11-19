// TO CALL IN MAIN():
// Scanner scan = new Scanner(System.in);
// RateRecord.rateMenu(scan);

package Core_Records.Operations;

import java.sql.*;
import java.util.Scanner;

public class RateRecord {

    private static final String url = "jdbc:mysql://localhost:3306/roblox_db";
    private static final String username = "root";
    private static final String password = "";

    public static void rateMenu(Scanner scan) {
        int choice = -1;

        while (choice != 0) {
            System.out.println("\n===== RATE RECORD MENU =====");
            System.out.println("1. View Rates");
            System.out.println("2. Add Rate");
            System.out.println("3. Edit Rate");
            System.out.println("4. Delete Rate");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");

            choice = scan.nextInt();
            scan.nextLine();

            switch (choice) {
                case 1: viewRates(); break;
                case 2: addRate(scan); break;
                case 3: editRate(scan); break;
                case 4: deleteRate(scan); break;
                case 0: break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // View rates
    public static void viewRates() {

        String sql = "SELECT * FROM RATE";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== LIST OF RATES =====");
            while (rs.next()) {
                System.out.println("Rate ID: " + rs.getInt("rateID"));
                System.out.println("Item ID: " + rs.getInt("itemID"));
                System.out.println("Game ID: " + rs.getInt("gameID"));
                System.out.println("Price: " + rs.getDouble("price"));
                System.out.println("Effective Date: " + rs.getString("effectiveDate"));
                System.out.println("--------------------------------");
            }

        } catch (Exception e) {
            System.out.println("Error retrieving rates.");
            e.printStackTrace();
        }
    }

    // add rate
    public static void addRate(Scanner scan) {

        System.out.print("Is this rate for an ITEM or GAME? ");
        String type = scan.nextLine().trim().toLowerCase();

        int itemID = 0;
        int gameID = 0;

        if (type.equals("item")) {
            System.out.print("Enter Item ID: ");
            itemID = Integer.parseInt(scan.nextLine());
        } else if (type.equals("game")) {
            System.out.print("Enter Game ID: ");
            gameID = Integer.parseInt(scan.nextLine());
        } else {
            System.out.println("Invalid type.");
            return;
        }

        System.out.print("Enter price: ");
        double price = Double.parseDouble(scan.nextLine());

        System.out.print("Enter effective date (YYYY-MM-DD): ");
        String date = scan.nextLine();

        String sql = "INSERT INTO RATE(itemID, gameID, price, effectiveDate) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Only one of them is allowed, other must be NULL
            if (itemID != 0) {
                ps.setInt(1, itemID);
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
                ps.setInt(2, gameID);
            }

            ps.setDouble(3, price);
            ps.setString(4, date);

            ps.executeUpdate();
            System.out.println("Rate successfully added.");

        } catch (Exception e) {
            System.out.println("Error adding rate.");
            e.printStackTrace();
        }
    }

    // edit rate
    public static void editRate(Scanner scan) {
        System.out.print("Enter Rate ID to edit: ");
        int rateID = Integer.parseInt(scan.nextLine());

        System.out.print("Enter new price: ");
        double newPrice = Double.parseDouble(scan.nextLine());

        String sql = "UPDATE RATE SET price = ? WHERE rateID = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newPrice);
            ps.setInt(2, rateID);

            ps.executeUpdate();
            System.out.println("Rate updated successfully.");

        } catch (Exception e) {
            System.out.println("Error updating rate.");
            e.printStackTrace();
        }
    }

    // delete rate
    public static void deleteRate(Scanner scan) {
        System.out.print("Enter Rate ID to delete: ");
        int rateID = Integer.parseInt(scan.nextLine());

        String sql = "DELETE FROM RATE WHERE rateID = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rateID);
            ps.executeUpdate();
            System.out.println("Rate deleted successfully.");

        } catch (Exception e) {
            System.out.println("Error deleting rate.");
            e.printStackTrace();
        }
    }
}
