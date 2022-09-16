package gui_menu.menu;

import socket.SocketServer;

import javax.swing.*;
import java.awt.event.*;

import static global_param.GlobalParam.DEFAULT_PORT;
import static gui_menu.util.Util.logAppend;

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
        logAppend(consoleLog, "====此处显示系统日志====\n默认端口号为: " + DEFAULT_PORT);

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
                    logAppend(consoleLog, "成功创建服务端程序");
                }
                else
                    logAppend(consoleLog, "不能重复创建服务端程序");
            }
        });

        CloseServerButton.addMouseListener(new MouseAdapter() {     //关闭服务端
            @Override
            public void mouseClicked(MouseEvent e) {    //关闭服务端
                if (server != null) {
                    server.exit();
                    server = null;
                    logAppend(consoleLog, "服务端被关闭");
                }
                else
                    logAppend(consoleLog, "服务端未被创建");
            }
        });

        confirmPortButton.addMouseListener(new MouseAdapter() {     //更新端口号
            @Override
            public void mouseClicked(MouseEvent e) {        //更新端口号
                if (server != null)
                    logAppend(consoleLog, "不能在服务端启动时更新端口号");
                else {
                    port = Integer.parseInt(portInput.getText());
                    logAppend(consoleLog, "端口号更新为: " + port);
                }
            }
        });

        ShowStatusButton.addMouseListener(new MouseAdapter() {      //查看服务端状态
            @Override
            public void mouseClicked(MouseEvent e) {
                if (server == null)
                    logAppend(consoleLog, "服务端未被启动");
                else
                    server.showLocalStatus();
            }
        });
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
