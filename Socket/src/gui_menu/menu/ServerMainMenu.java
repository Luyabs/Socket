package gui_menu.menu;

import gui_menu.util.Util;
import socket.SocketServer;

import javax.swing.*;
import java.awt.event.*;

import static global_param.GlobalParam.DEFAULT_PORT;

public class ServerMainMenu extends JDialog {
    private int port;
    private SocketServer server;
    private JPanel contentPane;
    private JTextArea consoleLog;
    private JButton RunServerButton;
    private JButton CloseServerButton;
    private JTextField portInput;
    private JButton confirmPortButton;
    private JButton ShowStatusButton;

    public ServerMainMenu() {
        port = DEFAULT_PORT;
        server = null;
        consoleLog.append("====此处显示系统日志====\n默认端口号为: " + DEFAULT_PORT  + "\n");

        setAlwaysOnTop(true);
        setTitle("服务端主界面");
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

        RunServerButton.addMouseListener(new MouseAdapter() {       //运行服务端
            @Override
            public void mouseClicked(MouseEvent e) {
                if (server == null) {
                    server = new SocketServer(port, consoleLog);
                    server.start();
                    consoleLog.append("成功创建服务端程序\n");
                }
                else
                    consoleLog.append("不能重复创建服务端程序\n");
                freshLog();
            }
        });

        CloseServerButton.addMouseListener(new MouseAdapter() {     //关闭服务端
            @Override
            public void mouseClicked(MouseEvent e) {    //关闭服务端
                if (server != null) {
                    server.exit();
                    server = null;
                    consoleLog.append("服务端被关闭\n");
                }
                else
                    consoleLog.append("服务端未被创建\n");
                freshLog();
            }
        });

        confirmPortButton.addMouseListener(new MouseAdapter() {     //更新端口号
            @Override
            public void mouseClicked(MouseEvent e) {        //更新端口号
                if (server != null)
                    consoleLog.append("不能在服务端启动时更新端口号\n");
                else {
                    port = Integer.parseInt(portInput.getText());
                    consoleLog.append("端口号更新为: " + port + '\n');
                }
                freshLog();
            }
        });

        ShowStatusButton.addMouseListener(new MouseAdapter() {      //查看服务端状态
            @Override
            public void mouseClicked(MouseEvent e) {
                if (server == null)
                    consoleLog.append("服务端未被启动\n");
                else
                    server.showLocalStatus();
                freshLog();
            }
        });
    }

    private void freshLog() {       //删去最早历史日志
        Util.freshLog(consoleLog);
    }

    private void onCancel() {
        // 必要时在此处添加您的代码
        dispose();
    }

    public static void main(String[] args) {
        ServerMainMenu dialog = new ServerMainMenu();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
