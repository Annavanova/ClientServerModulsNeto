package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;


public class LoggerServer {
    protected int num = 1;
    private static LoggerServer logger;
    protected AtomicInteger numStr = new AtomicInteger(0);
    public String UrlPathString = "serverLog.txt";
    public String INFO_INFO = "INFO";
    public String ERROR_INFO = "ERROR";
    public String INFO_MESSAGE = "MESSAGE";


    private LoggerServer() {
    }

    public static LoggerServer getInstance() {
        if (logger == null) logger = new LoggerServer();
        return logger;
    }

    public String log(String logInfo, String msg) {
        String dtf = (DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")).format(LocalDateTime.now());
        String stringLog = String.format("[%s] (%d) %s: %s\n", dtf, numStr.incrementAndGet(), logInfo, msg);
        writeLog(stringLog);
        return stringLog;
    }

    public String error(String logInfo, String msg){
        return log(ERROR_INFO, msg);
    }

    public String message(String logInfo, String msg){
        return log(INFO_MESSAGE, msg);
    }

    public synchronized File getMyFile(String UrlPath){
        File file = new File(UrlPath);
        if(!file.exists()){
            //file.mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return file;
    }

    private void writeLog(String stringLog) {

           try {
            BufferedWriter writerLog = new BufferedWriter(new FileWriter(getMyFile(UrlPathString),true));
            writerLog.write(stringLog);
            writerLog.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
