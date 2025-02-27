import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.border.*;

public abstract class BaseUI extends JFrame {
    protected static final int WIDTH = 300;
    protected static final int HEIGHT = 500;
    protected static final int NAV_ICON_SIZE = 20;
    private static final int IMAGE_SIZE = WIDTH / 3;
    protected static final Font MAIN_FONT = new Font("Arial", Font.PLAIN, 14);
    protected static final Font BOLD_FONT = new Font("Arial", Font.BOLD, 14);
    protected static final Color PRIMARY_COLOR = new Color(255, 90, 95);
    protected static final Color SECONDARY_COLOR = new Color(51, 51, 51);
    protected final UIComponentFactory componentFactory;

    public BaseUI(String title) {
        setTitle(title);
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        this.componentFactory = new UIComponentFactory();
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

    protected static class DialogUtils {
        private DialogUtils() {}

        public static void showWarning(Component parent, String message) {
            JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
        }

        public static void showError(Component parent, String message) {
            JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected static class NavigationUtils {
        private NavigationUtils() {}

        public static void navigateTo(JFrame current, Supplier<JFrame> nextScreen) {
            current.dispose();
            SwingUtilities.invokeLater(() -> nextScreen.get().setVisible(true));
        }
    }

    protected static class UIComponentFactory {
        public JPanel createPanel(int padding) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
            panel.setBackground(Color.WHITE);
            return panel;
        }

        public JLabel createLogo(String path, int width, int height) {
            ImageIcon logo = new ImageIcon(
                new ImageIcon(path).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)
            );
            JLabel label = new JLabel(logo);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            return label;
        }

        public void setupTextField(JTextField field, String placeholder, Dimension size) {
            styleField(field, size);
            field.setText(placeholder);
            addPlaceholderBehavior(field, placeholder, false);
        }

        public void setupPasswordField(JPasswordField field, String placeholder, Dimension size) {
            styleField(field, size);
            field.setEchoChar((char) 0);
            field.setText(placeholder);
            addPlaceholderBehavior(field, placeholder, true);
        }

        private void styleField(JTextField field, Dimension size) {
            field.setFont(MAIN_FONT);
            field.setForeground(Color.GRAY);
            field.setPreferredSize(size);
            field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        }

        private void addPlaceholderBehavior(JTextField field, String placeholder, boolean isPassword) {
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    String currentText = isPassword ? 
                        String.valueOf(((JPasswordField)field).getPassword()) : 
                        field.getText();
                        
                    if (currentText.equals(placeholder)) {
                        field.setText("");
                        if (isPassword) {
                            ((JPasswordField)field).setEchoChar('●');
                        }
                        field.setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    String currentText = isPassword ? 
                        String.valueOf(((JPasswordField)field).getPassword()) : 
                        field.getText();
                        
                    if (currentText.isEmpty()) {
                        if (isPassword) {
                            ((JPasswordField)field).setEchoChar((char) 0);
                        }
                        field.setText(placeholder);
                        field.setForeground(Color.GRAY);
                    }
                }
            });
        }

        public JPanel createButtonPanel(ButtonConfig... configs) {
            JPanel panel = new JPanel(new GridLayout(configs.length, 1, 0, 10));
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
            
            for (ButtonConfig config : configs) {
                JButton button = new JButton(config.getText());
                styleButton(button, config);
                panel.add(button);
            }
            
            return panel;
        }

        private void styleButton(JButton button, ButtonConfig config) {
            button.setFont(BOLD_FONT);
            button.setBackground(config.getBackground());
            button.setForeground(config.getForeground());
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            button.addActionListener(config.getAction());
            button.addMouseListener(new HoverAdapter(config.getBackground()));
        }
    }

    protected static class ButtonConfig {
        private final String text;
        private final Color bgColor;
        private final Color fgColor;
        private final ActionListener action;

        public ButtonConfig(String text, Color bgColor, Color fgColor, ActionListener action) {
            this.text = text;
            this.bgColor = bgColor;
            this.fgColor = fgColor;
            this.action = action;
        }

        public String getText() { return text; }
        public Color getBackground() { return bgColor; }
        public Color getForeground() { return fgColor; }
        public ActionListener getAction() { return action; }
    }

    protected static class HoverAdapter extends MouseAdapter {
        private final Color originalColor;

        public HoverAdapter(Color originalColor) {
            this.originalColor = originalColor;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            ((JButton)e.getSource()).setBackground(originalColor.darker());
        }

        @Override
        public void mouseExited(MouseEvent e) {
            ((JButton)e.getSource()).setBackground(originalColor);
        }
    }
}