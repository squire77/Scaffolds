package scaffold.console;

import scaffold.console.documents.TextDocumentViewer;
import javax.swing.*;

public class ErrorDialog {
    public static ErrorDialog getInstance() {
        if (null == instance)
            instance = new ErrorDialog();
        return instance;
    }

    private ErrorDialog() {
        this.logs = new TextDocumentViewer();
    }

    public TextDocumentViewer getLogs() {
        return logs;
    }

    public static void error(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void error(String message, String detail) {
        logs.setText("");
        if (message.length() < 500)
            JOptionPane.showMessageDialog(null, message, "Scaffold Error", JOptionPane.ERROR_MESSAGE);
        logs.getPrintWriter().println(detail);
    }

    public void error(String message, Exception ex) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        logs.setText("");
        ex.printStackTrace(logs.getPrintWriter());
    }

    private static ErrorDialog instance = null;
    private TextDocumentViewer logs;
}
