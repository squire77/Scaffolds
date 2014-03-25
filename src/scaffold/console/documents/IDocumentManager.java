package scaffold.console.documents;

import scaffold.fileexplorer.FileExplorer;
import javax.swing.JComponent;

public interface IDocumentManager {
    IDocumentViewer getCurrentDoc();
            
    void openMostRecentFiles();
    void setViewToModels();
    void setViewToTemplates();	

    JComponent getExplorerComponent();
    JComponent getTabbedDocumentComponent();

    TextDocumentViewer  getStartViewer();
    ModelDocumentViewer getModelViewer();
    TemplateViewer      getTemplateViewer();
    TextDocumentViewer  getGeneratedViewer();
    TextDocumentViewer  getLogViewer();    

    FileExplorer getModelFileExplorer();
    FileExplorer getTemplateFileExplorer();
    FileExplorer getGeneratedFileExplorer();

    void refreshDirectories();
    void saveAllChanges(boolean confirm);
    void newFile(String fileName);
    void openFile(String fileName);
    void saveFile();
    void saveFileAs(String newFileName);
}
