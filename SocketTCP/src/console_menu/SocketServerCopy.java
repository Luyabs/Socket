package console_menu;

import data.UserData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;

import static global_param.GlobalParam.MAX_LOGIN_TIMES;
import static global_param.GlobalParam.WAITING_TIME;

public class SocketServerCopy extends Thread {
    private ServerSocket serverSocket;
    private UserData database;
    private DataInputStream in;
    private DataOutputStream out;
    //private int connectNum = 0; //客户端连接数

    public SocketServerCopy(int port) {
        try {
            database = new UserData();

            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(WAITING_TIME);   //10s等待

            System.out.println("服务端已启动...");
            showLocalStatus();
        }
        catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("端口号长度不对劲...");
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
        System.out.println("等待客户端连接...");
        while (true) {
            Socket server = acceptClient();     //建立连接
            if (server != null) {
                checkLogin(server);       //检查登录信息
                receiveMessage(server);   //接收来自客户端的信息
            }
            else                          //accpetClient返回null后将自动停止一切进程
                break;
        }

        try {
            serverSocket.close();
            exit();
        }
        catch (IOException e) {
            System.out.println("没有连接到客户端");
            //e.printStackTrace();
        }
    }

    private Socket acceptClient() {
        Socket server;
        try {
            server = serverSocket.accept();
            System.out.println("客户端连接成功...");
            showConnectStatus(server);
            return server;
        }
        catch (SocketTimeoutException t) {
            System.out.println("服务端空载时间超过" + (WAITING_TIME / 1000) + "s\n服务端关闭!");
            return null;
        }
        catch (SocketException s) {
            System.out.println("服务端与所有客户端失去连接");
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void checkLogin(Socket server) {
        int loginCount = 0;
        for (int i = 0; i < MAX_LOGIN_TIMES; i++) {
            if (checkLoginOneTime(server)) {
                System.out.println("用户登陆成功");
                return;
            }
            System.out.println("用户登陆失败第" + ++loginCount + "次");
        }
    }

    private boolean checkLoginOneTime(Socket server) {
        try {
            out = new DataOutputStream(server.getOutputStream());
            in = new DataInputStream(server.getInputStream());

            String loginUser = in.readUTF();
            boolean success = database.contains(loginUser);
            out.writeBoolean(success);  //反馈验证状态
            return success;
        }
        catch (IOException e) {
            //e.printStackTrace();
            return false;
        }
    }

    private void receiveMessage(Socket server) {      //服务端接收信息
        try {
            //out = new DataOutputStream(server.getOutputStream());
            in = new DataInputStream(server.getInputStream());
            while (true) {
                String message;
                message = in.readUTF();
                if (message.equals("exit")) {
                    //connectNum--;
                    return;
                }
                System.out.println("[Server] " + message + "\n服务端接收时间: " + new Date());
            }
        }
        catch (IOException e) {
            System.out.println("与当前客户端断开连接!");
        }
    }

    private void showLocalStatus() {
        System.out.println("服务端端口号: " + serverSocket.getLocalPort());
    }

    private void showConnectStatus(Socket remote) {
        System.out.println("服务端主机地址: " + remote.getLocalSocketAddress());
        System.out.println("客户端主机地址: " + remote.getRemoteSocketAddress());
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
            System.out.println("强制退出!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SocketServerCopy server = new SocketServerCopy(6789);
        server.start();
    }
}
