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
}
