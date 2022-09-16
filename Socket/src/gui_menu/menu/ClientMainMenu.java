package gui_menu.menu;

import socket.SocketClient;

import javax.swing.*;
import java.awt.event.*;
import java.net.ConnectException;

import static global_param.GlobalParam.DEFAULT_PORT;
import static gui_menu.util.Util.logAppend;

public class ClientMainMenu extends JDialog {
    private String serverName;
    private int port;
    private SocketClient client;


    private JPanel contentPane;
    private JTextArea consoleLog;
    private JButton RunServerButton;
    private JButton loginCheckButton;
    private JButton closeServerButton;
    private JTextField portInput;
    private JButton confirmPortButton;
    private JTextField serverNameInput;
    private JTextField sendMessageBox;
    private JButton sendMessageButton;
    private JLabel sendMessage;

    public ClientMainMenu() {
        port = DEFAULT_PORT;
        serverName = "localhost";
        logAppend(consoleLog, "====此处显示系统日志====\n默认连接地址为: " + "localhost"  + ", 默认端口号为: " + DEFAULT_PORT);

        setAlwaysOnTop(true);
        setTitle("客户端主界面");
        setContentPane(contentPane);
        setModal(true);

        // 点击 X 时调用 onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // 遇到 ESCAPE 时调用 onCancel()
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        RunServerButton.addMouseListener(new MouseAdapter() {   //开启客户端
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean successOpen = true;
                if (client == null) {
                    try {
                        client = new SocketClient(serverName, port);
                    }
                    catch (ConnectException ex) {
                        logAppend(consoleLog, "该地址与端口并未配置服务器");
                        successOpen = false;
                    }
                    if (successOpen)
                        logAppend(consoleLog, "成功与服务端建立连接");
                }
                else
                    logAppend(consoleLog, "不能重复创建客户端程序");
            }
        });

        closeServerButton.addMouseListener(new MouseAdapter() {     //关闭客户端
            @Override
            public void mouseClicked(MouseEvent e) {
                if (client != null) {
                    logAppend(consoleLog, "客户端被关闭");
                    client.setHasLogin(false);
                    client.exit();
                    client = null;//断开连接
                }
                else
                    logAppend(consoleLog, "客户端未被创建");
            }
        });

        confirmPortButton.addMouseListener(new MouseAdapter() {     //更新地址与端口号
            @Override
            public void mouseClicked(MouseEvent e) {
                if (client != null)
                    logAppend(consoleLog, "不能在客户端启动时更新地址与端口号");
                else {
                    serverName = serverNameInput.getText();
                    port = Integer.parseInt(portInput.getText());
                    logAppend(consoleLog, "地址更新为: " + serverName + ", 端口号更新为: " + port);
                }
            }
        });

        sendMessageButton.addMouseListener(new MouseAdapter() {   //发送信息
            @Override
            public void mouseClicked(MouseEvent e) {
                if (client.isLogin()) {
                    client.sendMessage(sendMessageBox.getText());
                }
                else
                    logAppend(consoleLog, "当前未登录或未与服务器建立连接");
            }
        });

        loginCheckButton.addMouseListener(new MouseAdapter() {  //身份验证
            @Override
            public void mouseClicked(MouseEvent e) {
                if (client == null) {
                    logAppend(consoleLog, "当前未与服务器建立连接");
                }
                else {
                    Login dialog = new Login(client, consoleLog);
                    dialog.pack();
                    dialog.setVisible(true);
                }
            }
        });
    }

    private void onCancel() {
        // 必要时在此处添加您的代码
        closeServerButton.doClick();
        dispose();
    }

    public static void main(String[] args) {
        ClientMainMenu dialog = new ClientMainMenu();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
