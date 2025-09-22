package read;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class BibleToTextFile {
    public static void main(String[] args) {
        // ✅ Update with your DB details
        String jdbcURL = "jdbc:mysql://localhost:3306/gateofheaven?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String dbUser = "root";   // 👈 using root
        String dbPassword = "Tkkcode@090327"; // 👈 put your root password

        // Output text file
        String outputFile = "bible_output.txt";

        try (
                // Auto-close file writer
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
                // DB connection
                Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        ) {
            System.out.println("✅ Connected to database!");

            // SQL query
            String sql = "SELECT book_no, book_id_eng, book_id_telugu, chapter_id, verse_id, verses " +
                    "FROM bible_bsi ORDER BY book_no, chapter_id, verse_id";

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);

            // Write to file
            while (result.next()) {
                String bookEng = result.getString("book_id_eng");
                String bookTel = result.getString("book_id_telugu");
                String chapter = result.getString("chapter_id");
                int verseId = result.getInt("verse_id");
                String verse = result.getString("verses");

                writer.write("📖 " + bookEng + " (" + bookTel + ") - Chapter " + chapter + ", Verse " + verseId);
                writer.newLine();
                writer.write("    " + verse);
                writer.newLine();
                writer.write("------------------------------------------------");
                writer.newLine();
            }

            System.out.println("✅ Bible data exported to " + outputFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
