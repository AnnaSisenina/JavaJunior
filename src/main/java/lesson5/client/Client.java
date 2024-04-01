package lesson5.client;

import lesson5.server.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

public class Client {

    public static void main(String[] args) {
        try {
            Socket serverSocket = new Socket("localhost", Server.PORT);
            System.out.println("Подключились к серверу: tcp://localhost: " + Server.PORT);

            Scanner serverIn = new Scanner(serverSocket.getInputStream());
            String input = serverIn.nextLine();
            System.out.println("Сообщение от сервера: " + input);

            new PrintWriter(serverSocket.getOutputStream(), true).println(UUID.randomUUID());

            new Thread(new ServerReader(serverSocket)).start();
            new Thread(new ServerWriter(serverSocket)).start();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось подключить к серверу: " + e.getMessage(), e);
        }
    }
}

class ServerWriter implements Runnable {
    private final Socket serverSocket;

    public ServerWriter(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        Scanner consoleReader = new Scanner(System.in);
        try (PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true)) {
            while (true) {
                String msgFromConsole = consoleReader.nextLine();
                out.println(msgFromConsole);

                if (Objects.equals("exit", msgFromConsole)) {
                    System.out.println("Отключаемся...");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при отправке на сервер: " + e.getMessage());
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Ощибка при отключении от сервера " + e.getMessage());
        }
    }
}

class ServerReader implements Runnable {
    private final Socket serverSocket;

    public ServerReader(Socket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try (Scanner scannerIn = new Scanner(serverSocket.getInputStream())) {
            while (scannerIn.hasNext()) {
                String inputFromServer = scannerIn.nextLine();
                System.out.println("Сообщение от сервера: " + inputFromServer);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении с сервера: " + e.getMessage());
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Ошибка при отключении от сервера " + e.getMessage());
        }

    }
}