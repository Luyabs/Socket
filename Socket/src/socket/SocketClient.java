package socket;

import java.util.Date;
import java.io.*;
import java.net.*;

public class SocketClient{
    private boolean hasLogin;   //这个变量不会在该类进行修改
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;

    public SocketClient(String serverName, int port) throws ConnectException{
        try {
            client = new Socket(serverName, port);
            setHasLogin(false);
            InputStream inputStream = client.getInputStream();
            in = new DataInputStream(inputStream);
            OutputStream outputStream = client.getOutputStream();
            out = new DataOutputStream(outputStream);

            System.out.println("客户端已启动...");
        }
        catch (ConnectException c) {
            throw c;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setHasLogin(boolean login) {
        hasLogin = login;
    }

    public boolean isLogin() {
        return hasLogin;
    }

    public boolean loginOneTime(String userInfo) {       //用户登录(仅一次)
        try {
            out.writeUTF(userInfo);
            return in.readBoolean();
        }
        catch (Exception e) {
            return false;}
    }

    public void sendMessage(String message) {      //用户向服务端发送一条信息
        try {
            out.writeUTF(message + "\n客户端发送时间: " + new Date());
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
        }
    }
}
