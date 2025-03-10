package com.quackstagram.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class NotificationsUI extends NavigateUI {

    public NotificationsUI() {
        super("Notifications");
        initializeUI();
    }

    private void initializeUI() {
        JPanel headerPanel = createHeaderPanel("Notifications");
        JPanel navigationPanel = createNavigationPanel();

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        containerPanel.setBackground(new Color(245, 245, 245));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        containerPanel.add(scrollPane, BorderLayout.CENTER);

        String currentUsername = readLoggedInUsername();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "notifications.txt"))) {
            String line;
            boolean foundNotifications = false;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("; ");
                if (parts.length >= 4 && parts[0].trim().equals(currentUsername)) {
                    addNotificationToPanel(contentPanel, parts);
                    foundNotifications = true;
                }
            }
            
            if (!foundNotifications) {
                JLabel noNotificationsLabel = new JLabel("No notifications yet");
                noNotificationsLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
                noNotificationsLabel.setForeground(Color.GRAY);
                noNotificationsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                noNotificationsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                contentPanel.add(noNotificationsLabel);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error loading notifications");
            errorLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
            errorLabel.setForeground(Color.RED);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            contentPanel.add(errorLabel);
        }

        add(headerPanel, BorderLayout.NORTH);
        add(containerPanel, BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.SOUTH);
    }

    private void addNotificationToPanel(JPanel contentPanel, String[] parts) {
        String userWhoLiked = parts[1].trim();
        String imageId = parts[2].trim();
        String timestamp = parts[3].trim();
        String notificationMessage = "<html><b>" + userWhoLiked + "</b> liked your picture<i>" + getElapsedTime(timestamp);

        JPanel notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        notificationPanel.setBackground(Color.WHITE);
        notificationPanel.setPreferredSize(new Dimension(280, 60));
        notificationPanel.setBackground(Color.WHITE);

        JLabel notificationLabel = new JLabel("<html>" + notificationMessage + "</html>");
        notificationLabel.setPreferredSize(new Dimension(250, 50));
        notificationLabel.setVerticalAlignment(SwingConstants.TOP);
        notificationLabel.setHorizontalAlignment(SwingConstants.LEFT);
        notificationLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        notificationLabel.setForeground(new Color(50, 50, 50));

        notificationPanel.add(notificationLabel, BorderLayout.CENTER);

        contentPanel.add(notificationPanel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private String getElapsedTime(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timeOfNotification = LocalDateTime.parse(timestamp, formatter);
        LocalDateTime currentTime = LocalDateTime.now();

        Duration duration = Duration.between(timeOfNotification, currentTime);
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        
        String timeText;
        
        if (days > 0) {
            timeText = String.format("%d day%s ago", days, days > 1 ? "s" : "");
        } else if (hours > 0) {
            timeText = String.format("%d hour%s ago", hours, hours > 1 ? "s" : "");
        } else if (minutes > 0) {
            timeText = String.format("%d minute%s ago", minutes, minutes > 1 ? "s" : "");
        } else {
            timeText = "just now";
        }

        return "<font color='#888888'>- " + timeText + "</font>";
    }
}
