package com.quackstagram.network;

// Singleton class to manage a single server instance for the entire application
 
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

    public synchronized void startServer() {
        if (!isServerRunning) {
            server = new Server();
            serverThread = new Thread(server);
            serverThread.setDaemon(true); 
            serverThread.start();
            isServerRunning = true;
            System.out.println("Chat server started on port 9999");
        }
    }

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