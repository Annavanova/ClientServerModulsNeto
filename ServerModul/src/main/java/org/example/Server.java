package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Server {

    public static final String PROPERTY_PATH_FILE = "settings.properties";
    public static String HOST;
    public static int PORT;
    public static String LOGFILE;
    protected static LoggerServer loggerServer = LoggerServer.getInstance();

    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    ///Разбор настроки сервера
    private static void loadSettingsProperty() {
        try {
            InputStream inputStreamPath = Server.class.getClassLoader().getResourceAsStream(PROPERTY_PATH_FILE);
            Properties properties = new Properties();
            properties.load(inputStreamPath);
            HOST = properties.getProperty("host");
            PORT = Integer.valueOf(properties.getProperty("port"));
            LOGFILE = properties.getProperty("logpath");
        } catch (IOException e) {
            //throw new RuntimeException(e);
            loggerServer.error(LoggerServer.getInstance().ERROR_INFO, e.getMessage());
        }
    }

    public void ServerStart() throws IOException {
        System.out.println("Сервер запущен!");
        loggerServer.log(LoggerServer.getInstance().INFO_INFO, "Сервер запущен!");
        try {
            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                ServerClientHeaders serverClientHeaders = new ServerClientHeaders(socket);
                System.out.println("Приссоединился новый пользователь: " + serverClientHeaders.toString());

                Thread thread =new Thread(serverClientHeaders);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            loggerServer.error(LoggerServer.getInstance().ERROR_INFO, "Сервер запустить не удалось\n" + e.getMessage());
        } finally {
            //ServerCloseSocket();
            serverSocket.close();
        }
    }

    public void ServerCloseSocket(){
        try {
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        loadSettingsProperty();
        loggerServer.log(LoggerServer.getInstance().INFO_INFO, "Запуск сервера...");
        System.out.println("Запуск сервера...");
        loggerServer.log(LoggerServer.getInstance().INFO_INFO, "Данные сервера: host=" + HOST + ", port=" + PORT);

        ServerSocket serverSocket = new ServerSocket(PORT);
        Server server = new Server(serverSocket);
        server.ServerStart();

    }
}

