package Core_Records.Operations;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import Core_Records.Game;

public class GameUI {

    private String url;
    private String username;
    private String password;
    private GameDao dao;
    private Game game = new Game();
    private Scanner scan;

    public GameUI(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
        this.dao = new GameDao(url, username, password);
        this.scan = new Scanner(System.in);
    }

    public void menu(){
        int choice = 0;
        while (true) {
            choice = getChoice();
            if (choice == 7) break;
            callChoice(choice);
        }
    }

    public void displayMenu(){
        System.out.println("\nGame Menu");
        System.out.println("[1] Add Game");
        System.out.println("[2] View Game");
        System.out.println("[3] List Games");
        System.out.println("[4] Update Game");
        System.out.println("[5] Delete Game");
        System.out.println("[6] View Game + Players");
        System.out.println("[7] Back");
    }

    public int getChoice(){
        int choice = 0;
        while (true) {
            displayMenu();
            System.out.print("Enter your choice: ");

            if (scan.hasNextInt()) {
                choice = scan.nextInt();
                if (choice >= 1 && choice <= 7) {
                    break;
                } else {
                    System.out.println("Please enter a number between 1 and 7.\n");
                }
            } else {
                System.out.println("Invalid input! Enter a number.\n");
                scan.next(); 
            }
        }
        return choice;
    }

    public void callChoice(int choice){
        switch(choice){
            case 1 -> addGame();
            case 2 -> viewGame();
            case 3 -> listGames();
            case 4 -> updateGame();
            case 5 -> deleteGame();
            case 6 -> viewGameWithPlayers();
            default -> { }
        }
    }


    public void addGame(){
        try {
            scan.nextLine(); 
            System.out.println("\nAdd Game");
            System.out.print("Game Name: ");
            String name = scan.nextLine();

            System.out.print("Genre: ");
            String genre = scan.nextLine();

            System.out.print("Active Players: ");
            int active = Integer.parseInt(scan.nextLine());

            Game g = new Game(name, genre, active);
            dao.addGame(g);

            System.out.println("Game has been added.");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }


    public void viewGame(){
        try {
            System.out.print("Enter Game ID: ");
            int id = scan.nextInt();
            scan.nextLine();

            Game g = dao.getGame(id);

            if (g == null) {
                System.out.println("Game not found!");
                return;
            }

            System.out.println("\n=== GAME DETAILS ===");
            System.out.println("ID: " + g.getGameID());
            System.out.println("Name: " + g.getGameName());
            System.out.println("Genre: " + g.getGenre());
            System.out.println("Created: " + g.getDateCreated());
            System.out.println("Active Players: " + g.getActivePlayers());

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

  
    public void listGames(){
        try {
            List<Game> games = dao.getAllGames();

            System.out.println("\n=== ALL GAMES ===");
            for (Game g : games) {
                System.out.printf("[%d] %-20s | %-10s | Active: %d\n",
                    g.getGameID(),
                    g.getGameName(),
                    g.getGenre(),
                    g.getActivePlayers()
                );
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }

    public void updateGame(){
        try {
            System.out.print("Enter Game ID to update: ");
            int id = scan.nextInt();
            scan.nextLine();

            Game g = dao.getGame(id);

            if (g == null) {
                System.out.println("Game not found!");
                return;
            }

            System.out.println("\nUpdate Game");
            System.out.println("(Press ENTER to keep existing value)");

            System.out.print("New Name (" + g.getGameName() + "): ");
            String name = scan.nextLine();
            if (!name.isEmpty()) g.setGameName(name);

            System.out.print("New Genre (" + g.getGenre() + "): ");
            String genre = scan.nextLine();
            if (!genre.isEmpty()) g.setGenre(genre);

            System.out.print("New Active Players (" + g.getActivePlayers() + "): ");
            String activeStr = scan.nextLine();
            if (!activeStr.isEmpty()) g.setActivePlayers(Integer.parseInt(activeStr));

            dao.updateGame(g);
            System.out.println("Game has been updated.");

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void deleteGame(){
        try {
            System.out.print("Enter Game ID to delete: ");
            int id = scan.nextInt();
            scan.nextLine();

            dao.deleteGame(id);

            System.out.println("Game has been deleted.");

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }


    public void viewGameWithPlayers(){
        try {
            System.out.print("Enter Game ID: ");
            int id = scan.nextInt();
            scan.nextLine();

            dao.viewGameWithPlayers(id);

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
