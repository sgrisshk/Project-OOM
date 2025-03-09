package com.quackstagram.network;

/**
 * Singleton class to manage a single server instance for the entire application
 */
public class ServerManager {
    private static ServerManager instance;
    private Server server;
    private Thread serverThread;
    private boolean isRunning = false;
    
    public static synchronized ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }
    
    public synchronized void startServer() {
        if (!isRunning) {
            try {
                server = new Server();
                // Initialize server
                serverThread = new Thread(server);
                serverThread.setDaemon(true); // Make it a daemon thread so it doesn't prevent app exit
                serverThread.start();
                isRunning = true;
                System.out.println("Chat server started on port 9999");
            } catch (Exception e) {
                System.out.println("Server may already be running: " + e.getMessage());
                // Assume server is running somewhere
                isRunning = true;
            }
        }
    }
    
    public boolean isServerRunning() {
        return isRunning;
    }
    
    public synchronized void stopServer() {
        if (isRunning && server != null) {
            try {
                server.shutdown();
                isRunning = false;
                System.out.println("Server has been shut down");
            } catch (Exception e) {
                System.out.println("Error shutting down server: " + e.getMessage());
            }
        }
    }
} 