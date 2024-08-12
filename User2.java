import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

class User2 extends JFrame implements ActionListener, Runnable {
    JTextPane textPane;
    JTextField textField;
    JButton button;
    Socket client;
    DataInputStream input;
    DataOutputStream output;
    Thread check;

    User2() {
        // Set up the text pane with styled document
        textPane = new JTextPane();
        textPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textPane);

        textField = new JTextField();
        button = new JButton("Send");

        button.addActionListener(this);

        try {
            client = new Socket("localhost", 2222); // Connect to the server
            input = new DataInputStream(client.getInputStream());
            output = new DataOutputStream(client.getOutputStream());
        } catch (Exception e) {
            System.out.println(e);
        }

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(textField, BorderLayout.CENTER);
        bottomPanel.add(button, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        check = new Thread(this);
        check.setDaemon(true);
        check.start();

        setSize(600, 400);
        setTitle("User2 - Chat Application");
        setVisible(true);

        // Modern window close action
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = textField.getText();
        appendToPane(textPane, "User2: " + msg + "\n", StyleConstants.ALIGN_RIGHT);
        textField.setText("");

        try {
            output.writeUTF(msg);
            output.flush();
        } catch (Exception e1) {
            System.out.println(e1);
        }
    }

    public void run() {
        while (true) {
            try {
                String msg = input.readUTF();
                appendToPane(textPane, "User1: " + msg + "\n", StyleConstants.ALIGN_RIGHT);
            } catch (Exception e) {
                // Handle exception
            }
        }
    }

    private void appendToPane(JTextPane tp, String msg, int alignment) {
        StyledDocument doc = tp.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setAlignment(attr, alignment);
        StyleConstants.setForeground(attr, Color.BLACK);
        StyleConstants.setFontSize(attr, 14);

        try {
            doc.insertString(doc.getLength(), msg, attr);
            doc.setParagraphAttributes(doc.getLength(), 1, attr, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new User2();
    }
}
