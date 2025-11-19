package Core_Records.Operations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Core_Records.Game;

public class GameDao {

    private String url;
    private String username;
    private String password;

    public GameDao(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    // CREATE
    public void addGame(Game game) throws SQLException {
        String sql = "INSERT INTO game (gameName, genre, activePlayers) VALUES (?,?,?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, game.getGameName());
            stmt.setString(2, game.getGenre());
            stmt.setInt(3, game.getActivePlayers());

            stmt.executeUpdate();
            System.out.println("Game added successfully!");
        }
    }

    // READ (Single Game)
    public Game getGame(int gameID) throws SQLException {
        String sql = "SELECT * FROM game WHERE gameID = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Game g = new Game();
                g.setGameID(rs.getInt("gameID"));
                g.setGameName(rs.getString("gameName"));
                g.setGenre(rs.getString("genre"));
                g.setDateCreated(rs.getTimestamp("dateCreated"));
                g.setActivePlayers(rs.getInt("activePlayers"));
                return g;
            }
        }
        return null;
    }

    // READ (All Games)
    public List<Game> getAllGames() throws SQLException {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT * FROM game";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Game g = new Game();
                g.setGameID(rs.getInt("gameID"));
                g.setGameName(rs.getString("gameName"));
                g.setGenre(rs.getString("genre"));
                g.setDateCreated(rs.getTimestamp("dateCreated"));
                g.setActivePlayers(rs.getInt("activePlayers"));
                games.add(g);
            }
        }
        return games;
    }

    // UPDATE
    public void updateGame(Game game) throws SQLException {
        String sql = "UPDATE game SET gameName=?, genre=?, activePlayers=? WHERE gameID=?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, game.getGameName());
            stmt.setString(2, game.getGenre());
            stmt.setInt(3, game.getActivePlayers());
            stmt.setInt(4, game.getGameID());

            stmt.executeUpdate();
            System.out.println("Game updated successfully!");
        }
    }

    // DELETE
    public void deleteGame(int gameID) throws SQLException {
        String sql = "DELETE FROM game WHERE gameID=?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);
            stmt.executeUpdate();
            System.out.println("Game deleted successfully!");
        }
    }

    // VIEW GAME + PLAYERS WHO PLAYED IT
    public void viewGameWithPlayers(int gameID) throws SQLException {

        Game game = getGame(gameID);

        if (game == null) {
            System.out.println("Game not found!");
            return;
        }

        System.out.println("\n===== GAME DETAILS =====");
        System.out.println("ID: " + game.getGameID());
        System.out.println("Name: " + game.getGameName());
        System.out.println("Genre: " + game.getGenre());
        System.out.println("Created: " + game.getDateCreated());
        System.out.println("Active Players: " + game.getActivePlayers());

        System.out.println("\n===== PLAYERS WHO PLAYED THIS GAME =====");

        String sql = 
            "SELECT DISTINCT p.playerID, p.username, p.email " +
            "FROM PLAYER p " +
            "JOIN SESSION_ACTIVITY s ON p.playerID = s.playerID " +
            "WHERE s.gameID = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();

            boolean hasPlayers = false;

            while (rs.next()) {
                hasPlayers = true;
                System.out.printf("| %-5d | %-20s | %-25s |\n",
                    rs.getInt("playerID"),
                    rs.getString("username"),
                    rs.getString("email"));
            }

            if (!hasPlayers) {
                System.out.println("No players have played this game yet.");
            }
        }
    }
}
