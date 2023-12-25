package cv.api;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class DynamicTwoColumnLayout extends JFrame {

    public DynamicTwoColumnLayout() {
        setTitle("Dynamic Two-Column Layout");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        // Create a panel with a GridLayout and add it to a JScrollPane
        JPanel gridPanel = new JPanel(new GridLayout(0, 2));
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        add(scrollPane);

        // Add content dynamically
        for (int i = 0; i < 20; i++) { // Replace 100 with the number of rows you need
            JPanel rowPanel = createRowPanel("https://img.freepik.com/free-psd/silver-sedan-car_53876-84522.jpg?w=1060&t=st=1703317658~exp=1703318258~hmac=8a1c73389fde82cb7b1cf4f67be37d753f29dce9664e1783497564bac5603d87", "Content " + i,100,100);
            gridPanel.add(rowPanel);
        }

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createRowPanel(String imageUrl, String content, int width, int height) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new FlowLayout());

        try {
            URL url = new URL(imageUrl);
            Image originalImage = ImageIO.read(url);

            // Resize the image
            Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            // Create an ImageIcon with the resized image
            ImageIcon imageIcon = new ImageIcon(resizedImage);
            JLabel imageLabel = new JLabel(imageIcon);
            rowPanel.add(imageLabel);
        } catch (IOException e) {
            // Log the exception or handle it as needed
            e.printStackTrace();

            // Display a placeholder image
            ImageIcon placeholderIcon = new ImageIcon("path/to/placeholder.jpg");
            JLabel placeholderLabel = new JLabel(placeholderIcon);
            rowPanel.add(placeholderLabel);
        }

        // Add content label
        JLabel contentLabel = new JLabel(content);
        rowPanel.add(contentLabel);

        return rowPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DynamicTwoColumnLayout());
    }
}
