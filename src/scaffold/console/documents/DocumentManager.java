package scaffold.console.documents;

import java.io.File;
import scaffold.console.Console;
import scaffold.console.ErrorDialog;
import scaffold.console.ProjectData;
import scaffold.fileexplorer.FileExplorer;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class DocumentManager implements IDocumentManager 
{
    public DocumentManager( Console theConsole, ProjectData theProject ) 
    {
        this.console = theConsole;
        this.project = theProject;
        this.tabbedPane = new JTabbedPane();

        //create documents                
        startViewer = new TextDocumentViewer();
        modelViewer = new ModelDocumentViewer();
        templateViewer = new TemplateViewer();
        generatedViewer = new TextDocumentViewer();
        logViewer = ErrorDialog.getInstance().getLogs();        

        //create file explorers
        modelFileExplorer = new FileExplorer(this);
        templateFileExplorer = new FileExplorer(this);
        generatedFileExplorer = new FileExplorer(this);
		
        modelFileExplorer.setRootDir(project.getDirectory() + File.separator + modelViewer.getSubDir());		
        templateFileExplorer.setRootDir(project.getDirectory() + File.separator + templateViewer.getSubDir());
        generatedFileExplorer.setRootDir(project.getGeneratorTargetDirectory());

        //set initial view
        explorerPane = new JScrollPane();
        explorerPane.setViewportView(modelFileExplorer.getComponent());
        currentDoc = modelViewer;

        tabbedPane.add("Start", startViewer.getComponent());               //0
        tabbedPane.add("Graphical Model", modelViewer.getComponent());     //1
        tabbedPane.add("Templates", templateViewer.getComponent());        //2
        tabbedPane.add("Generated Code", generatedViewer.getComponent());  //3
        tabbedPane.add("Logs", logViewer.getComponent());                  //4

        //add change listener to detect when select tab changes
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane tp = (JTabbedPane)e.getSource();

                int index = tp.getSelectedIndex();
                switch (index) {
                    case 0: //Start
                        currentDoc = startViewer;
                        break;
                    case 1: //Graphical Model
                        currentDoc = modelViewer;
                        explorerPane.setViewportView(modelFileExplorer.getComponent());
                        console.setDocumentTitle(currentDoc.getDocumentTitle());
                        break;
                    case 2: //Templates
                        currentDoc = templateViewer;
                        explorerPane.setViewportView(templateFileExplorer.getComponent());
                        console.setDocumentTitle(templateViewer.getDocumentTitle());
                        break;
                    case 3: //Generated Code
                        currentDoc = generatedViewer;
                        explorerPane.setViewportView(generatedFileExplorer.getComponent());
                        console.setDocumentTitle(generatedViewer.getDocumentTitle());
                        break;
                    default: //5: Logs
                        currentDoc = logViewer;                        
                }
            }
        });
    }

    @Override
    public IDocumentViewer getCurrentDoc() {
        return this.currentDoc;
    }
    
    @Override
    public void setViewToTemplates() {
        tabbedPane.setSelectedComponent(templateViewer.getComponent());
    }
	
    @Override
    public void setViewToModels() {
        tabbedPane.setSelectedComponent(modelViewer.getComponent());
    }    

    @Override
    public void openMostRecentFiles() {
        String fileName = project.getLastReadModelFileName();
        if (fileName.contains("<"))
            modelViewer.newFile(DocumentViewer.DEFAULT_NEW_FILENAME);
        else
            modelViewer.openFile(fileName);

        fileName = project.getLastReadTemplateFileName();
        if (fileName.contains("<"))
            templateViewer.newFile("<New File>");
        else
            templateViewer.openFile(fileName);

        fileName = project.getLastReadGeneratedFileName();
        if (fileName.contains("<"))
            generatedViewer.newFile("<New File>");
        else
            generatedViewer.openFile(fileName);
    }

    @Override
    public JComponent getExplorerComponent()        { return this.explorerPane; }
    @Override
    public JComponent getTabbedDocumentComponent()  { return this.tabbedPane; }
 
    @Override
    public TextDocumentViewer getStartViewer()       { return this.startViewer; }    
    @Override
    public ModelDocumentViewer getModelViewer()     { return this.modelViewer; }
    @Override
    public TemplateViewer getTemplateViewer()       { return this.templateViewer; }
    @Override
    public TextDocumentViewer getGeneratedViewer()  { return this.generatedViewer; }
    @Override
    public TextDocumentViewer getLogViewer()        { return this.logViewer; }

    @Override
    public FileExplorer getModelFileExplorer()      { return this.modelFileExplorer; }    
    @Override
    public FileExplorer getTemplateFileExplorer()   { return this.templateFileExplorer; }
    @Override
    public FileExplorer getGeneratedFileExplorer()  { return this.generatedFileExplorer; }

    @Override
    public void refreshDirectories() {
        templateFileExplorer.setRootDir(templateFileExplorer.getRootDir()); //HACK to refresh directories
        generatedFileExplorer.setRootDir(generatedFileExplorer.getRootDir()); //HACK to refresh directories
    }

    @Override
    public void saveAllChanges(boolean confirm) {
        saveDocument(modelViewer, confirm);
        saveDocument(templateViewer, confirm);
        saveDocument(generatedViewer, confirm);
    }
    
    @Override
    public void newFile(String fileName) {        
        currentDoc.newFile(fileName);
        console.setDocumentTitle(currentDoc.getDocumentTitle()); //update document title
        
        if (currentDoc == modelViewer) {
            modelFileExplorer.clear();
        }
    }
    @Override
    public void openFile(String fileName) {
        currentDoc.openFile(fileName);
        console.setDocumentTitle(currentDoc.getDocumentTitle()); //update document title
    }
    @Override
    public void saveFileAs(String newFileName) {
        currentDoc.saveAsFile(newFileName);
        console.setDocumentTitle(currentDoc.getDocumentTitle()); //update document title
    }
    @Override
    public void saveFile() {
        currentDoc.saveFile();
    }

    private void saveDocument(IDocumentViewer doc, boolean confirm) {
        if (doc.hasChanged() || doc.getFileName().startsWith("<")) {
            boolean saveFile = false;
            if (confirm) {
                 int n = JOptionPane.showConfirmDialog(console, "The file " +
                             doc.getFileName() + " has been modified. Save?",
                            "Save Confirmation", JOptionPane.YES_NO_OPTION);
                 if (JOptionPane.YES_OPTION == n)
                    saveFile = true;
            }
            if (saveFile) {
                doc.saveFile();
            }
        }
    }

    private Console                 console;
    private ProjectData         project;
    private IDocumentViewer         currentDoc;

    private JTabbedPane             tabbedPane;
    private TextDocumentViewer      startViewer;
    private ModelDocumentViewer     modelViewer;
    private TemplateViewer          templateViewer;
    private TextDocumentViewer      generatedViewer;
    private TextDocumentViewer      logViewer;    

    private FileExplorer            modelFileExplorer;
    private FileExplorer            templateFileExplorer;
    private FileExplorer            generatedFileExplorer;
    private JScrollPane             explorerPane;
}
