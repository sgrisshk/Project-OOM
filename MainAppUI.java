import java.awt.*;
import javax.swing.*;

/**
 * Main application UI with access to the messaging system
 */
public class MainAppUI extends JFrame {
    private User currentUser;
    
    public MainAppUI(User user) {
        this.currentUser = user;
        
        // Set up the frame
        setTitle("Quackstagram - " + user.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create a simple toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        // Add buttons to toolbar
        JButton homeButton = new JButton("Home");
        JButton profileButton = new JButton("Profile");
        JButton messagesButton = new JButton("Messages");
        
        toolbar.add(homeButton);
        toolbar.add(profileButton);
        toolbar.add(messagesButton);
        
        // Add the toolbar to the top of the frame
        add(toolbar, BorderLayout.NORTH);
        
        // Add a simple content panel (just a placeholder)
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(new JLabel("Welcome to Quackstagram, " + user.getUsername() + "!"));
        add(contentPanel, BorderLayout.CENTER);
        
        // Add action listener for the messages button
        messagesButton.addActionListener(e -> {
            // Open the messages UI
            MessageUI.showMessagesFor(currentUser);
        });
    }
    
    /**
     * Start the application with a specific user
     */
    public static void startApp(User user) {
        MainAppUI app = new MainAppUI(user);
        app.setVisible(true);
    }
    
    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        // Demo user
        User demoUser = new User("demo_user", "This is a demo user", "password");
        
        // Create some other users and messages for demonstration
        User user1 = new User("user1", "User 1's bio", "pass1");
        User user2 = new User("user2", "User 2's bio", "pass2");
        
        demoUser.sendMessage(user1, "Hello User 1!");
        user1.sendMessage(demoUser, "Hi Demo User!");
        demoUser.sendMessage(user2, "Hey User 2, how's it going?");
        
        // Start the application
        SwingUtilities.invokeLater(() -> startApp(demoUser));
    }
} 