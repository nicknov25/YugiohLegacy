package com.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private int clientsConnected = 0;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private String clientUsername;
    public ClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");
            clientsConnected++;
        }
        catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter, objectInputStream, objectOutputStream);
        }
    }
    @Override
    public void run() {
        String messageFromClient;
        while(socket.isConnected()){
            try {
                if(clientsConnected != -1){
                    gameBegin();
                }
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            }
            catch(IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter, objectInputStream, objectOutputStream);
                break;
            }
        }
    }
    public void gameBegin(){
        if(clientsConnected == 2){
            for (ClientHandler clientHandler : clientHandlers) {
                try {
                    clientHandler.bufferedWriter.write("Start");
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                    clientsConnected = -1;
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter, objectInputStream, objectOutputStream);
                }
            }
        }
    }
    public void broadcastMessage(String messageToSend){
        for(ClientHandler clientHandler : clientHandlers){
            try {
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }
            catch(IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter, objectInputStream, objectOutputStream);
            }
        }
    }
    public void removeClientHandler(){
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
        clientHandlers.remove(this);
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter,
                                ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
        removeClientHandler();
        try {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if(objectOutputStream != null){
                objectOutputStream.close();
            }
            if(objectInputStream != null){
                objectInputStream.close();
            }
            if(socket != null){
                socket.close();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
