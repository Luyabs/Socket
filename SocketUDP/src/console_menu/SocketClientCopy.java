package console_menu;

import pojo.User;

import java.util.Date;
import java.util.Scanner;
import java.io.*;
import java.net.*;

import static global_param.GlobalParam.MAX_LOGIN_TIMES;

public class SocketClientCopy extends Thread{
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;

    public SocketClientCopy(String serverName, int port) {
        try {
            client = new Socket(serverName, port);

            InputStream inputStream = client.getInputStream();
            in = new DataInputStream(inputStream);
            OutputStream outputStream = client.getOutputStream();
            out = new DataOutputStream(outputStream);

            System.out.println("客户端已启动...");
        }
        catch (ConnectException c) {
            System.out.println("该地址与端口并未配置服务器\n给你送走");
            System.exit(1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        runClient();
    }

    private void runClient(){
        if (login()) {            //登录
            sendMessage();      //发送信息
        }
        try {
            client.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean login() {
        for (int i = 0; i < MAX_LOGIN_TIMES; i++) {
            if (loginOneTime()) {
                System.out.println("登陆成功");
                return true;
            }
            System.out.println("用户名或密码错误");
        }
        System.out.println("超过三次登录失败，请尝试重新连接");
        return false;
    }

    private boolean loginOneTime() {       //用户登录(仅一次)
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入用户名: ");
        String username = sc.next();
        System.out.print("请输入密码: ");
        String password = sc.next();
        User loginUser = new User(username, password);
        try {
            out.writeUTF(loginUser.toString());
            return in.readBoolean();
        }
        catch (IOException e) {
            System.out.println("与服务端的连接已断开");
            //e.printStackTrace();
            return false;
        }
    }

    private void sendMessage() {      //用户向服务端不停发送信息
        Scanner sc = new Scanner(System.in);
        System.out.println("请在下面发送信息(输入exit可退出): ");
        try {
            while (true) {
                String message = sc.next();
                if (message.equals("exit"))
                    break;
                out.writeUTF(message + "\n客户端发送时间: " + new Date());

            }
        }
        catch (SocketException s) {
            System.out.println("服务端被关闭 连接已断开");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (client != null)
                client.close();
        }
        catch (IOException e) {
            System.out.println("强制退出!");
            //e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SocketClientCopy client = new SocketClientCopy("localhost", 6789);
        client.start();
    }
}
