package client;

import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class WriteThread extends Thread implements IThread{
    private PrintWriter printWriter;
    private Socket socket;
    private ChatClient client;

    public WriteThread (Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run () {
        Console console = System.console();

        String userName = console.readLine("\nEnter your name: ");
        client.setUserName(userName);
        printWriter.println(userName);

        String text;

        do {
            text = console.readLine("[" + userName + "]: ");
            printWriter.println(text);
        } while (!text.equals("bye"));

        try {
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }
}
