//client 3
import java.io.*;
import java.net.*;

public class ChatClient3 {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 1111;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            // Authentication
            System.out.println(input.readLine());
            output.println(console.readLine());
            System.out.println(input.readLine());
            output.println(console.readLine());
            System.out.println(input.readLine());

            // Start receive messages
            new Thread(new MessageReceiver(input)).start();

            // Send messages server
            String userInput;
            while ((userInput = console.readLine()) != null) {
                output.println(userInput);
                if (userInput.equalsIgnoreCase("/quit")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Client Error[*_-] " + e.getMessage());
        }
    }

    private static class MessageReceiver implements Runnable {
        private BufferedReader input;

        public MessageReceiver(BufferedReader input) {
            this.input = input;
        }

        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = input.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (IOException e) {
                System.out.println("Connection lost[-_-]");
            }
        }
    }
}
