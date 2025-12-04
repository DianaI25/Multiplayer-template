import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClientGUI {
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private PrintWriter out;

    public ChatClientGUI(String serverAddress, int port) {
        this.setupUI();
        this.connectToServer(serverAddress, port);
    }

    private void setupUI() {
        this.frame = new JFrame("Java Chat Client");
        this.frame.setSize(400, 300);
        this.frame.setDefaultCloseOperation(3);
        this.messageArea = new JTextArea();
        this.messageArea.setEditable(false);
        this.messageArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(this.messageArea);
        this.inputField = new JTextField();
        this.sendButton = new JButton("Send");
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(this.inputField, "Center");
        inputPanel.add(this.sendButton, "East");
        this.frame.add(scrollPane, "Center");
        this.frame.add(inputPanel, "South");
        this.frame.setVisible(true);
        ActionListener sendAction = (e) -> {
            String text = this.inputField.getText().trim();
            if (!text.isEmpty()) {
                this.out.println(text);
                this.inputField.setText("");
            }

        };
        this.inputField.addActionListener(sendAction);
        this.sendButton.addActionListener(sendAction);
    }

    private void connectToServer(String serverAddress, int port) {
        try {
            Socket socket = new Socket(serverAddress, port);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            (new Thread(() -> {
                String msg;
                try {
                    while ((msg = in.readLine()) != null) {
                        this.messageArea.append(msg + "\n");
                    }
                } catch (IOException var4) {
                    this.messageArea.append("Disconnected from server.\n");
                }

            })).start();
        } catch (IOException var5) {
            JOptionPane.showMessageDialog(this.frame, "Unable to connect to server.");
            System.exit(0);
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClientGUI("localhost", 12345));
    }
}