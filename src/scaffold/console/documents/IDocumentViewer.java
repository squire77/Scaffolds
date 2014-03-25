package scaffold.console.documents;

import java.awt.Dimension;
import javax.swing.JComponent;

public interface IDocumentViewer {
    void setPreferredSize(Dimension d);
    JComponent getComponent();

    boolean hasChanged();
    void hasChanged(boolean hasChanged);
    String getDocumentTitle();
    String getSubDir();
    String getFileName();
    
    void newFile(String fileName);
    void openFile(String fileName);
    void saveAsFile(String fileName);
    void saveFile();
}
