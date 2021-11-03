package server;

import java.io.*;
import java.net.Socket;

public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;

    public UserThread (Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream outputStream = socket.getOutputStream();
            writer = new PrintWriter(outputStream, true);

            printUsers();

            String userName = reader.readLine();
            server.addUserName(userName);

            String serverMessage = ServerMessages.USER_CONNECTED + userName;
            server.broadcast(serverMessage, this);

            String clientMessage;

            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);
            } while (!clientMessage.equals(ServerMessages.USER_EXIT));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + ServerMessages.USER_HAS_LEFT;
            server.broadcast(serverMessage, this);
        }catch (IOException ex) {
            System.out.println("Error in server.UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    void printUsers() {
        if (server.hasUsers()) {
            writer.println(ServerMessages.CONNECTED_USERS + server.getUserNames());
        } else writer.println("No other users connected");
    }

    void sendMessage (String message) {
        writer.println(message);
    }
}
