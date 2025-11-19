package Core_Records.Operations;

import Core_Records.Player;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Scanner;

public class PlayerUI {

    private final String url;
    private final String username;
    private final String password;
    private final PlayerDAO dao;
    private final Scanner scan;

    public PlayerUI(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.dao = new PlayerDAO(url, username, password);
        this.scan = new Scanner(System.in);
    }

    public void menu() {
        while (true) {
            displayMenu();
            int choice = getChoice();
            if (choice == 6) break; // Back
            handleChoice(choice);
        }
    }

    private void displayMenu() {
        System.out.println("\nPlayer Menu");
        System.out.println("[1] Add Player\n[2] View Player\n[3] List Players\n[4] Update Player\n[5] Delete Player\n[6] Back");
    }

    private int getChoice() {
        while (true) {
            System.out.print("Enter your choice: ");
            if (scan.hasNextInt()) {
                int c = scan.nextInt();
                scan.nextLine(); // consume newline
                if (c >= 1 && c <= 6) return c;
            } else {
                scan.next(); // consume invalid token
            }
            System.out.println("Please enter a valid number (1-6).");
        }
    }

    private void handleChoice(int choice) {
        try {
            switch (choice) {
                case 1: addPlayer(); break;
                case 2: viewPlayer(); break;
                case 3: listPlayers(); break;
                case 4: updatePlayer(); break;
                case 5: deletePlayer(); break;
                default: System.out.println("Invalid option."); break;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace(); // remove in production
        }
    }

    private void addPlayer() throws Exception {
        Player p = new Player();
        System.out.print("Username: ");
        p.setUsername(scan.nextLine().trim());
        System.out.print("Email: ");
        p.setEmail(scan.nextLine().trim());
        p.setJoinDate(new Timestamp(System.currentTimeMillis()));
        System.out.print("Initial Robux balance (e.g., 100.50) or leave blank: ");
        String balStr = scan.nextLine().trim();
        BigDecimal bal = balStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(balStr);
        p.setRobuxBalance(bal);

        if (!p.isValidForInsert()) {
            System.out.println("Invalid input (username/email). Player not added.");
            return;
        }
        int newId = dao.insert(p);
        System.out.println("Player added with ID: " + newId);
    }

    private void viewPlayer() throws Exception {
        System.out.print("Enter player ID to view: ");
        int id = Integer.parseInt(scan.nextLine().trim());
        Player p = dao.findById(id);
        if (p == null) {
            System.out.println("Player not found.");
            return;
        }
        System.out.println("ID: " + p.getPlayerID());
        System.out.println("Username: " + p.getUsername());
        System.out.println("Email: " + p.getEmail());
        System.out.println("Join Date: " + p.getJoinDate());
        System.out.println("Robux Balance: " + p.getRobuxBalance());
    }

    private void listPlayers() throws Exception {
        List<Player> list = dao.findAll();
        if (list.isEmpty()) {
            System.out.println("No players found.");
            return;
        }
        System.out.printf("%-6s %-20s %-25s %-20s %-10s\n", "ID", "Username", "Email", "JoinDate", "Balance");
        for (Player p : list) {
            System.out.printf("%-6d %-20s %-25s %-20s %-10s\n",
                    p.getPlayerID(), p.getUsername(), p.getEmail(),
                    p.getJoinDate(), p.getRobuxBalance());
        }
    }

    private void updatePlayer() throws Exception {
        System.out.print("Enter player ID to update: ");
        int id = Integer.parseInt(scan.nextLine().trim());
        Player p = dao.findById(id);
        if (p == null) {
            System.out.println("Player not found.");
            return;
        }

        System.out.print("New Username (leave blank to keep '" + p.getUsername() + "'): ");
        String name = scan.nextLine().trim();
        if (!name.isEmpty()) p.setUsername(name);

        System.out.print("New Email (leave blank to keep '" + p.getEmail() + "'): ");
        String email = scan.nextLine().trim();
        if (!email.isEmpty()) p.setEmail(email);

        System.out.print("New Robux balance (editing balance is not allowed here; use Transactions menu to credit/debit): ");
        String bal = scan.nextLine().trim();
        if (!bal.isEmpty()) {
            System.out.println("Direct balance editing is disabled. Use Transactions -> Credit/Debit instead.");
        } 

        dao.update(p);
        System.out.println("Player updated.");
    }

    private void deletePlayer() throws Exception {
        System.out.print("Enter player ID to delete: ");
        int id = Integer.parseInt(scan.nextLine().trim());
        dao.delete(id);
        System.out.println("Player deleted (if existed).");
    }
}