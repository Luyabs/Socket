package socket;

import javax.swing.*;
import java.util.Date;
import java.io.*;
import java.net.*;

import static global_param.GlobalParam.FILE_NOT_EXIST;
import static gui_menu.menu.util.Util.logAppend;
import static gui_menu.menu.util.Util.transmitFile;

public class SocketClient{
    private JTextArea consoleLog;
    private boolean hasLogin;
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;

    public SocketClient(String serverName, int port, JTextArea consoleLog) throws ConnectException{
        try {
            this.consoleLog = consoleLog;
            client = new Socket(serverName, port);
            setHasLogin(false);
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());

            logAppend(consoleLog, "客户端已启动...");
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
            return false;
        }
    }

    public void sendMessage(String message) {      //用户向服务端发送一条信息
        try {
            if (message.length() > 2 && message.charAt(1) == ':' && message.charAt(2) == '\\') {    //识别为文件
                message = transmitFile(message);
                if (message.equals(FILE_NOT_EXIST)) {
                    logAppend(consoleLog, "文件打开失败 不存在该文件");
                    return;
                }
            }
            out.writeUTF(message + "\n客户端发送时间: " + new Date());
            logAppend(consoleLog, "信息发送成功");
        }
        catch (SocketException s) {
            logAppend(consoleLog, "服务端被关闭 连接已断开");
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
            logAppend(consoleLog, "强制退出!");
        }
    }
}
