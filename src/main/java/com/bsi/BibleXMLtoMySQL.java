package com.bsi;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.sql.*;
import org.w3c.dom.*;

public class BibleXMLtoMySQL {

    public static void main(String[] args) {
        // DB connection details
        String jdbcURL = "jdbc:mysql://localhost:3306/gateofheaven?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String dbUser = "root";
        String dbPassword = "Tkkcode@090327"; // 👈 change this

        // English book names
        String[] englishBooks = {
                "Genesis","Exodus","Leviticus","Numbers","Deuteronomy","Joshua","Judges","Ruth",
                "1 Samuel","2 Samuel","1 Kings","2 Kings","1 Chronicles","2 Chronicles","Ezra",
                "Nehemiah","Esther","Job","Psalms","Proverbs","Ecclesiastes","Song of Solomon",
                "Isaiah","Jeremiah","Lamentations","Ezekiel","Daniel","Hosea","Joel","Amos",
                "Obadiah","Jonah","Micah","Nahum","Habakkuk","Zephaniah","Haggai","Zechariah",
                "Malachi","Matthew","Mark","Luke","John","Acts","Romans","1 Corinthians",
                "2 Corinthians","Galatians","Ephesians","Philippians","Colossians",
                "1 Thessalonians","2 Thessalonians","1 Timothy","2 Timothy","Titus",
                "Philemon","Hebrews","James","1 Peter","2 Peter","1 John","2 John",
                "3 John","Jude","Revelation"
        };

        // Telugu book names
        String[] teluguBooks = {
                "ఆదికాండము","నిర్గమకాండము","లేవీయకాండము","సంఖ్యాకాండము","ద్వితీయోపదేశకాండమ","యెహొషువ",
                "న్యాయాధిపతులు","రూతు","సమూయేలు మొదటి గ్రంథము","సమూయేలు రెండవ గ్రంథము","రాజులు మొదటి గ్రంథము",
                "రాజులు రెండవ గ్రంథము","దినవృత్తాంతములు మొదటి గ్రంథము","దినవృత్తాంతములు రెండవ గ్రంథము",
                "ఎజ్రా","నెహెమ్యా","ఎస్తేరు","యోబు గ్రంథము","కీర్తనల గ్రంథము","సామెతలు","ప్రసంగి",
                "పరమగీతము","యెషయా గ్రంథము","యిర్మీయా","విలాపవాక్యములు","యెహెజ్కేలు","దానియేలు",
                "హొషేయ","యోవేలు","ఆమోసు","ఓబద్యా","యోనా","మీకా","నహూము","హబక్కూకు","జెఫన్యా",
                "హగ్గయి","జెకర్యా","మలాకీ","మత్తయి సువార్త","మార్కు సువార్త","లూకా సువార్త","యోహాను సువార్త",
                "అపొస్తలుల కార్యములు","రోమీయులకు","1 కొరింథీయులకు","2 కొరింథీయులకు","గలతీయులకు","ఎఫెసీయులకు",
                "ఫిలిప్పీయులకు","కొలొస్సయులకు","1 థెస్సలొనీకయులకు","2 థెస్సలొనీకయులకు","1 తిమోతికి","2 తిమోతికి",
                "తీతుకు","ఫిలేమోనుకు","హెబ్రీయులకు","యాకోబు","1 పేతురు","2 పేతురు","1 యోహాను","2 యోహాను",
                "3 యోహాను","యూదా","ప్రకటన గ్రంథము"
        };

        try (Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword)) {
            System.out.println("✅ Connected to MySQL.");

            // Clear old data
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("TRUNCATE TABLE bible_bsi");
                System.out.println("🗑 Cleared old data from bible_bsi");
            }

            // Parse XML
            File xmlFile = new File("C:/Users/kiran/Documents/Eclipse/bsi/src/main/java/com/bsi/bible.xml"); // 👈 Update path
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList bookList = doc.getElementsByTagName("Book");

            for (int b = 0; b < bookList.getLength(); b++) {
                Element book = (Element) bookList.item(b);
                int bookNo = b + 1;
                String bookEng = englishBooks[b];
                String bookTel = teluguBooks[b];

                NodeList chapterList = book.getElementsByTagName("Chapter");
                int totalChapters = chapterList.getLength();

                for (int c = 0; c < chapterList.getLength(); c++) {
                    Element chapter = (Element) chapterList.item(c);
                    String chapterId = chapter.getAttribute("id");

                    NodeList verseList = chapter.getElementsByTagName("Verse");

                    for (int v = 0; v < verseList.getLength(); v++) {
                        Element verse = (Element) verseList.item(v);

                        int verseId = v + 1; // resets per chapter
                        String verseText = verse.getTextContent().trim();

                        String sql = "INSERT INTO bible_bsi " +
                                "(book_no, book_id_eng, book_id_telugu, chapter_id, verse_id, verses, no_of_chapters) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";

                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setInt(1, bookNo);
                            ps.setString(2, bookEng);
                            ps.setString(3, bookTel);
                            ps.setString(4, chapterId);
                            ps.setInt(5, verseId);
                            ps.setString(6, verseText);
                            ps.setInt(7, totalChapters);
                            ps.executeUpdate();
                        }
                    }
                }
            }

            System.out.println("✅ XML data inserted successfully into bible_bsi.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
