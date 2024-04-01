package lesson5.server;

import lesson5.client.Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class Server {
    public static final int PORT = 8000;


    public static void main(String[] args) {
        final Map<String, ClientHandler> clients = new HashMap<>();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("������ ������� �� �����: " + PORT);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("����������� ����� ������: " + clientSocket.toString());

                    PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
                    clientOut.println("����������� �������. ������� �������������");

                    Scanner clientIn = new Scanner(clientSocket.getInputStream());
                    String clientId = clientIn.nextLine();
                    System.out.println("������������� ������� " + clientSocket + ": " + clientId);

                    String allClients = clients.entrySet().stream()
                            .map(it -> "id = " + it.getKey() + ", client = " + it.getValue().getClientSocket())
                            .collect(Collectors.joining("\n"));
                    clientOut.println("������ ��������� ��������: \n" + allClients);

                    ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
                    new Thread(clientHandler).start();

                    for (ClientHandler client : clients.values()) {
                        client.send("����������� ����� ������: " + clientSocket + ", id = " + clientId);
                    }
                    clients.put(clientId, clientHandler);
                } catch (IOException e) {
                    System.err.println("��������� ������ ��� ������������� � ��������" + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("�� ������� ���������� ����" + PORT, e);
        }
    }
}

class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final PrintWriter out;
    private final Map<String, ClientHandler> clients;
    public ClientHandler(Socket clientSocket, Map<String, ClientHandler> clients) throws IOException{
        this.clientSocket = clientSocket;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.clients = clients;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }


    @Override
    public void run() {
        try (Scanner input = new Scanner(clientSocket.getInputStream()))
              {
           // output.println("����������� ������ �������");

            while (true) {
                if (clientSocket.isClosed()) {
                    System.out.println("������ " + clientSocket + " ����������");
                    break;
                }
                String inputFromClient = input.nextLine();
                System.out.println("�������� ��������� �� ������� " + clientSocket + ": " + inputFromClient);

                String toClientId = null;
                if (inputFromClient.startsWith("@")){
                    String[] parts = inputFromClient.split("\\s+");
                    if (parts.length > 0) {
                            toClientId = parts[0].substring(1);
                    }
                }


                if (toClientId == null) {
                    clients.values().forEach(it -> it.send(inputFromClient));
                } else {
                    ClientHandler toClient = clients.get(toClientId);
                    if (toClient != null) {
                        toClient.send(inputFromClient.replace("@" + toClientId + " ", ""));
                    } else {
                        System.out.println("�� ������ ������ � ���������������: " + toClientId);
                    }
                }

                if (Objects.equals("exit", inputFromClient)) {
                    System.out.println("������ ����������");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("��������� ������ ��� ������������� � ��������" + clientSocket + ": " + e.getMessage());
        }
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("������ ��� ���������� ������� " + clientSocket + ": " + e.getMessage());
        }
    }

    public void send (String msg) {
        out.println(msg);

    }
}