/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import gamestates.Gamestate;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author nimro
 */
public class Leaderboard {

    public static void saveScore(String name, int score) {
        String url = "jdbc:mysql://localhost:3306/leaderboard?serverTimezone=Europe/Budapest";
        String user = "root";
        String password = "admin";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            // Ellenőrizzük, hogy a név már létezik-e
            String checkQuery = "SELECT score FROM leaderboard WHERE name = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, name);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Ha a név már létezik, ellenőrizzük a pontszámot
                int existingScore = rs.getInt("score");
                if (score > existingScore) {
                    // Csak akkor frissítjük, ha az új pontszám nagyobb
                    String updateQuery = "UPDATE leaderboard SET score = ? WHERE name = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setInt(1, score);
                    updateStmt.setString(2, name);
                    updateStmt.executeUpdate();
                    System.out.println("Score updated for " + name);
                } else {
                    System.out.println("Score not updated; existing score is higher or equal.");
                }
            } else {
                // Ha a név nem létezik, beszúrjuk az új rekordot
                String insertQuery = "INSERT INTO leaderboard (name, score) VALUES (?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, name);
                insertStmt.setInt(2, score);
                insertStmt.executeUpdate();
                System.out.println("New score saved for " + name);
            }

            System.out.println("Score saved successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error saving score.");
        }
    }

    public static void showTopScores() {
        
        String url = "jdbc:mysql://localhost:3306/leaderboard?serverTimezone=Europe/Budapest";
        String user = "root";
        String password = "admin";

        JFrame frame = new JFrame("Top Scores");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Name", "Score"}, 0);
        JTable table = new JTable(tableModel);

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Lekérdezés a legjobb 10 pontszám lekérésére
            String query = "SELECT name, score FROM leaderboard ORDER BY score DESC LIMIT 10";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String name = rs.getString("name");
                int score = rs.getInt("score");
                tableModel.addRow(new Object[]{name, score});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error retrieving scores.");
        }

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Bezárja az aktuális ablakot
                Gamestate.state = Gamestate.MENU;
                
                
            }
        });
        frame.add(backButton, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}
