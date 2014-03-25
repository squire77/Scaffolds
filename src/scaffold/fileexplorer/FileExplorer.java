package scaffold.fileexplorer;

import scaffold.console.documents.IDocumentManager;
import java.io.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.*;
import javax.swing.tree.TreeSelectionModel;


public class FileExplorer {
    public FileExplorer(IDocumentManager docMgr) {
        this.docMgr = docMgr;
    }
    
    public Component getComponent() { return fileTree; }
        
    public String getRootDir() { return this.rootDir; }
    
    public void clear() {
        fileTree.getTree().clearSelection();
        lastSelected = null;
    }
    
    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
        fileTree = new FileSystemTreePanel(rootDir);
        fileTree.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        fileTree.getTree().addTreeSelectionListener(new TreeListener());
        fileTree.getTree().addMouseListener(new MouseAdapter() {
            @Override  
            public void mouseClicked(MouseEvent e) {  
                int row = fileTree.getTree().getRowForLocation(e.getX(),e.getY());  
                if (row == -1) {
                    fileTree.getTree().clearSelection();  
                }
            }  
        } );
    }
    
    protected class TreeListener implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            File fileSysEntity = (File)e.getPath().getLastPathComponent();
            
            if (fileSysEntity == null) {
                return;
            }
            
            if (!fileSysEntity.isDirectory()) {
                if (lastSelected == fileSysEntity) {
                    //don't reopen a file we already have open
                } else {
                    docMgr.openFile(fileSysEntity.getAbsolutePath());
                    lastSelected = (File)e.getPath().getLastPathComponent();
                }
            }
        }                
    }

    private IDocumentManager    docMgr;
    private FileSystemTreePanel fileTree;
    private String              rootDir;
    private File                lastSelected;
}

