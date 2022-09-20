package gui_menu.util;

import javax.swing.*;

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
}
