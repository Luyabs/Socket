package gui_menu.util;

import javax.swing.*;

import java.io.*;

import static global_param.GlobalParam.FILE_NOT_EXIST;
import static global_param.GlobalParam.MAX_ROW;

public class Util {

    public static void freshLog(JTextArea consoleLog) {
        if (consoleLog.getLineCount() > MAX_ROW) {
            String beforeText = consoleLog.getText();
            int i;
            for (i = 0; i < beforeText.length(); i++) {
                if (beforeText.charAt(i) == '\n')
                    break;
            }
            consoleLog.setText(beforeText.substring(i + 1));
        }
    }

    public static void logAppend(JTextArea consoleLog, String str) {
        if (consoleLog == null) {
            System.out.println("有一方失去连接");
            return;
        }
        consoleLog.append(str + '\n');
        for (int i = 0; i < 3; i++) {
            freshLog(consoleLog);
        }
    }

    public static String transmitFile(String message) {
        try {
            File file = new File(message);
            Reader reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            StringBuilder str = new StringBuilder("____FILE____");    //标识符
            while ((tempchar = reader.read()) != -1) {
                if (((char) tempchar) != '\r') {
                    str.append((char) tempchar);
                }
            }
            reader.close();
            return str.toString();
        }
        catch (IOException e) {
            return FILE_NOT_EXIST;
        }
    }
}
