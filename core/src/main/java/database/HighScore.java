package database;

public class HighScore {
    private final String userName;
    private final int highScore;

    public HighScore(String userName, int highScore) {
        this.userName = userName;
        this.highScore = highScore;
    }

    public String getUserName() {
        return userName;
    }

    public int getHighScore() {
        return highScore;
    }
}
