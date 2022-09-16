package gui_menu.menu;

import pojo.User;
import socket.SocketClient;

import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;

import static gui_menu.util.Util.freshLog;

public class Login extends JDialog {
    private JTextArea consoleLog;
    private SocketClient client;
    private JPanel contentPane;
    private JButton loginButton;
    private JButton cancelButton;
    private JPasswordField passwordField;
    private JTextField usernameField;
    private JLabel username;
    private JLabel password;

    public Login(SocketClient client, JTextArea consoleLog) {
        this.consoleLog = consoleLog;
        this.client = client;

        setAlwaysOnTop(true);
        setTitle("用户登录");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(loginButton);

        cancelButton.addActionListener(e -> onCancel());

        // 点击 X 时调用 onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // 遇到 ESCAPE 时调用 onCancel()
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        loginButton.addMouseListener(new MouseAdapter() {   //尝试一次登录
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean success = client.loginOneTime(new User(usernameField.getText(), passwordField.getText()).toString());
                if (success) {//如果登陆成功 修改登陆状态
                    client.setHasLogin(true);
                    consoleLog.append("登陆成功\n");
                    freshLog(consoleLog);
                    onCancel();
                    return;
                }
                else
                    consoleLog.append("用户名或密码错误\n");
                freshLog(consoleLog);

            }
        });

        cancelButton.addMouseListener(new MouseAdapter() {  //取消登录
            @Override
            public void mouseClicked(MouseEvent e) {
                client.setHasLogin(false);
                onCancel();
            }
        });
    }

    private void onCancel() {
        // 必要时在此处添加您的代码
        dispose();
    }
}
