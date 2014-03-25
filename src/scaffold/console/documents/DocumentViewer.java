package scaffold.console.documents;

import java.awt.Dimension;
import javax.swing.JComponent;

abstract public class DocumentViewer implements IDocumentViewer {
    public static final String DEFAULT_NEW_FILENAME = "<New File>";
    
    public DocumentViewer() {
        this.hasChanged = false;
        this.fileName = DEFAULT_NEW_FILENAME;
    }
    
    public void reset() {
        this.hasChanged = false;
        this.fileName = DEFAULT_NEW_FILENAME;
    }

    @Override
    abstract public void setPreferredSize(Dimension d);
    @Override
    abstract public JComponent getComponent();

    @Override
    public boolean hasChanged() {
        return this.hasChanged;
    }
    @Override
    public void hasChanged(boolean hasChanged) {
        this.hasChanged = hasChanged;
    }
    @Override
    public String getDocumentTitle() {
        return (this.hasChanged) ? this.fileName + " *" : this.fileName;
    }
    @Override
    public String getFileName() {
        return this.fileName;
    }
    @Override
    public void newFile(String fileName) {
        doNewFile(fileName);
        setFileName(fileName); //change fileName AFTER successful new
    }
    
    //This is called by the document viewer which passes the full path name.
    @Override
    public void openFile(String fileName) {
        doOpenFile(fileName);
        setFileName(fileName); //change fileName AFTER successful open
    }
    @Override
    public void saveAsFile(String fileName) {
        doSaveAsFile(fileName);
        setFileName(fileName); //change fileName AFTER successful save
        hasChanged(false);
    }
    @Override
    public void saveFile() {
        saveAsFile(fileName);
    }

    abstract public void doNewFile(String fileName);
    abstract public void doOpenFile(String fileName);
    abstract public void doSaveAsFile(String fileName);

    protected void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private boolean         hasChanged;
    private String          fileName;
}
