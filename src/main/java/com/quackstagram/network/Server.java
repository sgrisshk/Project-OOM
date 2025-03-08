package com.quackstagram.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.quackstagram.service.UserService;
import com.quackstagram.model.User;

public class Server implements Runnable{
    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean isRunning;
    private ExecutorService pool;

    @Override
    public void run() {
        try {
            server = new ServerSocket(9999);
            pool = Executors.newCachedThreadPool();
            while(isRunning){
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
                handler.run();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public void broadcastMessage(String message){
        for (ConnectionHandler connection : connections){
            connection.sendMessage(message);
        }
    }
    public void shutdown(){
        isRunning = false;
        try {
            for (ConnectionHandler connection : connections) {
                connection.sendMessage("Server is shutting down");
                connection.close();
            }
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class ConnectionHandler implements Runnable{
        private BufferedReader in;
        private PrintWriter out;
        private User connectedUser;

        private Socket client;
        public ConnectionHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try{
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String username = in.readLine(); 
                UserService userService = new UserService();
                try{
                    connectedUser = userService.getUserByUsername(username); 
                }
                catch (Exception e){
                    out.println("User not found");
                }
                System.out.println("User connected: " + connectedUser.getUsername());
                broadcastMessage("User connected: " + connectedUser.getUsername());
                String message; 
                while((message = in.readLine()) != null){
                   if (message.startsWith("/quit")){
                        broadcastMessage(connectedUser.getUsername() + " has left the chat");
                        close();
                        break;      
                        }
                    else{
                        // Handle messages
                        broadcastMessage(connectedUser.getUsername() + ": " + message);
                    }
                }


                        
                
            } catch (IOException e){
                e.printStackTrace();
            }

        }
        public void sendMessage(String message){
            out.println(message);
        }
        public void close(){
            try{
                client.close();
                in.close();
                out.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }   
    }
    public static void main(String[] args){
        Server server = new Server();
        server.run();
    }


}
