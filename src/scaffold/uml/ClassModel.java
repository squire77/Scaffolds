package scaffold.uml;

import scaffold.codegen.CmGenerator;
import scaffold.uml.basic.UmlType;
import scaffold.uml.basic.UmlPackage;
import scaffold.uml.basic.cm.CmClass;
import scaffold.uml.basic.cm.CmAssociation;
import scaffold.fileexplorer.FileUtility;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


public class ClassModel {   
    public static void initialize() {
        UmlPackage.initialize(); 
        UmlType.initialize();
        CmClass.initialize();
        CmAssociation.initialize();
    }
    
    //*** GENERATE ***********************************************************

    public static String generate(String projectDir, String classTemplate) throws Exception {
        StringBuilder strb = new StringBuilder();
        
        StringBuilder classDir = new StringBuilder(projectDir + File.separator + "src");
        File dir = new File(classDir.toString());                
        
        if (!dir.exists()) {
            dir.mkdirs();
        } else {
            FileUtility.deleteFilesInDirectory(dir, true);            
        }
        
        for (CmClass c: CmClass.getAllClasses().values()) {
            StringBuilder filePath = new StringBuilder(classDir);
            
            UmlPackage packge = UmlPackage.getPackage(c.getPackage());
            if (!packge.getName().equals("default")) {
                filePath.append(File.separator);
                filePath.append(packge.getName());
            }
            
            File path = new File(filePath.toString());
            if (!path.exists()) {
                path.mkdirs();
            }
            
            filePath.append(File.separator);
            filePath.append(c.getName());
            filePath.append(".java");
            
            String programData = generator.generateClass(c, classTemplate);
            strb.append(programData);                       
            
            FileUtility.writeFile(filePath.toString(), programData);
        }
        
        return strb.toString();
    }
    
    //*** SAVE / RESTORE *****************************************************
    
    public static void writeToFile(String dir) throws JAXBException {
        FileUtility.deleteFilesInDirectory(new File(dir), false);
        
        //write the packages
        JAXBContext packageContext = JAXBContext.newInstance(UmlPackage.class);
        Marshaller pm = packageContext.createMarshaller();
        pm.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);         
        for (String idStr: UmlPackage.getAllPackages().keySet()) {
            UmlPackage p = UmlPackage.getAllPackages().get(idStr);
            
            //do not write the primitive types package
            if (p.getFullPackageName().equals("infrastructure.core.primitivetypes")) {
                continue;
            }
            
            pm.marshal(p, new File(dir + "package-" + p.getName() + idStr + ".xml"));
        }
        
        //write the types                 
        for (String idStr: UmlType.getAllTypes().keySet()) {                        
            //primitive types are not stored to disk
            if (UmlType.isPrimitiveType(idStr)) {
                continue;
            }
            
            UmlType type = UmlType.getAllTypes().get(idStr);
            
            //classes are stored separately
            if (type instanceof CmClass) {
                continue;
            }
        
            JAXBContext typeContext = JAXBContext.newInstance(type.getClass());
            Marshaller tm = typeContext.createMarshaller();         
            tm.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            tm.marshal(type, new File(dir + "type-" + type.getClass().getName() + idStr + ".xml"));
        }        
        
        //write the classes
        JAXBContext classContext = JAXBContext.newInstance(CmClass.class);
        Marshaller cm = classContext.createMarshaller();
        cm.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);   
        for (String idStr: CmClass.getAllClasses().keySet()) {
            CmClass c = CmClass.getAllClasses().get(idStr);
            cm.marshal(c, new File(dir + "class-" + c.getName() + idStr + ".xml"));
        }
        
        //write the associations
        JAXBContext assocContext = JAXBContext.newInstance(CmAssociation.class);
        Marshaller am = assocContext.createMarshaller();
        am.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);         
        for (String idStr: CmAssociation.getAllAssociations().keySet()) {
            CmAssociation assoc = CmAssociation.getAllAssociations().get(idStr);
            am.marshal(assoc, new File(dir + "assoc-" + idStr + ".xml"));
        }
    }
    
    public static void readFromFile(String dir) throws JAXBException, IOException, ClassNotFoundException {                
        initialize();
        
        JAXBContext context;
        Unmarshaller um;
        File[] files;
        
        //read the packages
        context = JAXBContext.newInstance(UmlPackage.class);
        um = context.createUnmarshaller();         
        files = FileUtility.getFilesStartingWith(dir, "package-");        
        for (File f: files) {           
            UmlPackage p = (UmlPackage) um.unmarshal(f);
            
            //note: this will replace the default package created during initialization
            UmlPackage.getAllPackages().put(p.getID(), p);
        }
        
        //read the types              
        files = FileUtility.getFilesStartingWith(dir, "type-");        
        for (File f: files) { 
            String name = f.getName();
            String typeName = name.substring(5,name.indexOf("TYPE_"));
            context = JAXBContext.newInstance(Class.forName(typeName));
            um = context.createUnmarshaller();   
            UmlType t = (UmlType) um.unmarshal(f);
            UmlType.getAllTypes().put(t.getID(), t);
        }
            
        //read the classes
        context = JAXBContext.newInstance(CmClass.class);
        um = context.createUnmarshaller();         
        files = FileUtility.getFilesStartingWith(dir, "class-");        
        for (File f: files) {           
            CmClass c = (CmClass) um.unmarshal(f);
            CmClass.getAllClasses().put(c.getID(), c);  
            
            //store in the list of known types
            UmlType.getAllTypes().put(c.getID(), c);
        }
        
        //associate any super classes referenced by ID
        for (CmClass c: CmClass.getAllClasses().values()) {
            List<String> superClassIDs = new ArrayList<String>();
            
            //copy out the classIDs, before we clear the list
            for (String classID: c.getSuperClassIDs()) {
                superClassIDs.add(classID);
            }
            
            // we'll add them back as we add the classes
            c.getSuperClassIDs().clear(); 
            
            for (String classID: superClassIDs) {
                CmClass superClass = CmClass.getClass(classID);
                
                if (superClass == null) {
                    throw new IOException("unknown super class " + classID + " encountered loading class: " + c.getName());
                }
                
                //add super class
                if (!c.addSuperClass(superClass)) {
                    throw new IOException("problem adding super class " + superClass.getName() + " while loading class: " + c.getName());
                }
            }
        }
        
        //read the associations
        context = JAXBContext.newInstance(CmAssociation.class);
        um = context.createUnmarshaller();         
        files = FileUtility.getFilesStartingWith(dir, "assoc-");        
        for (File f: files) {           
            CmAssociation assoc = (CmAssociation) um.unmarshal(f);
            CmAssociation.getAllAssociations().put(assoc.getID(), assoc);            
        }
        
        //add endPoints to classes referenced by ID
        for (CmAssociation assoc: CmAssociation.getAllAssociations().values()) {
            CmClass c1 = assoc.getEndPoint1().getClazz();
            CmClass c2 = assoc.getEndPoint2().getClazz();
            c1.addEndPoint(assoc.getEndPoint1());
            c2.addEndPoint(assoc.getEndPoint2());
        }
    }              

    //*** PRIVATE VARIABLES **************************************************
    
    private static CmGenerator generator = new CmGenerator();                     
}

