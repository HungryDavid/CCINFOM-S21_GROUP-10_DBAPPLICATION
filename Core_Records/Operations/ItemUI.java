package Core_Records.Operations;

import java.sql.SQLException;
import java.util.*;
import Core_Records.*;
import Data_Types.dataTypes.ItemType;
import Main.mainMenu;

public class ItemUI {
  private String url;
  private String username;
  private String password;
  private ItemDao dao;
  private Item item = new Item();
  private Scanner scan;
  private mainMenu mainMenu = new mainMenu();

  public ItemUI(String url, String username, String password){
    this.url = url;
    this.username = username;
    this.password = password;
    this.dao = new ItemDao(url, username, password);
    this.scan = new Scanner(System.in);
  }

  public void menu(){
    int choice = 0;
    choice = getChoice();
    callChoice(choice);
  }

  public void displayMenu(){
    System.out.println("\nItem Menu");
    System.out.println("[1] Add Item\n[2] View Item\n[3] List Items\n[4] Update Item\n[5] Delete Item\n[6] Back");
  }

  public int getChoice(){
    int choice = 0;
    while(true){
      displayMenu();
      System.out.print("Enter your choice: ");
      if(scan.hasNextInt()){ // check if input is an integer
        choice = scan.nextInt();   
        if(choice >= 1 && choice <= 6){
          break; // choice is valid
        } else {
            System.out.println("Please enter a number between 1 and 6.\n");
        }
      } else {
        System.out.println("Invalid input! Enter a number.\n");
          scan.next(); // consume the invalid input
      }
    }
    return choice;
  }

  public void callChoice(int choice){
    switch(choice){
      case 1:
        addItem();
        break;
      case 2:
        getItem();
      case 3:
        listItems();
      case 4:
        updateItem();
      case 5:
        deleteItem();
      default:
        break;
    }
    mainMenu.getChoice(scan);
  }

  public void addItem(){
    try{
      scan.nextLine();
      System.out.println("\nAdd Item");
      System.out.print("\nItem Name: ");
      item.setItemName(scan.nextLine());
      System.out.print("Item Type (STORE/GAME): ");
      item.setItemType(ItemType.valueOf(scan.nextLine().toUpperCase()));
      System.out.print("Price: ");
      item.setPrice(Double.parseDouble(scan.nextLine()));
      System.out.print("Availability (1 = available, 0 = unavailable): ");
      item.setAvailability(Integer.parseInt(scan.nextLine()));
      System.out.print("Owner Game ID: ");
      String owner = scan.nextLine();
      item.setOwnerGameID(owner.isEmpty() ? null : Integer.parseInt(owner));
      dao.addItem(item);
      System.out.println("Item has been added");
    } catch (Exception e) {
      System.out.println("Error adding item "+ e.getMessage());
    }
  }

  public void getItem(){
    try {
      scan.nextLine();
      System.out.println("\nView Item");
      System.out.print("Enter Item ID for viewing: ");
      int id = Integer.parseInt(scan.nextLine());
      Item item = dao.getItem(id);
      if(item != null){
        System.out.println(item);
      } else{
        System.out.println("Item not found");
      }
    } catch (Exception e) {
      System.out.println("Error viewing item "+ e.getMessage());
    }
  }

  public void listItems(){
    try {
      List<Item> items = dao.getAllItems();
      System.out.printf("\n\n|%-3s|%-25s|%-15s|%-15s|%-10s|%-3s|\n", "Item Id", "Item Name", "Item Type", "Price", "Avialibility", "OwnerGameID");
      for(Item item : items){
        System.out.printf("|%-3d|%-25s|%-15s|%-15.2f|%-10d|%-3s|\n", item.getItemID(), item.getItemName(), item.getItemType(), item.getPrice(), item.getAvailability(), item.getOwnerGameID() == null ? "-" : item.getOwnerGameID());
      }
    } catch (Exception e) {
      System.out.println("Error listing the items "+ e.getMessage());
    }
  }

  public void updateItem(){
    scan.nextLine();
    System.out.println("\nUpdate Item");
    try {
      System.out.print("\nEnter Item ID for updating: ");
      int id = Integer.parseInt(scan.nextLine());

      Item item = dao.getItem(id);
      if(item == null){
        System.out.println("Item not found.");
      } else {
        System.out.println("Leave the values blank to keep the current values.");
        System.out.print("\nNew Name: ");
        String name = scan.nextLine();
        if(!name.isEmpty()){
          item.setItemName(name);
        }
        System.out.print("New Type: ");
        String type = scan.nextLine();
        if(!type.isEmpty()){
          item.setItemType(ItemType.valueOf(type.toUpperCase()));
        }
        System.out.print("New Price: ");
        String price = scan.nextLine();
        if(!price.isEmpty()){
          item.setPrice(Double.parseDouble(price));
        }
        System.out.print("New Availability: ");
        String avail = scan.nextLine();
        if(!avail.isEmpty()){
          item.setAvailability(Integer.parseInt(avail));
        }
        System.out.print("New Owner Game ID: ");
        String owner = scan.nextLine();
        if(!owner.isEmpty()){
          item.setOwnerGameID(Integer.parseInt(owner));
        }

        dao.updateItem(item);
        System.out.println("Item has been updated");
      }
    } catch (Exception e) {
      System.out.println("Error updating item " + e.getMessage());
    }
  }

  public void deleteItem(){
    try {
      scan.nextLine();
      System.out.println("\nDelete Item");
      System.out.print("Enter item ID to delete: ");
      int id = Integer.parseInt(scan.nextLine());
      dao.deleteItem(id);
      System.out.println("Item has been deleted");
    } catch (Exception e) {
      System.out.println("Error deleting item " + e.getMessage());
    }
  }
}