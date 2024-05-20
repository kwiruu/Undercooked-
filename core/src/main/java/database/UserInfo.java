package database;

public class UserInfo {
    private final int level;
    private final String username;

    public UserInfo(int level,String username) {
        this.level = level;
        this.username = username;
    }

    public int getLevel() {
        return level;
    }

    public String getUserName() {
        return username;
    }
}
