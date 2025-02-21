import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.*;

public abstract class BaseUI extends JFrame {
    protected static final int WIDTH = 300;
    protected static final int HEIGHT = 500;
    protected static final int NAV_ICON_SIZE = 20;
    private static final int IMAGE_SIZE = WIDTH / 3; // Size for each image in the grid


    public BaseUI(String title) {
        setTitle(title);
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    protected JPanel createNavigationPanel() {
        JPanel navigationPanel = new JPanel();
        navigationPanel.setBackground(new Color(249, 249, 249));
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        navigationPanel.add(createIconButton("img/icons/home.png", "home"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/search.png", "explore"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/add.png", "add"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/heart.png", "notification"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/profile.png", "profile"));

        return navigationPanel;
    }

    protected JButton createIconButton(String iconPath, String buttonType) {
        ImageIcon iconOriginal = new ImageIcon(iconPath);
        Image iconScaled = iconOriginal.getImage().getScaledInstance(NAV_ICON_SIZE, NAV_ICON_SIZE, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(iconScaled));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);

        button.addActionListener(e -> navigateToScreen(buttonType));
        return button;
    }

    protected void navigateToScreen(String screenType) {
        JFrame newScreen = null;
        switch (screenType) {
            case "home":
                newScreen = new QuakstagramHomeUI();
                break;
            case "profile":
                newScreen = new InstagramProfileUI(new User(readLoggedInUsername()));
                break;
            case "notification":
                newScreen = new NotificationsUI();
                break;
            case "explore":
                newScreen = new ExploreUI();
                break;
            case "add":
                newScreen = new ImageUploadUI();
                break;
        }

        if (newScreen != null) {
            this.dispose();
            newScreen.setVisible(true);
        }
    }

    protected String readLoggedInUsername() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
            String line = reader.readLine();
            return line != null ? line.split(":")[0].trim() : "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    protected JPanel createHeaderPanel(String headerText) {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(51, 51, 51));
        JLabel lblHeader = new JLabel(headerText + " 🐥");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        headerPanel.setPreferredSize(new Dimension(WIDTH, 40));
        return headerPanel;
    }
} 