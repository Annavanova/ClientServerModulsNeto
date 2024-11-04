package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class Client {
    public static String HOST;
    public static int PORT;
    public  String LOGFILE;

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;

    protected static LoggerServer loggerServer = LoggerServer.getInstance();

    public Client(Socket socket, String clientUserName){
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUserName = clientUserName;
        }catch (IOException e){
            closeAll(socket, bufferedWriter,bufferedReader);
        }
    }
    private static void loadSettingsProperty() {
        try {
            InputStream inputStreamPath = Server.class.getClassLoader().getResourceAsStream(Server.PROPERTY_PATH_FILE);
            Properties properties = new Properties();
            properties.load(inputStreamPath);
            HOST = properties.getProperty("host");
            PORT = Integer.valueOf(properties.getProperty("port"));
            //LOGFILE = properties.getProperty("logpath");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMess(){
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                while (!socket.isClosed()){
                    try {
                        String messToSend = scanner.nextLine();

                        if(messToSend.equalsIgnoreCase("bye")){
                            loggerServer.log(LoggerServer.getInstance().INFO_INFO,"SERVER: " + clientUserName + " покинул чат!");
                            closeAll(socket, bufferedWriter,bufferedReader);
                            break;
                        }
                        bufferedWriter.write(clientUserName + ": " + messToSend);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }catch (IOException e){
                        closeAll(socket, bufferedWriter,bufferedReader);
                    }
                }
            }
        }).start();*/
        try {
            bufferedWriter.write(clientUserName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (!socket.isClosed()){
                String messToSend = scanner.nextLine();

                if(messToSend.equalsIgnoreCase("bye")){
                    loggerServer.log(LoggerServer.getInstance().INFO_INFO,"SERVER: " + clientUserName + " покинул чат!");
                    closeAll(socket, bufferedWriter,bufferedReader);
                    break;
                }
                bufferedWriter.write(clientUserName + ": " + messToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            closeAll(socket, bufferedWriter,bufferedReader);
        }
    }

    public void listenForMess(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()){
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    }catch (IOException e){
                        closeAll(socket, bufferedWriter,bufferedReader);
                    }
                }
            }
        }).start();
    }

    private void closeAll(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            if(!socket.isClosed()){
                bufferedReader.close();
                bufferedWriter.close();
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        finally {
            socket.isClosed();
        }
    }

    public static void main(String[] args) throws IOException {
        loadSettingsProperty();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите свой никнайм: ");
        String username = scanner.nextLine();
        Socket socket = new Socket(HOST, PORT);
        Client client = new Client(socket, username);
        client.listenForMess();
        client.sendMess();
    }
}

