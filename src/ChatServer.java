import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 1111;
    private static HashMap<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Chat Server Started[*_*]");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Server Error[-_-] " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private String username;
        private BufferedReader input;
        private PrintWriter output;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);

                // User Authentication
                output.println("Enter Username:[*_*]");
                username = input.readLine();
                output.println("Enter Password:[-,-]");
                String password = input.readLine();

                synchronized (clients) {
                    if (clients.containsKey(username)) {
                        output.println("Username already use. Disconnecting[*_*]");
                        socket.close();
                        return;
                    }
                    clients.put(username, output);
                }

                output.println("Welcome to the Chat[*_-]");
                broadcast(username + " has joined the chat[*_-]");

                // Handle Client Messages
                String message;
                while ((message = input.readLine()) != null) {
                    if (message.startsWith("/quit")) {
                        break;
                    } else if (message.startsWith("/pm")) {
                        privateMessage(message);
                    } else {
                        broadcast(username + ": " + message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Client Error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clients) {
                    clients.remove(username);
                    broadcast(username + " has left the chat[*_-]");
                }
            }
        }

        private void broadcast(String message) {
            synchronized (clients) {
                for (PrintWriter writer : clients.values()) {
                    writer.println(message);
                }
            }
        }

        private void privateMessage(String message) {
            String[] tokens = message.split(" ", 3);
            if (tokens.length < 3) {
                output.println("Invalid private message format. Use /pm <username> <message>");
                return;
            }
            String targetUser = tokens[1];
            String privateMsg = tokens[2];

            synchronized (clients) {
                PrintWriter target = clients.get(targetUser);
                if (target != null) {
                    target.println("[Private] " + username + ": " + privateMsg);
                } else {
                    output.println("User not found[-_-]");
                }
            }
        }
    }
}
