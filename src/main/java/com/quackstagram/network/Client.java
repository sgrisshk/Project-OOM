package com.quackstagram.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    @Override
    public void run(){
        try{
            socket = new Socket( "127.0.0.1", 9999);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        class InputHandler implements Runnable{
            @Override
            public void run(){
                try{
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    String message;
                    // Add code to handle input
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }   
        }
    }
}   

