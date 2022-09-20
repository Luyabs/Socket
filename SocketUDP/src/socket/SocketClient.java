package socket;

import javax.swing.*;
import java.util.Date;
import java.io.*;
import java.net.*;

import static global_param.GlobalParam.DEFAULT_PORT;
import static global_param.GlobalParam.EXIT_STR;
import static gui_menu.util.Util.logAppend;
import static socket.UDPUtil.UDPReceive;
import static socket.UDPUtil.UDPSend;

public class SocketClient implements UDPUtilInterface{
    private JTextArea consoleLog;
    private boolean hasLogin;
    private DatagramSocket client;
    private static int destPort;

    public SocketClient(int port, JTextArea consoleLog) throws ConnectException{
        try {
            setDestPort(DEFAULT_PORT);
            this.consoleLog = consoleLog;
            client = new DatagramSocket(port);
            setHasLogin(false);

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

    public String loginOneTime(String userInfo) {       //用户登录(仅一次)
        try {
            send(userInfo);
            return receive();
        }
        catch (Exception e) {
            return null;
        }
    }

    public void sendMessage(String message) {      //用户向服务端发送一条信息
        if (message.equals(EXIT_STR)) {
            send(message);
            return;
        }
        send(message + "\n客户端发送时间: " + new Date());
    }

    public void exit() {
        if (client != null)
            client.close();
        setHasLogin(false);
    }

    public static void setDestPort(int destPort) {
        SocketClient.destPort = destPort;
    }

    @Override
    public void send(String message) {
        UDPSend(client, message, destPort);
    }

    @Override
    public String receive() {
        return UDPReceive(client);
    }
}
