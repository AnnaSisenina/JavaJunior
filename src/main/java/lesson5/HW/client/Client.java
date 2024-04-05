package lesson5.HW.client;

import lesson5.HW.network.TCPConnectionListener;
import lesson5.HW.network.TCPConnection;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class Client {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }
}
class ClientWindow extends JFrame implements TCPConnectionListener {
    private TCPConnection tcpConnection;
    private boolean isConnected = false;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 500;

    private final JPanel panelTop = new JPanel(new GridLayout(2, 3));
    private final JTextArea log = new JTextArea();
    private final JTextArea users = new JTextArea();
    private final JTextField tfIPAddress = new JTextField("127.0.0.1");
    private final JTextField tfPort = new JTextField("8000");
    private final JTextField fieldNickname = new JTextField("Nickname");
    private final JButton btnLogin = new JButton("Login");
    private final JButton btnLogout= new JButton("Logout");
    private final JPanel panelBottom = new JPanel(new BorderLayout());
    private final JTextField fieldInput = new JTextField();
    private final JButton btnSend = new JButton("Send");


    public ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setTitle("Chat");

        log.setEditable(false);
        users.setEditable(false);
        JScrollPane scrolling = new JScrollPane(log);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrolling, users);
        splitPane.setResizeWeight(0.75);
        add(splitPane, BorderLayout.CENTER);

        panelTop.add(tfIPAddress);
        panelTop.add(tfPort);
        panelTop.add(fieldNickname);
        panelTop.add(btnLogin);
        panelTop.add(btnLogout);
        add(panelTop, BorderLayout.NORTH);

        panelBottom.add(fieldInput, BorderLayout.CENTER);
        panelBottom.add(btnSend, BorderLayout.EAST);
        add(panelBottom, BorderLayout.SOUTH);

        setVisible(true);

        btnLogin.addActionListener(e -> {
            if (!isConnected) {
                int port = Integer.parseInt(tfPort.getText());
                String ipAddress = tfIPAddress.getText();
                connectToServer(ipAddress, port);
            }
        });
        btnLogout.addActionListener(e -> {
            if (isConnected) {
                onDisconnect(tcpConnection);
            }
        });
        btnSend.addActionListener(e -> {
            String msg = fieldInput.getText();
            if (msg.equals("")) return;
            fieldInput.setText(null);
            String message = (fieldNickname.getText() + ": " + msg);
            tcpConnection.sendString(message);
        });
        fieldInput.addActionListener(e -> {
            String msg = fieldInput.getText();
            if (msg.equals("")) return;
            fieldInput.setText(null);
            String message = (fieldNickname.getText() + ": " + msg);
            tcpConnection.sendString(message);
        });
    }

    private void connectToServer(String ipAddress, int port)  {
        try {
            tcpConnection = new TCPConnection(this, ipAddress, port);
            isConnected = true;
        } catch (IOException e) {
            this.onException(tcpConnection, e);
        }
        if (tcpConnection != null) {
            tcpConnection.sendString(fieldNickname.getText());
        }
    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        log.append("Подключение " + tcpConnection.toString() + "\n");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        if (value.startsWith("/List of users:")) printUsers(value);
        else printText(value);
    }


    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        isConnected = false;
        tcpConnection.disconnect();
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printText("Ошибка подключения: " + e);
    }

    private void printText(String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(text + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
    private void printUsers(String value) {
        value = value.replace("@", "\n");
        users.setText(null);
        users.append(value.substring(1));
    }
}


