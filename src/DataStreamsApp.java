import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class DataStreamsApp extends JFrame {
    private JTextArea originalText;
    private JTextArea filteredText;
    private JTextField searchField;
    private JButton loadButton, searchButton, quitButton;
    
    public DataStreamsApp() {
        // Set up the main frame
        setTitle("Data Streams App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize text areas and set them to be scrollable
        originalText = new JTextArea();
        originalText.setEditable(false);
        JScrollPane originalScrollPane = new JScrollPane(originalText);
        
        filteredText = new JTextArea();
        filteredText.setEditable(false);
        JScrollPane filteredScrollPane = new JScrollPane(filteredText);

        // Set up the panel for input and buttons
        JPanel controlPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        // Add components to control panel
        controlPanel.add(new JLabel("Search:"));
        controlPanel.add(searchField);
        controlPanel.add(loadButton);
        controlPanel.add(searchButton);
        controlPanel.add(quitButton);

        // Add components to the frame
        add(controlPanel, BorderLayout.NORTH);
        add(originalScrollPane, BorderLayout.WEST);
        add(filteredScrollPane, BorderLayout.EAST);

        // Set up button actions
        loadButton.addActionListener(new LoadFileAction());
        searchButton.addActionListener(new SearchFileAction());
        quitButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    // LoadFileAction class with additional debugging and alternative loading method
    private class LoadFileAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(DataStreamsApp.this);

            if (result == JFileChooser.APPROVE_OPTION) {
                Path filePath = fileChooser.getSelectedFile().toPath();
                System.out.println("Selected file path: " + filePath);

                if (!Files.exists(filePath)) {
                    System.out.println("Error: File does not exist at the selected path.");
                    JOptionPane.showMessageDialog(DataStreamsApp.this, "Error: File does not exist.");
                    return;
                }
                if (!Files.isReadable(filePath)) {
                    System.out.println("Error: File is not readable. Check file permissions.");
                    JOptionPane.showMessageDialog(DataStreamsApp.this, "Error: File is not readable.");
                    return;
                }

                originalText.setText("");  // Clear existing text in the JTextArea

                try {
                    List<String> allLines = Files.readAllLines(filePath);
                    System.out.println("Number of lines in file: " + allLines.size());

                    for (String line : allLines) {
                        originalText.append(line + "\n");
                    }
                    System.out.println("File loaded successfully into JTextArea.");

                    // Debug: Confirm the content in originalText
                    System.out.println("JTextArea content:\n" + originalText.getText());

                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(DataStreamsApp.this, "Error loading file: " + ex.getMessage());
                }
            } else {
                System.out.println("File selection was canceled by the user.");
            }
        }
    }

    // SearchFileAction class for filtering and displaying search results
    private class SearchFileAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchString = searchField.getText();
            if (searchString.isEmpty()) {
                JOptionPane.showMessageDialog(DataStreamsApp.this, "Please enter a search string.");
                return;
            }

            String[] lines = originalText.getText().split("\n");
            filteredText.setText("");  // Clear any existing filtered text

            Stream.of(lines)
                    .filter(line -> line.contains(searchString))
                    .forEach(line -> filteredText.append(line + "\n"));
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DataStreamsApp::new);
    }
}
