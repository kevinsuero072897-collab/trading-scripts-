import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class FetchTopstepTokenApp extends JFrame {
    private JTextArea outputArea;
    private JButton fetchButton;

    public FetchTopstepTokenApp() {
        super("Fetch Topstep Token");

        fetchButton = new JButton("Fetch Token");
        outputArea = new JTextArea(5, 30);
        outputArea.setEditable(false);

        fetchButton.addActionListener(e -> fetchToken());

        setLayout(new BorderLayout());
        add(fetchButton, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void fetchToken() {
        try {
            String scriptPath = "../aside/topstep-token-service/topstep-get-token.ps1";
            ProcessBuilder pb = new ProcessBuilder("pwsh", scriptPath);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String token = reader.readLine();
            outputArea.setText("Fetched token: " + token);
            process.waitFor();
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FetchTopstepTokenApp::new);
    }
}