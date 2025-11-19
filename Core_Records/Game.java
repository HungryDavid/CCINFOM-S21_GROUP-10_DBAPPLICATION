package Core_Records;

import java.sql.Timestamp;
// note to self 
public class Game {

    private int gameID;
    private String gameName;
    private String genre;
    private Timestamp dateCreated;
    private int activePlayers;

    // Empty constructor
    public Game() {}

    // Constructor for adding new games
    public Game(String gameName, String genre, int activePlayers) {
        this.gameName = gameName;
        this.genre = genre;
        this.activePlayers = activePlayers;
    }

    //  GETTERS 
    public int getGameID() {
        return gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public String getGenre() {
        return genre;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public int getActivePlayers() {
        return activePlayers;
    }

    //  SETTERS 
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setActivePlayers(int activePlayers) {
        this.activePlayers = activePlayers;
    }
}
