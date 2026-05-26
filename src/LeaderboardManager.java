import java.util.*;

public class LeaderboardManager {

    private static final String FILE = "scores.txt";
    private List<String[]> allScores = new ArrayList<>();

    // Re-reads the scores file and refreshes the internal list
    public void updateLeaderboard() {
        allScores.clear();
        for (String line : FileHandler.readLines(FILE)) {
            if (!line.trim().isEmpty()) {
                allScores.add(line.split(","));
            }
        }
    }

    // Returns top 'limit' scores for a game, sorted highest first
    // Pass "Overall" to get top scores across all games
    public List<String[]> getTopScores(String gameName, int limit) {
        updateLeaderboard();

        List<String[]> filtered = new ArrayList<>();
        for (String[] entry : allScores) {
            if (gameName.equals("Overall") || entry[1].equals(gameName)) {
                filtered.add(entry);
            }
        }

        // Sort by score descending (entry[2] is the score)
        filtered.sort((a, b) -> Integer.parseInt(b[2]) - Integer.parseInt(a[2]));

        // Return only up to 'limit' entries
        return filtered.subList(0, Math.min(limit, filtered.size()));
    }
}