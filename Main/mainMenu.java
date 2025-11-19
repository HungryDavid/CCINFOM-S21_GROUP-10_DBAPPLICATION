package Main;

import java.sql.*;
import java.util.Scanner;
import Core_Records.Operations.*;

public class mainMenu {
  private String url = "jdbc:mysql://localhost:3306/roblox_db?serverTimezone=UTC";
  private String username = "root";
  private String password = "";
  private Scanner scan = new Scanner(System.in);

  public mainMenu(){}

  public void main() {
    displayTitle();
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      connect();
      getChoice(scan);
      /*Connection connection = DriverManager.getConnection(url, username, password);
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("select * from PLAYER");
      System.out.printf("|%-10s|%-15s|%-20s|%-25s|%-15s|\n", "Player ID", "Username", "Email", "Join Date", "Robux Balance");
      while (resultSet.next()){
        System.out.printf("|%-10s|%-15s|%-20s|%-25s|%-15s|\n", resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4), resultSet.getDouble(5));
      } 
      connection.close(); */
    } catch (Exception e) {
      System.out.println(e);
    }
  }
  private void displayTitle(){
    System.out.println("\t\t==============================================");
    System.out.println("\t\t      ROBLOX DATABASE MANAGEMENT SYSTEM      ");
    System.out.println("\t\t==============================================");
  }

  private void connect(){
    try{
      System.out.print("Connecting to Roblox Database");
      Thread.sleep(580);
      System.out.print(".");
      Thread.sleep(580);
      System.out.print(".");
      Thread.sleep(580);
      System.out.println(".");
      Thread.sleep(1300);
      System.out.println("Connection Succesful!");
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private void displayMenu(){
    System.out.println("\nMain Menu");
    System.out.println("[1] Player Record\n[2] Game Record\n[3] Item Record\n[4] Rate Record\n[5] Transactions\n[6] Reports\n[7] Exit");
  }

  public void getChoice(Scanner scan){
    int choice = 0;
    while(true){
      displayMenu();
      System.out.print("Enter your choice: ");
      if(scan.hasNextInt()){ // check if input is an integer
        choice = scan.nextInt();   
        if(choice >= 1 && choice <= 7){
          break; // choice is valid
        } else {
            System.out.println("Please enter a number between 1 and 7.\n");
        }
      } else {
        System.out.println("Invalid input! Enter a number.\n");
          scan.next(); // consume the invalid input
      }
    }
    callChoice(choice);
  }

  private void callChoice(int choice){
    switch (choice) {
      case 1:
        playerRecord();
        break;
      case 2:
        gameRecord();
        break;
      case 3:
        itemRecord();
        break;
      case 4:
        rateRecord();
        break;
      default:
        System.out.println("Exiting the program, goodbye.");
        break;
    }
  }

  
  private void playerRecord(){
    PlayerUI player = new PlayerUI(url, username, password);
    player.menu();
  }

  private void itemRecord(){
    ItemUI Item = new ItemUI(url, username, password);
    Item.menu();
  }
  private void gameRecord() {
    GameUI game = new GameUI(url, username, password);
    game.menu();
  }

  private void rateRecord(){
    RateRecord.rateMenu(scan);
  }
}
