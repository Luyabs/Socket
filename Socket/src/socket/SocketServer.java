package socket;

import data.UserData;
import gui_menu.util.Util;

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

public class SocketServer extends Thread {
    private JTextArea consoleLog;
    private ServerSocket serverSocket;
    private UserData database;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean isConnected;

    public SocketServer(int port, JTextArea consoleLog) {
        try {
            database = new UserData();
            this.consoleLog = consoleLog;
            isConnected = false;
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(WAITING_TIME);   //10s等待

            consoleLog.append("服务端已启动...");
            showLocalStatus();
        }
        catch (IllegalArgumentException illegalArgumentException) {
            consoleLog.append("端口号长度不对劲...");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        freshLog();
    }

    @Override
    public void run() {
        runServer();
    }

    private void runServer(){
        consoleLog.append("等待客户端连接...\n");
        freshLog();
        while (true) {
            Socket server = acceptClient();     //建立连接
            if (server != null) {
                isConnected = true;       //连接已建立
                checkLogin(server);
                receiveMessage(server);   //接收来自客户端的信息
                isConnected = false;
            }
            else                          //accpetClient返回null后将自动停止一切进程
                break;
        }

        isConnected = false;
        try {
            serverSocket.close();
            exit();
        }
        catch (IOException e) {
            consoleLog.append("没有连接到客户端\n");
        }
        freshLog();
    }

    private Socket acceptClient() {
        Socket server;
        try {
            server = serverSocket.accept();
            consoleLog.append("客户端连接成功...\n");
            freshLog();
            showConnectStatus(server);
            out = new DataOutputStream(server.getOutputStream());
            in = new DataInputStream(server.getInputStream());
            return server;
        }
        catch (SocketTimeoutException t) {
            consoleLog.append("服务端空载时间超过" + (WAITING_TIME / 1000) + "s\n服务端关闭!\n");
            freshLog();
            return null;
        }
        catch (SocketException s) {
            consoleLog.append("服务端与所有客户端失去连接\n");
            freshLog();
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void checkLogin(Socket server) {
        if (server == null) {
            consoleLog.append("用户断开连接\n");
            exit();
            return;
        }
        int count = 0;
        boolean success = checkLoginOneTime(server);
        while (!success) {      //检查登录信息
            try {
                out.writeBoolean(false);
            }
            catch (IOException ignore) {

            }
            count++;
            if (count == 10) {  //修bug
                consoleLog.append("用户名或密码多次错误 中断连接\n");
                exit();
                return;
            }
            freshLog();
            success = checkLoginOneTime(server);
        }
        consoleLog.append("用户登录成功\n");
        try {
            out.writeBoolean(true);
        }
        catch (IOException ignore) {

        }
        freshLog();
    }

    private boolean checkLoginOneTime(Socket server) {
        try {
            String loginUser = in.readUTF();
            boolean success = database.contains(loginUser);
            return success;
        }
        catch (IOException e) {
            return false;
        }
    }

    private void receiveMessage(Socket server) {      //服务端接收信息
        if (serverSocket == null) {
            consoleLog.append("用户断开连接\n");
            exit();
            return;
        }
        try {
            while (true) {
                String message;
                message = in.readUTF();
                consoleLog.append("[Server] " + message + "\n服务端接收时间: " + new Date() + '\n');
                freshLog();
            }
        }
        catch (IOException e) {
            consoleLog.append("与当前客户端断开连接!\n");
            freshLog();
        }
    }

    public void showLocalStatus() {
        consoleLog.append("服务端端口号: " + serverSocket.getLocalPort() + '\n');
        consoleLog.append("当前连接状态: " + isConnected + '\n');
        freshLog();
    }

    public void showConnectStatus(Socket remote) {
        consoleLog.append("服务端主机地址: " + remote.getLocalSocketAddress() + '\n');
        consoleLog.append("客户端主机地址: " + remote.getRemoteSocketAddress() + '\n');
        freshLog();
    }

    private void freshLog() {       //删去最早历史日志
        Util.freshLog(consoleLog);
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
            consoleLog.append("强制退出!\n");
            e.printStackTrace();
        }
        freshLog();
    }
}
