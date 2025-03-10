package com.quackstagram.network;

// Singleton pattern implementation to manage a single instance of the server
public class ServerManager {
    private static ServerManager instance;
    private Server server;
    private Thread serverThread;
    private boolean isServerRunning;

    private ServerManager() {
        isServerRunning = false;
    }

    public static ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }

    // Starts the server in a separate thread if it is not already running
    public synchronized void startServer() {
        if (!isServerRunning) {
            server = new Server();
            serverThread = new Thread(server);
            serverThread.setDaemon(true); // Allow JVM to exit even if server is running
            serverThread.start();
            isServerRunning = true;
            System.out.println("Chat server started on port 9999");
        }
    }

    // Stops the server and ensures all resources are properly released
    public synchronized void stopServer() {
        if (isServerRunning && server != null) {
            server.shutdown();
            try {
                serverThread.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isServerRunning = false;
            System.out.println("Chat server stopped");
        }
    }

    public boolean isRunning() {
        return isServerRunning;
    }
}