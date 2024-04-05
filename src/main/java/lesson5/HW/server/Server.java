package lesson5.HW.server;

import lesson5.HW.network.TCPConnectionListener;
import lesson5.HW.network.TCPConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class Server implements TCPConnectionListener {
    public static final int PORT = 8000;
    private final Map<String, TCPConnection> users = new HashMap<>();
    private Server() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("������ ������� �� �����: " + PORT);
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.err.println("��������� ������ ��� ������������� � ��������" + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("�� ������� ���������� ����" + PORT, e);
        }
    }

    public static void main(String[] args) {
        new Server();
    }
    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        String newUser = tcpConnection.getNickname();
        if (users.containsKey(newUser)) {
            tcpConnection.sendString("������������ � ����� ������ ��� ����");
            tcpConnection.disconnect();
        }
        users.put(newUser, tcpConnection);
        System.out.println("����������� ����� ������: " + tcpConnection);
        sendToALlUsers("����������� ����� ������: " + newUser);
        sendUpdatedUsersList();
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String message) {
        if (message != null) {
            if (message.substring(message.indexOf(" ")+1).startsWith("@"))
                sendPrivateMessage(message);
            else sendToALlUsers(message);
        }
    }
    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        users.remove(tcpConnection.getNickname());
        System.out.println("���������� ������: " + tcpConnection.toString());
        sendToALlUsers("���������� ������: " + tcpConnection.getNickname());
        sendUpdatedUsersList();
    }
    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("��������� ������ ��� �������������� � ��������: "+ tcpConnection.toString() + " " + e.getMessage());
    }

    private void sendUpdatedUsersList() {
        StringBuilder usersList = new StringBuilder("/List of users:@");
        users.keySet().forEach(key -> usersList.append(key).append("@"));
        sendToALlUsers(usersList.toString());
    }

    private synchronized void sendToALlUsers(String text) {
        users.forEach((key, value) -> value.sendString(text));
    }

    private void sendPrivateMessage(String message) {
        int firstSpaceIndex = message.indexOf(" ");
        int secondSpaceIndex = message.indexOf(" ", firstSpaceIndex + 1);
        String receiver = message.substring(firstSpaceIndex+2, secondSpaceIndex);
        String sender = message.substring(0, firstSpaceIndex-1);
        users.entrySet().stream().
                filter(it -> (it.getKey().equals(receiver)) || (it.getKey().equals(sender)))
                .forEach(it -> it.getValue().sendString(message));
    }

}
