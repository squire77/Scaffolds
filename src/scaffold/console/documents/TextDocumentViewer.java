package scaffold.console.documents;

import scaffold.console.ErrorDialog;
import scaffold.fileexplorer.FileUtility;
import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.IOException;


public class TextDocumentViewer extends DocumentViewer {
    public TextDocumentViewer() {
        this.area = new JTextPane();//JavaEditorPane();
        this.scrollPane = new JScrollPane(area);
        this.writer = new TextDocumentPrintWriter(this, area);
    }
    
    @Override
    public void setPreferredSize(Dimension d) {
        scrollPane.setPreferredSize(d);
    }
    
    @Override
    public JComponent getComponent() {
        return scrollPane;
    }

    @Override
    public String getSubDir() {
        return "\\scaffolds";
    }
    
    @Override
    public void doNewFile(String fileName) {
        setText("");
    }
    @Override
    public void doOpenFile(String fileName) {
        try {
            setText(FileUtility.readFile(fileName));
        } catch (IOException ex) {
            ErrorDialog.getInstance().error("Unable to open file: " + fileName, ex);
        }
    }
    @Override
    public void doSaveAsFile(String fileName) {
        try {
            FileUtility.writeFile(fileName, getText());
        } catch (IOException ex) {
            ErrorDialog.getInstance().error("Unable to write to file: " + fileName, ex);
        }
    }

    //*** text specific methods

    public void setEditable(boolean e) {
        area.setEditable(e);
    }
    public void setText(String s){
        area.setText(s);
    }
    public String getText() {
        return area.getText();
    }
    public PrintWriter getPrintWriter() {
        return writer;
    }

    private TextDocumentPrintWriter     writer;
    private JScrollPane                 scrollPane;
    private JEditorPane                 area;
}
