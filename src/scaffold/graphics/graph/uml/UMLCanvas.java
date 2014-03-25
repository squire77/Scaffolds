package scaffold.graphics.graph.uml;

import scaffold.console.ErrorDialog;
import scaffold.fileexplorer.FileUtility;
import scaffold.uml.ClassModel;
import scaffold.uml.basic.UmlPackage;
import scaffold.uml.basic.cm.CmClass;
import scaffold.uml.basic.cm.CmAssociation;
import scaffold.graphics.graph.GraphCanvas;
import scaffold.graphics.graph.Node;
import scaffold.graphics.graph.Link;
import scaffold.graphics.draggable.IDraggable;
import java.util.ArrayList;
import java.util.List;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import scaffold.graphics.graph.uml.UMLNode.UMLNodeType;
import scaffold.uml.UmlIdentifiable;


public class UMLCanvas extends GraphCanvas {  
    public static UMLCanvas create() {
        UMLCanvas canvas = new UMLCanvas();
        canvas.init();
        canvas.drawObjects();
        return canvas;
    }
    
    //call create() instead to ensure init() is called
    private UMLCanvas() {        
    }
    
    //must call after calling constructor
    @Override
    public void init() {
        super.init();
        
        grid.setSpacing(12);
        showGrid(false);
        snapToGrid(true);
    }   
    
    @Override
    public void reset() {
        super.reset();
        storedClasses.clear();
    }    

    @Override
    protected void createGroupSelectionPopupMenu() {  
        super.createGroupSelectionPopupMenu();
        
        MenuItem createPackageM = new MenuItem("Create Package");
        createPackageM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {                  
                UMLPackageNode umlP = addUMLPackage(groupSelectionBounds.x, groupSelectionBounds.y,
                        groupSelectionBounds.width, groupSelectionBounds.height);
                
                if (umlP == null) {
                    return;
                }
                
                //add nodes to graphical package
                for (IDraggable d: savedSelection) {  
                    umlP.addNode((UMLNode) d);
                }
                
                UmlPackage p = umlP.getModelElement();

                //add elements to the model
                for (IDraggable d: groupSelection) {                               
                    if (d instanceof CmClass) {
                        p.addOwnedType(((CmClass) d).getID());
                    } else {
                        if (d instanceof UmlPackage) {
                            p.addNestedPackage(((UmlPackage) d).getID());                                
                        } else {
                            System.out.println( "DraggableCanvas::popupEvent, Attempt to add illegal object type to package");
                        }                        
                    }
                }
                                 
                //we have converted the group selection into a package so clear the selection
                clearGroupSelection();
                        
                drawObjects();
            }
        } ); 
        
        groupSelectionPopup.add(createPackageM);
    }
    
    //*** ADD UML PACKAGE *****************************************************
    
    public UMLPackageNode addUMLPackage(int posX, int posY, int sizeX, int sizeY) {
        String name = JOptionPane.showInputDialog(null, "Please enter a unique name:");
        
        //create the model element
        UmlPackage packge = UmlPackage.create(name);
        if ( packge == null) {
            ErrorDialog.error("Duplicate name " + name + ". Please choose a different name.");
            return null;
        }
        
        return addUMLPackage(posX, posY, sizeX, sizeY, packge);        
    }
    
    private UMLPackageNode addUMLPackage(int posX, int posY, int sizeX, int sizeY, UmlPackage packge) {               
        // create the GUI element
        UMLPackageNode p = UMLPackageNode.create(graph, this, packge, sizeX, sizeY);
        p.translate(posX, posY);        
        addObject(p); //add to canvas        
        drawObjects(); //re-draw in case snap moves the objects
        
        return p;
    }    
    
    //*** ADD UML CLASS *******************************************************
    
    public UMLClassNode addUMLClass(boolean isInterface, int posX, int posY) {
        String name = JOptionPane.showInputDialog(null, "Please enter a unique name:");
        
        //create the model element
        CmClass clazz = CmClass.create(name, isInterface);
        if ( clazz == null) {
            ErrorDialog.error("Duplicate name " + name + ". Please choose a different name.");
            return null;
        }
        
        return addUMLClass(posX, posY, clazz);        
    }
    
    private UMLClassNode addUMLClass(int posX, int posY, CmClass clazz) {               
        // create the GUI element
        UMLClassNode c = UMLClassNode.create(graph, this, clazz);
        c.translate(posX, posY);        
        addObject(c); //add to canvas        
        drawObjects(); //re-draw in case snap moves the objects
        
        return c;
    }
    
    //*** ADD UML INHERITANCE LINK ********************************************
    
    public UMLLink addUMLInheritanceLink(boolean isImplements,
            UMLEndPoint e1, UMLEndPoint e2, int ep1Location, int ep2Location, double percent) {   
        UMLLink link = UMLLink.create(UMLLink.LinkType.INHERITANCE, graph, e1, e2, null);
        
        e1.setLocationByPercent(ep1Location, percent);
        e2.setLocationByPercent(ep2Location, percent); 
        
        addObject(e1);
        addObject(e2);
        addObject(link);
                
        drawObjects(); //re-draw in case snap moves the objects
        
        return link;
    }
    
    //*** ADD UML ASSOCIATION LINK ********************************************
    
    public UMLLink addUMLAssocOrAggregationLink(boolean isAggregation,
            UMLEndPoint ep1, UMLEndPoint ep2, int ep1Location, int ep2Location, double percent) { 
        UMLClassNode clazz1 = (UMLClassNode) ep1.getNode();
        UMLClassNode clazz2 = (UMLClassNode) ep2.getNode();
        
        //create the model element        
        CmAssociation assoc = CmAssociation.create(
                isAggregation,
                ep1.getEPType() == UMLEndPoint.EPType.AGGREGATE,
                ep2.getEPType() == UMLEndPoint.EPType.AGGREGATE,
                clazz1.getModelElement(), clazz2.getModelElement());
        
        UMLLink link;
        
        if (isAggregation) {
            link = UMLLink.create(UMLLink.LinkType.AGGREGATION, graph, ep1, ep2, assoc);  
        } else {
            link = UMLLink.create(UMLLink.LinkType.ASSOCIATION, graph, ep1, ep2, assoc); 
        }
        
        double percent1 = percent;
        double percent2 = percent;

        //check for self association        
        if (ep1Location == ep2Location) {
            if (1.0 - percent1 - 0.2 > 0.0) {
                percent2 = percent1 + 0.2;
            } else {
                if (percent1 - 0.2 >= 0.0) {
                    percent2 = percent1 - 0.2;
                } else {
                    percent2 = 0.0;
                }
            }            
        }
        
        ep1.setLocationByPercent(ep1Location, percent1);
        ep2.setLocationByPercent(ep2Location, percent2); 
        
        addObject(ep1);
        addObject(ep2);
        addObject(link);
                
        drawObjects(); //re-draw in case snap moves the objects
        
        return link;
    }
           
    //*** UML GRAPH READ/WRITE TO FILE SYSTEM **************************   
    
    @Override
    public void writeGraph(String graphFileName) {
        try {
            ClassModel.writeToFile(FileUtility.extractDir(graphFileName)+"data\\");
        } catch (Exception e) {
            ErrorDialog.error("Unable to save model to file: " + FileUtility.extractFileName(graphFileName));
        }
        
        super.writeGraph(graphFileName);
    }
    
    @Override
    public void readGraph(String graphFileName) {  
        long startTime = System.currentTimeMillis();
        try {
            ClassModel.readFromFile(FileUtility.extractDir(graphFileName)+"data\\");
        } catch (Exception e) {
            ErrorDialog.error("Unable to read model from file: " + FileUtility.extractFileName(graphFileName));
            return;
        }        
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Time to load model: " + totalTime/1000 + "." + totalTime%1000);        
        
        super.readGraph(graphFileName);
    }
    
    @Override
    protected void doWriteNodeData(Node node, StringBuilder strBuf) {
        UMLNode umlNode = (UMLNode) node;
        
        strBuf.append(",");
        strBuf.append(umlNode.getNodeType());
        strBuf.append(",");
        strBuf.append(((UmlIdentifiable)umlNode.getModelElement()).getID());
    }
    
    @Override
    protected void doWriteLinkData(Link link, StringBuilder strBuf) {   
        UMLLink umlLink = (UMLLink) link;      
        
        strBuf.append(",");
        strBuf.append(umlLink.getLinkType());
        strBuf.append(",");                  
        strBuf.append(umlLink.getId());
        strBuf.append(",");                  
        strBuf.append(umlLink.getEndPoint1().getEPType());
        strBuf.append(",");                  
        strBuf.append(umlLink.getEndPoint2().getEPType());
    }
    
    @Override
    protected void doCreateNode(String[] nodeElements) {
        if (nodeElements.length < 3) {
            ErrorDialog.error("Invalid graph file");
            return;
        }
        
        if (UMLNodeType.valueOf(nodeElements[2]) == UMLNodeType.CLASS_NODE_TYPE) {
            CmClass clazz = CmClass.getClass(nodeElements[3]);
        
            if (clazz == null) {
                ErrorDialog.error("Unable to read graph: unknown class ID " + nodeElements[2]);
                return;
            } 

            UMLClassNode newClass = addUMLClass(Integer.parseInt(nodeElements[0]), 
                                            Integer.parseInt(nodeElements[1]), clazz);

            //save node for reference by links
            storedClasses.add(newClass);
        }
    }
    
    @Override
    protected void doCreateLink(String[] linkElements) {
        if (storedClasses.isEmpty()) {
            //this happens if the serialized model in incompatible {
            ErrorDialog.error("Empty or corrupted model file");
            return;
        }
        
        int classIndex1 = Integer.parseInt(linkElements[0]);
        int classIndex2 = Integer.parseInt(linkElements[1]);
        
        if (linkElements.length < 7 ||
            classIndex1 < 0 || classIndex1 > 6 || classIndex2 < 0 || classIndex2 > 6){
                
            ErrorDialog.error("Invalid graph file");
            return;
        }
        
        addUMLLink(UMLLink.LinkType.valueOf(linkElements[6]), linkElements[7],
                   UMLEndPoint.EPType.valueOf(linkElements[8]), UMLEndPoint.EPType.valueOf(linkElements[9]),
                   storedClasses.get(classIndex1), 
                   storedClasses.get(classIndex2),                                                           
                   Integer.parseInt(linkElements[2]), Double.parseDouble(linkElements[3]),                                                                                          
                   Integer.parseInt(linkElements[4]), Double.parseDouble(linkElements[5])
                   );                                                                    
    }                                  
    
    private UMLLink addUMLLink(
            UMLLink.LinkType ltype, String assocLinkID,
            UMLEndPoint.EPType ept1, UMLEndPoint.EPType ept2,
            UMLClassNode clazz1, UMLClassNode clazz2, 
            int location1, double percent1, 
            int location2, double percent2) {                                           
        UMLEndPoint ep1 = new UMLEndPoint(ept1, (UMLClassNode) clazz1, this);        
        UMLEndPoint ep2 = new UMLEndPoint(ept2, (UMLClassNode) clazz2, this);                 
        
        //associate stored GUI with stored model
        CmAssociation assoc = CmAssociation.getAssociation(assocLinkID);
        
        UMLLink link = UMLLink.create(ltype, graph, ep1, ep2, assoc);
        
        //recalculate locations
        ep1.setLocationByPercent(location1, percent1);
        ep2.setLocationByPercent(location2, percent2);         
        
        addObject(ep1);
        addObject(ep2);
        addObject(link);
        //drawObjects(); this is redundant since addObject already redraws()
        
        return link;
    }       
    
    private List<UMLClassNode> storedClasses = new ArrayList<UMLClassNode>();
}
