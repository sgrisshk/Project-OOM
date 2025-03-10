package com.quackstagram.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.quackstagram.model.User;


// Handles client-side messaging and server communication
public class MessageClient implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private User currentUser;
    private List<MessageListener> listeners = new ArrayList<>();
    private boolean isRunning = false;
    private Thread clientThread;

    public interface MessageListener {
        void onMessageReceived(String sender, String content);
    }

    public MessageClient(User currentUser) {
        this.currentUser = currentUser;
    }

    public void connect() {
        // Ensure server is running
        ServerManager.getInstance().startServer();

        if (clientThread == null || !clientThread.isAlive()) {
            clientThread = new Thread(this);
            clientThread.setDaemon(true);
            clientThread.start();
        }
    }

    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }

    // Sends a message to a specific receiver using the socket output stream
    public void sendMessage(String receiver, String content) {
        if (out != null) {
            out.println(receiver + "|" + content);
        }
    }

    public void disconnect() {
        isRunning = false;
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Listens for incoming messages from the server and notifies listeners
    @Override
    public void run() {
        try {
            socket = new Socket("127.0.0.1", 9999); // Local machine ip
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            isRunning = true;

            // Send username for debugging
            out.println(currentUser.getUsername());

            // Listen for messages
            String message;
            while (isRunning && (message = in.readLine()) != null) {
                if (message.contains(": ")) {
                    String[] parts = message.split(": ", 2);
                    String sender = parts[0];
                    String content = parts[1];

                    // Notify listeners
                    for (MessageListener listener : listeners) {
                        listener.onMessageReceived(sender, content);
                    }
                }
            }
        } catch (java.net.ConnectException e) {
            System.out.println("Could not connect to the chat server: " + e.getMessage());
            // Error if no chat
        } catch (IOException e) {
            if (isRunning) {
                System.out.println("Connection to chat server lost: " + e.getMessage());
            }
        } finally {
            disconnect();
        }
    }
}