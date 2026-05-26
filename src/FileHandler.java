import java.io.*;
import java.util.*;

public class FileHandler {

    // Reads all lines from a file and returns them as a list
    public static List<String> readLines(String filename) {
        List<String> lines = new ArrayList<>();
        File file = new File(filename);

        if (!file.exists()) {
            return lines; // return empty list if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
        }

        return lines;
    }

    // Overwrites the entire file with the given list of lines
    public static void writeLines(String filename, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + filename);
        }
    }

    // Adds a single line to the end of a file without erasing existing content
    public static void appendLine(String filename, String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error appending to file: " + filename);
        }
    }
}