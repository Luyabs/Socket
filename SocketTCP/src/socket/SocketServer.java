package socket;

import data.UserData;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;

import static global_param.GlobalParam.WAITING_TIME;
import static gui_menu.menu.util.Util.logAppend;

public class SocketServer extends Thread {
    private JTextArea consoleLog;
    private ServerSocket serverSocket;
    private UserData database;

    private DataInputStream in;
    private DataOutputStream out;
    private boolean isConnected;    //当前连接状态

    public SocketServer(int port, JTextArea consoleLog) {
        try {
            database = new UserData();
            this.consoleLog = consoleLog;
            isConnected = false;
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(WAITING_TIME);   //10s等待

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
            Socket server = acceptClient();     //建立连接
            if (server != null) {
                isConnected = true;       //连接已建立
                checkLogin(server);     //检查登录
                receiveMessage();       //接收来自客户端的信息
                isConnected = false;
            }
            else                          //acceptClient返回null后将自动停止一切进程
                break;
        }

        isConnected = false;
        try {
            serverSocket.close();
            exit();
        }
        catch (IOException e) {
            logAppend(consoleLog, "没有连接到客户端");
        }
    }

    private Socket acceptClient() {
        Socket server;
        try {
            server = serverSocket.accept();
            logAppend(consoleLog, "客户端连接成功...");
            showConnectStatus(server);
            out = new DataOutputStream(server.getOutputStream());
            in = new DataInputStream(server.getInputStream());
            return server;
        }
        catch (SocketTimeoutException t) {
            logAppend(consoleLog, "服务端空载时间超过" + (WAITING_TIME / 1000) + "s\n服务端已关闭，请关闭并重新运行服务端");
            return null;
        }
        catch (SocketException s) {
            logAppend(consoleLog, "服务端与所有客户端失去连接");
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void checkLogin(Socket server) {
        if (server == null) {
            logAppend(consoleLog, "用户断开连接");
            exit();
            return;
        }
        int count = 0;
        while (!checkLoginOneTime()) {      //检查登录信息 验证登陆不成功 发送false
            try {
                out.writeBoolean(false);
            }
            catch (IOException ignore) {}
            count++;
            if (count == 10) {  //修bug
                logAppend(consoleLog, "用户名或密码多次错误/失去与客户端的连接");
                exit();
                return;
            }
        }
        logAppend(consoleLog, "用户登录成功");    //验证登陆成功 发送true
        try {
            out.writeBoolean(true);
        }
        catch (IOException ignore) {}
    }

    private boolean checkLoginOneTime() {
        try {
            String loginUser = in.readUTF();
            return database.contains(loginUser);
        }
        catch (IOException e) {
            return false;
        }
    }

    private void receiveMessage() {      //服务端接收信息
        if (serverSocket == null) {
            logAppend(consoleLog, "用户断开连接");
            exit();
            return;
        }
        try {
            while (true) {
                String message;
                message = in.readUTF();
                if (message.length() >= 12 && message.substring(0, 12).equals("____FILE____")) {
                    message = message.substring(12);
                }
                logAppend(consoleLog, "[Server] " + message + "\n服务端接收时间: " + new Date());
            }
        }
        catch (IOException e) {
            logAppend(consoleLog, "与当前客户端已断开连接");
        }
    }

    public void showLocalStatus() {
        logAppend(consoleLog, "服务端端口号: " + serverSocket.getLocalPort());
        logAppend(consoleLog, "当前连接状态: " + isConnected);
    }

    public void showConnectStatus(Socket remote) {
        logAppend(consoleLog, "服务端主机地址: " + remote.getLocalSocketAddress());
        logAppend(consoleLog, "客户端主机地址: " + remote.getRemoteSocketAddress());
    }

    public void exit() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (serverSocket != null)
                serverSocket.close();
        }
        catch (Exception e) {
            logAppend(consoleLog, "强制退出!");
            e.printStackTrace();
        }
    }
}
