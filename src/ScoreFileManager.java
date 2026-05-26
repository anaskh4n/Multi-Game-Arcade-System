import java.util.*;

public class ScoreFileManager {

    private static final String FILE = "scores.txt";

    // Saves a new score entry to the file
    public void saveScore(String username, String gameName, int score) {
        FileHandler.appendLine(FILE, username + "," + gameName + "," + score);
    }

    // Returns all score entries for a specific user
    public List<String[]> getScoresByUser(String username) {
        List<String[]> result = new ArrayList<>();
        for (String line : FileHandler.readLines(FILE)) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",");
            if (parts[0].equals(username)) {
                result.add(parts);
            }
        }
        return result;
    }

    // Removes all score entries belonging to a specific user
    public void resetScores(String username) {
        List<String> allLines = FileHandler.readLines(FILE);
        List<String> remaining = new ArrayList<>();
        for (String line : allLines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",");
            if (!parts[0].equals(username)) {
                remaining.add(line);
            }
        }
        FileHandler.writeLines(FILE, remaining);
    }
}
