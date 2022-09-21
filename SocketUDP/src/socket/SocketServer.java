package socket;

import data.UserData;

import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.util.Date;

import static global_param.GlobalParam.DEFAULT_PORT;
import static gui_menu.util.Util.logAppend;
import static socket.UDPUtil.UDPReceive;
import static socket.UDPUtil.UDPSend;

public class SocketServer extends Thread implements UDPUtilInterface{
    private JTextArea consoleLog;
    private UserData database;
    private DatagramSocket server;
    private static int destPort;
    // private int destPort;

    static {    //只执行一次
        setDestPort(DEFAULT_PORT - 1);
    }

    public SocketServer(int port, JTextArea consoleLog) {
        try {
            database = new UserData();
            this.consoleLog = consoleLog;
            server = new DatagramSocket(port);

            logAppend(SocketServer.this.consoleLog, "服务端已启动...");
            showLocalStatus();
        }
        catch (IllegalArgumentException illegalArgumentException) {
            logAppend(SocketServer.this.consoleLog, "端口号长度不对劲...");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        runServer();
    }

    private void runServer(){
        logAppend(consoleLog, "等待客户端连接...");
        while (true) {
            if (server != null) {
                checkLogin();           //检查登录
                receiveMessage();       //接收来自客户端的信息
            }
            else
                break;
        }

        try {
            server.close();
            exit();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkLogin() {
        int count = 0;
        while (!checkLoginOneTime()) {      //检查登录信息 验证登陆不成功 发送false
            send("");
            count++;
            if (count == 10) {  //修bug
                logAppend(consoleLog, "用户名或密码多次错误/失去与客户端的连接");
                exit();
                return;
            }
        }
        logAppend(consoleLog, "用户登录成功");    //验证登陆成功 发送true
        try {
            send("true");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkLoginOneTime() {
        String loginUser = receive();
        return database.contains(loginUser);
    }

    private void receiveMessage() {      //服务端接收信息
        if (server == null) {
            logAppend(consoleLog, "用户断开连接");
            exit();
            return;
        }
        while (true) {
            String message;
            message = receive();
            if (message.equals("______exit______")) {
                logAppend(consoleLog, "当前连接用户已断开");
                return;
            }
            if (message.length() >= 12 && message.substring(0, 12).equals("____FILE____")) {
                message = message.substring(12);
            }
            logAppend(consoleLog, "[Server] " + message + "\n服务端接收时间: " + new Date());
        }
    }

    public void showLocalStatus() {
        logAppend(consoleLog, "服务端端口号: " + server.getLocalPort());
    }

    public void exit() {
        try {
            if (server != null)
                server.close();
        }
        catch (Exception e) {
            logAppend(consoleLog, "强制退出!");
            e.printStackTrace();
        }
    }

    public static void setDestPort(int destPort) {
        SocketServer.destPort = destPort;
    }

    @Override
    public void send(String message) {
        UDPSend(server, message, destPort);
    }

    @Override
    public String receive() {
        return UDPReceive(server);
    }
}
