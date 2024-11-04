package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ServerClientHeaders implements Runnable {
    protected static LoggerServer loggerServer = LoggerServer.getInstance();

    protected Socket socket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private String clientUserName;

    //protected static List<ServerClientHeaders> clientHeaders = Collections.synchronizedList(new ArrayList<ServerClientHeaders>());
    public static ArrayList<ServerClientHeaders> clientHeaders = new ArrayList<>();

    public ServerClientHeaders(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUserName = bufferedReader.readLine();
            loggerServer.log(LoggerServer.getInstance().INFO_INFO, "Приссоединился " + clientUserName + " (" + socket.getInetAddress() + ") ");
            clientHeaders.add(this);
            loggerServer.log(LoggerServer.getInstance().INFO_INFO,"SERVER: " + clientUserName + " зашел в чат");
            broadcastMessage("SERVER: " + clientUserName + " зашел в чат");
        }catch (IOException e){
            closeAll(socket, bufferedWriter,bufferedReader);
        }
    }

    public void run(){
             try {
                while (socket.isConnected()) {
                    String message = bufferedReader.readLine();
                    System.out.println(">> " + message);

                    if(message.equals("bye")) {
                        System.out.println("Вышел!");
                        closeAll(socket, bufferedWriter,bufferedReader);
                        break;
                        }
                    broadcastMessage(message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                 removeClientHendler();
             }
   }
    public void removeClientHendler(){
        clientHeaders.remove(this);
        broadcastMessage("SERVER: " + clientUserName +  " покинул чат!");
        }

    private void closeAll(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        removeClientHendler();
        try {
            if(!socket.isClosed()){

                bufferedReader.close();
                bufferedWriter.close();
                System.out.println("Socket закрывается");
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        socket.isClosed();
    }

    private void broadcastMessage(String message) {
         synchronized (clientHeaders){
            Iterator<ServerClientHeaders> iterator = clientHeaders.iterator();
            while (iterator.hasNext()){
                ServerClientHeaders sch = iterator.next();
                try {
                    if(!sch.clientUserName.equals(clientUserName)){
                    sch.bufferedWriter.write(message);
                    loggerServer.message(LoggerServer.getInstance().INFO_MESSAGE, message);
                    sch.bufferedWriter.newLine();
                    sch.bufferedWriter.flush();
                }
                } catch (IOException e) {
                    //throw new RuntimeException(e);
                    closeAll(socket, bufferedWriter,bufferedReader);
                }
            }
        }
    }

    @Override
    public String toString() {
        return  clientUserName;
    }

}

