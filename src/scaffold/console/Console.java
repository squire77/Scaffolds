package scaffold.console;

import scaffold.console.documents.DocumentManager;
import scaffold.console.documents.IDocumentManager;
import scaffold.console.documents.DocumentViewer;
import scaffold.fileexplorer.FileUtility;
import scaffold.uml.ClassModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;


//
//           |--> title
// Console <>---> docMgr <>---------------> tabbedPane <>---------> GraphCanvas
//           |                         |                                |
//           |--> STATUS_LABEL         |--> explorerPane                |
//                                              modelExplorer ----------|
//                                              templateExplorer
//                                              generatedExplorer
//
public class Console extends JFrame {   
    public Console() {
        super(FRAME_TITLE);
        
        validLicense = false;
        
        try {
            String licenseKey = FileUtility.readFile(LICENSE_FILE_NAME);
            if (LicenseValidator.isValidLicenseKey(licenseKey)) {
                validLicense = true;
            }
        } catch (IOException e) {
        }

        if (!validLicense) {
            buildDisabledFrame(DEFAULT_FRAME_SIZE_WIDTH, DEFAULT_FRAME_SIZE_HEIGHT);
            return;
        }
        
        ClassModel.initialize();
        
        //build and layout components
        buildFrame(DEFAULT_FRAME_SIZE_WIDTH, DEFAULT_FRAME_SIZE_HEIGHT);
        layoutComponents(DEFAULT_FRAME_SIZE_WIDTH, DEFAULT_FRAME_SIZE_HEIGHT);
        addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent event){
                layoutComponents(getWidth()-30, getHeight()-120);
                centerPane.revalidate();
                docMgr.getExplorerComponent().revalidate();
                docMgr.getTabbedDocumentComponent().revalidate();
                docMgr.getModelViewer().getComponent().revalidate();
                repaint();
            }
        });        		        
		
        docMgr.setViewToModels();
	docMgr.openMostRecentFiles();
        
        String rootDir = docMgr.getTemplateFileExplorer().getRootDir();
        
        try {
            docMgr.getTemplateViewer().setText(FileUtility.readFile(rootDir + "/java-class.tem"));
        } catch (IOException ex) {
            ErrorDialog.getInstance().error("Problem opening template: java-class.tem", ex);
        }
    }
    public void setDocumentTitle(String title) {
        if (title.startsWith("<"))
        {
            super.setTitle(FRAME_TITLE + " - [" + title.substring(1, title.length()-1) + "]");
        }
        else
        {
            String fileName = FileUtility.extractFileName(title);
            super.setTitle(FRAME_TITLE + " - [" + fileName + "]");
        }
    }

    public IDocumentManager getDocMgr() {
        return this.docMgr;
    }
    
    private void buildDisabledFrame(int w, int h) {
        //setLocationRelativeTo(null);
        //setLocation(200, 80);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width-w)/2, (dim.height-h)/2);
        
        //create menu bar
        setJMenuBar(createDisabledMenuBar());

        //create center pane
        centerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, null);
        centerPane.setContinuousLayout( true );
        getContentPane().add(centerPane, BorderLayout.NORTH);

        //initialize status bar
        STATUS_LABEL.setBorder(new EtchedBorder());
        getContentPane().add(STATUS_LABEL, BorderLayout.SOUTH); 
    }
    
    private JMenuBar createDisabledMenuBar() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add(exit);
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dispose();
                System.exit(0);
            }
        } );

        JMenu modelMenu = new JMenu("Model");
        JMenu generateMenu = new JMenu("Generate");        
        JMenu helpMenu = new JMenu( "Help" );
        
        JMenu aboutMenu = new JMenu( "About" );
        JMenuItem license = new JMenuItem( "License..." );
        aboutMenu.add(license);
        
        license.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String licenseKey = (String)JOptionPane.showInputDialog(Console.this, "Enter License Key:");
                
                if (LicenseValidator.isValidLicenseKey(licenseKey)) {
                    validLicense = true;
                    
                    try {
                        FileUtility.writeFile(LICENSE_FILE_NAME, licenseKey);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(Console.this, 
                           "Problem writing to license file: " + LICENSE_FILE_NAME + ".\n\n" +
                                "To enable your product without the need to re-enter your license key, " + 
                                "please paste your license key into a file named \"license.txt\" and " +
                                "place the file in the installation directory.",
                            "Problem writing license key",
                            JOptionPane.WARNING_MESSAGE);
                    }
                    
                    JOptionPane.showMessageDialog(Console.this,
                        "Congratulations!\nThank you for purchasing " + PRODUCT_NAME + " by " + COMPANY_NAME + "." +
                            "\n\nPlease restart " + PRODUCT_NAME + ".",
                        "License Key Success",                        
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(Console.this,
                        "Invalid license key." + 
                            "\nPlease purchase a valid license from http://www.pluggabledesignsoftware.com",
                        "License Key Failure",                        
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } );
        
        // menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(modelMenu);
	menuBar.add(generateMenu);
        menuBar.add(helpMenu);
        menuBar.add(aboutMenu);

        return menuBar;
    }
    
    private void buildFrame(int w, int h) {
        //setLocationRelativeTo(null);
        //setLocation(200, 80);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width-w)/2, (dim.height-h)/2);
		
        //create tabbed document viewers
        projectData = ProjectData.openMostRecentProject(); //create a new project for scaffolds
        docMgr = new DocumentManager(this, projectData);

        //create menu bar
        setJMenuBar(createMenuBar());

        //create center pane
        centerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        docMgr.getExplorerComponent(), docMgr.getTabbedDocumentComponent());
        centerPane.setContinuousLayout( true );
        getContentPane().add(centerPane, BorderLayout.NORTH);

        //initialize status bar
        STATUS_LABEL.setBorder(new EtchedBorder());
        getContentPane().add(STATUS_LABEL, BorderLayout.SOUTH);       
    }

    private void layoutComponents(int width, int height) {
        docMgr.getTabbedDocumentComponent().setPreferredSize(new Dimension(width, height));

        //resize fileExplorerPane
        Dimension dim = new Dimension (200, height-10);
        docMgr.getExplorerComponent().setPreferredSize(dim);

        //inset docs within tabbedPane
        dim = new Dimension (width-20, height-10);
        docMgr.getLogViewer().setPreferredSize(dim);
        docMgr.getTemplateViewer().setPreferredSize(dim);
        docMgr.getGeneratedViewer().setPreferredSize(dim);
        docMgr.getStartViewer().setPreferredSize(dim);

        //update graphical view component
        docMgr.getModelViewer().getComponent().setPreferredSize(dim);
    }

    private JMenuBar createMenuBar() {
        //"File" menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem openProject = new JMenuItem("Open Project");
        fileMenu.add(openProject);
        JMenuItem saveProject = new JMenuItem("Save Project");
        fileMenu.add(saveProject);
        fileMenu.insertSeparator(2);
        JMenuItem newFile = new JMenuItem("New");
        fileMenu.add(newFile);
        JMenuItem openFile = new JMenuItem("Open");
        fileMenu.add(openFile);
        JMenuItem saveFile = new JMenuItem("Save");
        fileMenu.add(saveFile);
        JMenuItem saveFileAs = new JMenuItem("Save As...");
        fileMenu.add(saveFileAs);
        fileMenu.insertSeparator(7);
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add(exit);
        newFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                docMgr.newFile(DocumentViewer.DEFAULT_NEW_FILENAME);
            }
        } );
        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                FC.setCurrentDirectory(new File(projectData.getDirectory() + 
                                                docMgr.getCurrentDoc().getSubDir()));
                int returnVal = FC.showOpenDialog(Console.this);

                if (returnVal == JFileChooser.APPROVE_OPTION){
                    File file = FC.getSelectedFile();
                    
                    if (file != null)
                        docMgr.openFile(file.getAbsolutePath());
                }
            }
        } );
        saveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                docMgr.saveFile();
            }
        } );
        saveFileAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                FC.setCurrentDirectory(new File(projectData.getDirectory() + 
                                                docMgr.getCurrentDoc().getSubDir()));
                int returnVal = FC.showSaveDialog(Console.this);

                if (returnVal == JFileChooser.APPROVE_OPTION){
                    File file = FC.getSelectedFile();

                    if (file != null) {
                        docMgr.saveFileAs(file.getAbsolutePath());
                        
                        //update file explorers to show new files got added
                        docMgr.refreshDirectories();
                    }
                }
            }
        } );
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                docMgr.saveAllChanges(true);
                dispose();
                System.exit(0);
            }
        } );

        //"Model" menu
        JMenu modelMenu = new JMenu("Model");
        JMenuItem addClass = new JMenuItem("Add Class");
        modelMenu.add(addClass);
        addClass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                docMgr.getModelViewer().getUMLCanvas().addUMLClass(false, 200, 400);
            }
        } );
        JMenuItem addInterface = new JMenuItem("Add Interface");
        modelMenu.add(addInterface);
        addInterface.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                docMgr.getModelViewer().getUMLCanvas().addUMLClass(true, 200, 400);
            }
        } );
				
        //"Generate" menu
        JMenu generateMenu = new JMenu("Generate");
        JMenuItem generateCode = new JMenuItem("Generate Code");
        generateMenu.add(generateCode);
        generateCode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                generate();
                
                //update file explorers in case new files got added
                docMgr.refreshDirectories();
            }
        } );

        //"Help" menu
        JMenu helpMenu = new JMenu( "Help" );
        JMenuItem clicheHelp = new JMenuItem( "Cliche Overview" );
        JMenuItem classHelp = new JMenuItem( "Class Model Usage" );
        JMenuItem projectHelp = new JMenuItem( "Project Model Usage" );
        helpMenu.add(clicheHelp);
        helpMenu.add(classHelp);
        helpMenu.add(projectHelp);
        clicheHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                docMgr.getStartViewer().openFile("data/help/cliche_help.txt");
            }
        } );
        classHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                docMgr.getStartViewer().openFile("data/help/class_help.txt");
            }
        } );
        projectHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                docMgr.getStartViewer().openFile("data/help/project_help.txt");
            }
        } );
        
        //"About" menu
        JMenu aboutMenu = new JMenu( "About" );
        JMenuItem license = new JMenuItem( "License..." );
        aboutMenu.add(license);
        license.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {  
                String licenseKey = "";
                
                try {
                    licenseKey = FileUtility.readFile(LICENSE_FILE_NAME);
                    
                    JOptionPane.showMessageDialog(Console.this,
                        PRODUCT_NAME + " by " + COMPANY_NAME + "\n\n" + licenseKey,
                        "License Key",                        
                        JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(Console.this,                            
                        "Problem reading license key from " + LICENSE_FILE_NAME,
                        "License File Error",
                        JOptionPane.ERROR_MESSAGE);
                }                                        
            }
        } );        
        
        //set default start text
        docMgr.getStartViewer().openFile("data/help/class_help.txt");
  
        // menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(modelMenu);
	menuBar.add(generateMenu);
        menuBar.add(helpMenu);
        menuBar.add(aboutMenu);

        return menuBar;
    }

    private static void setStatusText(String s) {
        STATUS_LABEL.setText(s);
    }

    private void generate() {	
        docMgr.getGeneratedViewer().setText("");
        
        //Update the model
        String model;        
        try {                                 
            model = ClassModel.generate(projectData.getGeneratorTargetDirectory(), docMgr.getTemplateViewer().getText());
        } catch (Exception ex) {
            ErrorDialog.getInstance().error("Problem generating code.", ex);
            return;
        }

        docMgr.getGeneratedViewer().setText(model);       
    }
    
    private static final String         COMPANY_NAME = "Pluggable Design Software";
    private static final String         PRODUCT_NAME = Character.toString((char) 169) + " UML Simple CodeGen Assistant";
    private static final String         LICENSE_FILE_NAME = "license.txt";
    
    private static final String         FRAME_TITLE = "Scaffold Editor";
    private static final int            DEFAULT_FRAME_SIZE_WIDTH = 800;
    private static final int            DEFAULT_FRAME_SIZE_HEIGHT = 690;    
    private static final JLabel         STATUS_LABEL = new JLabel();
    private static final JFileChooser   FC = new JFileChooser();
            
    private ProjectData         projectData;    
    private IDocumentManager    docMgr;
    private JSplitPane          centerPane;
    private boolean             validLicense;
}
