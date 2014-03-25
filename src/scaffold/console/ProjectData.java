package scaffold.console;

import scaffold.console.documents.IDocumentManager;
import scaffold.fileexplorer.FileUtility;

public class ProjectData 
{    
    public static ProjectData openMostRecentProject() {
        String projectFileName;
        try {
            projectFileName = FileUtility.readFile(MOST_RECENT_PROJECT);
        } catch (java.io.IOException ex) {
            ErrorDialog.getInstance().error("Unable to open file: " + MOST_RECENT_PROJECT, ex);
            return null;
        }

        return openProject(projectFileName);
    }
    public static ProjectData openProject(String projectFileName) {
        String projectDesc;
        try {
            projectDesc = FileUtility.readFile(projectFileName);
        } catch (java.io.IOException ex) {
            ErrorDialog.error("Unable to open project: " + projectFileName);
            return null;
        }

        String[] projectData = projectDesc.split("\n");
        //project data should have 6 elements: 
        //  name, projDir, modelFN, templateFN, generatedFN, genTargetDir
        if (projectData.length != 6) {
            ErrorDialog.error("Invalid project file: " + projectFileName);
            return null;
        }

        return new ProjectData(projectData[0], projectData[1], projectData[2], 
                projectData[3], projectData[4], projectData[5]);
    }

    public ProjectData(String name, String directory, String modelFN, String templateFN, String generatedFN, String genTargetDir) {
        this.projectName = name;
        this.projectDirectory = directory;
        this.lastReadModelFN = modelFN;
        this.lastReadTemplateFN = templateFN;
        this.lastReadGeneratedFN = generatedFN;		
	this.generatorTargetDirectory = genTargetDir;
    }
    public String getName() {
        return this.projectName;
    }
    public String getDirectory() {
        return this.projectDirectory;
    }
    public String getGeneratorTargetDirectory() {
        return this.generatorTargetDirectory;
    }
    public String getLastReadModelFileName() {
        return this.lastReadModelFN;
    }
    public String getLastReadTemplateFileName() {
        return this.lastReadTemplateFN;
    }
    public String getLastReadGeneratedFileName() {
        return this.lastReadGeneratedFN;
    }    
    public void saveProject(IDocumentManager docMgr) {        
        //THIS METHOD IS INCOMPLETE. FIX IT AND USE IT.
        String projectData = projectDirectory + "," + 
                "," + docMgr.getModelViewer().getFileName() +
                "," + docMgr.getTemplateViewer().getFileName() +
                "," + docMgr.getGeneratedViewer().getFileName() +
				"," + generatorTargetDirectory;

        try {
            FileUtility.writeFile(MOST_RECENT_PROJECT, projectDirectory + "\\" + projectName + "\\" + projectName + ".csv");
        } catch (java.io.IOException ex) {
            ErrorDialog.getInstance().error("Unable to open file: " + MOST_RECENT_PROJECT, ex);
        }
    }


    private static final String MOST_RECENT_PROJECT = "most_recent_project.txt";

    private String              projectName;
    private String              projectDirectory;
    private String              lastReadScaffoldFN;
    private String              lastReadModelFN;
    private String              lastReadTemplateFN;
    private String              lastReadGeneratedFN;    
    private String              generatorTargetDirectory;	
}
