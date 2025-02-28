import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageUploadUI extends NavigateUI {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;

    private JLabel imagePreviewLabel;
    private JTextArea captionTextArea;
    private JButton chooseImageButton;
    private JButton publishButton;
    private File selectedFile;

    public ImageUploadUI() {
        super("Image Upload");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeUI();


        setVisible(true);
    }

    private void initializeUI() {
        JPanel headerPanel = createHeaderPanel();

        JPanel navigationPanel = createNavigationPanel();

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("New Post");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.DARK_GRAY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        imagePreviewLabel = new JLabel("No image selected", SwingConstants.CENTER);

        imagePreviewLabel.setPreferredSize(new Dimension(WIDTH - 50, 180));
        imagePreviewLabel.setMaximumSize(new Dimension(WIDTH - 50, 180));
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        imagePreviewLabel.setBackground(Color.WHITE);
        imagePreviewLabel.setOpaque(true);
        imagePreviewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        captionTextArea = new JTextArea("Enter a caption");
        captionTextArea.setLineWrap(true);
        captionTextArea.setWrapStyleWord(true);
        captionTextArea.setForeground(Color.GRAY);
        captionTextArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        captionTextArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        captionTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        captionTextArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (captionTextArea.getText().equals("Enter a caption")) {
                    captionTextArea.setText("");
                    captionTextArea.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (captionTextArea.getText().trim().isEmpty()) {
                    captionTextArea.setText("Enter a caption");
                    captionTextArea.setForeground(Color.GRAY);
                }
            }
        });
        JScrollPane captionScrollPane = new JScrollPane(captionTextArea);
        captionScrollPane.setPreferredSize(new Dimension(WIDTH - 50, 80));
        captionScrollPane.setMaximumSize(new Dimension(WIDTH - 50, 80));
        captionScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        chooseImageButton = new JButton("Choose Image");
        styleButton(chooseImageButton);
        chooseImageButton.addActionListener(this::chooseImageAction);
        chooseImageButton.setAlignmentX(Component.CENTER_ALIGNMENT);


        publishButton = new JButton("Publish");
        styleButton(publishButton);

        publishButton.setBackground(new Color(255, 90, 95));
        publishButton.setForeground(Color.WHITE);
        publishButton.addActionListener(this::publishAction);
        publishButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(imagePreviewLabel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(captionScrollPane);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(chooseImageButton);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(publishButton);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.SOUTH);
    }

    private void chooseImageAction(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an image file");
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg");
        fileChooser.addChoosableFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            // Показываем превью
            updateImagePreview(selectedFile);
        }
    }


    private void publishAction(ActionEvent event) {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this,
                    "Please choose an image before publishing!",
                    "No Image Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String caption = captionTextArea.getText().trim();
        if (caption.equals("Enter a caption")) {
            caption = "";
        }

        try {
            String username = readUserNameFromFile();

            int imageId = getNextImageId(username);

            String fileExtension = getFileExtension(selectedFile);

            String newFileName = username + "_" + imageId + "." + fileExtension;

            Path destPath = Paths.get("img", "uploaded", newFileName);
            Files.copy(selectedFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

            saveImageInfo(username + "_" + imageId, username, caption);

            JOptionPane.showMessageDialog(this,
                    "Post published successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            selectedFile = null;
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("No image selected");
            captionTextArea.setText("Enter a caption");
            captionTextArea.setForeground(Color.GRAY);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error publishing post: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void updateImagePreview(File file) {
        ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());

        int labelWidth = imagePreviewLabel.getWidth();
        int labelHeight = imagePreviewLabel.getHeight();


        if (labelWidth > 0 && labelHeight > 0) {
            Image originalImage = imageIcon.getImage();
            int imageWidth = originalImage.getWidth(null);
            int imageHeight = originalImage.getHeight(null);

            double widthRatio = (double) labelWidth / imageWidth;
            double heightRatio = (double) labelHeight / imageHeight;
            double scale = Math.min(widthRatio, heightRatio);

            int scaledWidth = (int) (scale * imageWidth);
            int scaledHeight = (int) (scale * imageHeight);

            Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImage);
        }
        imagePreviewLabel.setIcon(imageIcon);
        imagePreviewLabel.setText(null);
        revalidate();
        repaint();
    }


    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setBackground(new Color(230, 230, 230));
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        button.setUI(new RoundedButtonUI(10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }


    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(51, 51, 51));

        JLabel lblTitle = new JLabel(" Upload Image \uD83D\uDC25");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        headerPanel.add(lblTitle);
        headerPanel.setPreferredSize(new Dimension(WIDTH, 40));
        return headerPanel;
    }

    private int getNextImageId(String username) throws IOException {
        Path storageDir = Paths.get("img", "uploaded");
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }

        int maxId = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(storageDir, username + "_*")) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                int idEndIndex = fileName.lastIndexOf('.');
                if (idEndIndex != -1) {
                    String idStr = fileName.substring(username.length() + 1, idEndIndex);
                    try {
                        int id = Integer.parseInt(idStr);
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return maxId + 1;
    }


    private void saveImageInfo(String imageId, String username, String bio) throws IOException {
        Path infoFilePath = Paths.get("img", "image_details.txt");
        if (!Files.exists(infoFilePath)) {
            Files.createFile(infoFilePath);
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (BufferedWriter writer = Files.newBufferedWriter(infoFilePath, StandardOpenOption.APPEND)) {
            writer.write(String.format("ImageID: %s, Username: %s, Bio: %s, Timestamp: %s, Likes: 0",
                    imageId, username, bio, timestamp));
            writer.newLine();
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf + 1);
    }


    private static class RoundedButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
        private final int radius;
        public RoundedButtonUI(int radius) {
            this.radius = radius;
        }
        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            AbstractButton b = (AbstractButton) c;
            int width = b.getWidth();
            int height = b.getHeight();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(b.getBackground());
            g2.fillRoundRect(0, 0, width, height, radius, radius);

            FontMetrics fm = g2.getFontMetrics();
            Rectangle stringBounds = fm.getStringBounds(b.getText(), g2).getBounds();
            int textX = (width - stringBounds.width) / 2;
            int textY = (height - stringBounds.height) / 2 + fm.getAscent();

            g2.setColor(b.getForeground());
            g2.drawString(b.getText(), textX, textY);
            g2.dispose();
        }
        @Override
        public Dimension getPreferredSize(JComponent c) {
            Dimension d = super.getPreferredSize(c);
            d.width = Math.max(d.width, d.height);
            return d;
        }
    }
}