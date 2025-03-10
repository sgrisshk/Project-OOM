package com.quackstagram.network;

// Server class handling client connections and message forwarding
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.quackstagram.model.User;
import com.quackstagram.service.UserService;

public class Server implements Runnable {
    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean isRunning;
    private ExecutorService pool;

    public Server() {
        connections = new ArrayList<>();
        isRunning = true;
        pool = Executors.newCachedThreadPool();
    }

    // Starts the server and listens for incoming client connections
    @Override
    public void run() {
        try {
            server = new ServerSocket(9999);
            System.out.println("Server listening on port 9999...");
            while(isRunning) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }
        } catch (IOException e) {
            if (isRunning) {
                shutdown();
            }
            throw new RuntimeException(e);
        }
    }

    private ConnectionHandler findHandler(String username) {
        for (ConnectionHandler handler : connections) {
            if (handler.connectedUser != null &&
                handler.connectedUser.getUsername().equals(username)) {
                return handler;
            }
        }
        return null;
    }

    // Sends a direct message from one user to another if the recipient is connected
    public void sendDirectMessage(String from, String to, String content) {
        ConnectionHandler receiver = findHandler(to);
        if (receiver != null) {
            receiver.sendMessage(from + ": " + content);
        }
    }

    class ConnectionHandler implements Runnable {
        private BufferedReader in;
        private PrintWriter out;
        private User connectedUser;
        private Socket client;

        public ConnectionHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String username = in.readLine();
                UserService userService = new UserService();
                try {
                    connectedUser = userService.getUserByUsername(username);
                    System.out.println("User connected: " + username);
                } catch (Exception e) {
                    out.println("Error: User not found");
                    close();
                    return;
                }

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/quit")) {
                        break;
                    }

                    String[] parts = message.split("\\|", 2);
                    if (parts.length == 2) {
                        String receiver = parts[0];
                        String content = parts[1];
                        sendDirectMessage(connectedUser.getUsername(), receiver, content);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
                connections.remove(this);
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void close() {
            try {
                if (client != null) client.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Gracefully shuts down the server, closing all connections and the thread pool
    public void shutdown() {
        isRunning = false;
        try {
            if (connections != null) {
                for (ConnectionHandler connection : new ArrayList<>(connections)) {
                    if (connection != null) {
                        try {
                            connection.sendMessage("Server is shutting down");
                            connection.close();
                        } catch (Exception e) {
                            System.out.println("Error closing connection: " + e.getMessage());
                        }
                    }
                }
            }

            if (server != null && !server.isClosed()) {
                server.close();
            }

            if (pool != null) {
                pool.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error during shutdown: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
