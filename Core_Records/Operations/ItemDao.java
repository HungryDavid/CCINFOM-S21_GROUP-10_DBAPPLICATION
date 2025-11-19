package Core_Records.Operations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Core_Records.Item;
import Data_Types.dataTypes.ItemType;

public class ItemDao{
    private String url;
    private String username;
    private String password;

    public ItemDao(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void addItem(Item item) throws SQLException {
      String sql = "INSERT INTO item (itemName, itemType, price, availability, ownerGameID) VALUES (?,?,?,?,?)";
      try (Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getItemType().name());
            stmt.setDouble(3, item.getPrice());
            stmt.setInt(4, item.getAvailability());
            if (item.getOwnerGameID() != null) {
                stmt.setInt(5, item.getOwnerGameID());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.executeUpdate();
            System.out.println("Item added successfully!");
        }
    }

    public Item getItem(int itemID) throws SQLException {
        String sql = "SELECT * FROM item WHERE itemID = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Item item = new Item();
                item.setItemID(rs.getInt("itemID"));
                item.setItemName(rs.getString("itemName"));
                item.setItemType(ItemType.valueOf(rs.getString("itemType")));
                item.setPrice(rs.getDouble("price"));
                item.setAvailability(rs.getInt("availability"));
                int ownerID = rs.getInt("ownerGameID");
                if (!rs.wasNull()) item.setOwnerGameID(ownerID);
                return item;
            }
        }
        return null;
    }

    public List<Item> getAllItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM item";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Item item = new Item();
                item.setItemID(rs.getInt("itemID"));
                item.setItemName(rs.getString("itemName"));
                item.setItemType(ItemType.valueOf(rs.getString("itemType")));
                item.setPrice(rs.getDouble("price"));
                item.setAvailability(rs.getInt("availability"));
                int ownerID = rs.getInt("ownerGameID");
                if (!rs.wasNull()) item.setOwnerGameID(ownerID);
                items.add(item);
            }
        }
        return items;
    }

    public void updateItem(Item item) throws SQLException {
        if (!item.isValid()) {
            System.out.println("Invalid item: Check itemType and ownerGameID!");
            return;
        }

        String sql = "UPDATE item SET itemName=?, itemType=?, price=?, availability=?, ownerGameID=? WHERE itemID=?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getItemType().name());
            stmt.setDouble(3, item.getPrice());
            stmt.setInt(4, item.getAvailability());
            if (item.getOwnerGameID() != null) stmt.setInt(5, item.getOwnerGameID());
            else stmt.setNull(5, Types.INTEGER);
            stmt.setInt(6, item.getItemID());

            stmt.executeUpdate();
            System.out.println("Item updated successfully!");
        }
    }

    public void deleteItem(int itemID) throws SQLException {
      String sql = "DELETE FROM item WHERE itemID = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemID);
            stmt.executeUpdate();
            System.out.println("Item deleted successfully!");
        }
    }
}
