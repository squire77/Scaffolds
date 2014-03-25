package scaffold.graphics.graph.uml;

import scaffold.graphics.graph.EndPoint;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class LinkToControl extends Control {
    public static final int TOP=0, BOTTOM=1, RIGHT=2, LEFT=3;
      
    public LinkToControl(UMLCanvas canvas, UMLClassNode node, int location) {        
        this.umlCanvas = canvas;
        this.node = node;
        this.location = location;
        
        popup = new JPopupMenu("Options");
        JMenuItem implement = new JMenuItem("Implement Interface");
        JMenuItem inherit = new JMenuItem("Inherit from Super Class");        
        JMenuItem aggregate = new JMenuItem("Aggregate");
        JMenuItem aggregateToSelf = new JMenuItem("Aggregate to Self");
        JMenuItem associate = new JMenuItem("Associate");
        JMenuItem associateToSelf = new JMenuItem("Associate to Self");
        popup.add(implement);
        popup.add(inherit);
        popup.add(aggregate);
        popup.add(aggregateToSelf);
        popup.add(associate);
        popup.add(associateToSelf);        
        implement.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                linkToNewClass(0, releaseX, releaseY);
            }
        } );
        inherit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                linkToNewClass(1, releaseX, releaseY);
            }
        } );
        aggregate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                linkToNewClass(2, releaseX, releaseY);
            }
        } );       
        aggregateToSelf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                linkToNewClass(3, releaseX, releaseY);
            }
        } ); 
        associate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                linkToNewClass(4, releaseX, releaseY);
            }
        } );   
        associateToSelf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                linkToNewClass(5, releaseX, releaseY);
            }
        } );   
    }        

    @Override
    public void mousePressed(MouseEvent event) {
        pressX = event.getX();
        pressY = event.getY();
    }
    
    @Override
    public void mouseReleased(MouseEvent event) {
        releaseX = event.getX();
        releaseY = event.getY();  
        
        if (pressX == releaseX && pressY == releaseY) {              
            popup.show(umlCanvas, releaseX, releaseY);                                
        }
    }    
    
    @Override
    public Polygon getPolygon() {
        Polygon rect = new Polygon();

        int leftX = node.getPolygon().xpoints[0];
        int rightX = node.getPolygon().xpoints[1];
        int topY = node.getPolygon().ypoints[1];
        int bottomY = node.getPolygon().ypoints[2];  

        int thickness = 4;
        
        //create polygon relative to base shape
        if (location == TOP) {
            rect.addPoint(leftX, topY+thickness);
            rect.addPoint(rightX, topY+thickness);
            rect.addPoint(rightX, topY);
            rect.addPoint(leftX, topY);
        } 
        else if (location == BOTTOM) {
            rect.addPoint(leftX, bottomY-thickness);
            rect.addPoint(rightX, bottomY-thickness);
            rect.addPoint(rightX, bottomY);
            rect.addPoint(leftX, bottomY);
        } 
        else if (location == RIGHT) {
            rect.addPoint(rightX-thickness, topY);
            rect.addPoint(rightX, topY);
            rect.addPoint(rightX, bottomY);
            rect.addPoint(rightX-thickness, bottomY);
        } else { //location == LEFT
            rect.addPoint(leftX, topY);
            rect.addPoint(leftX+thickness, topY);
            rect.addPoint(leftX+thickness, bottomY);
            rect.addPoint(leftX, bottomY);
        }

        return rect;
    }

    //rtype : 0-IMPLEMENT INTERFACE, 1-INHERITEE FROM SUPER CLASS, 2-AGGREGATION, 
    //          3-ASSOCIATION, 4-ASSOCIATION-TO-SELF
    private void linkToNewClass(int rtype, int posX, int posY) {
        int width = node.getPolygon().xpoints[1] - node.getPolygon().xpoints[0];
        int height = node.getPolygon().ypoints[2] - node.getPolygon().ypoints[1];
        int newNodePosX = node.getPolygon().xpoints[0];
        int newNodePosY = node.getPolygon().ypoints[0];
        int e1Location;
        int e2Location;
        double percent;
        
        if (location == TOP) {
            newNodePosY -= 50 + height;
            e1Location = EndPoint.TOP;
            e2Location = EndPoint.BOTTOM;
            percent = (double)(posX - node.getPolygon().xpoints[0]) / width;
        } else if (location == BOTTOM) {
            newNodePosY += 50 + height;
            e1Location = EndPoint.BOTTOM;
            e2Location = EndPoint.TOP;
            percent = (double)(posX - node.getPolygon().xpoints[0]) / width;
        } else if (location == RIGHT) {
            newNodePosX += 50 + width;
            e1Location = EndPoint.RIGHT;
            e2Location = EndPoint.LEFT;
            percent = (double)(posY - node.getPolygon().ypoints[0]) / height;
        } else { //location == LEFT
            newNodePosX -= 50 + width;
            e1Location = EndPoint.LEFT;
            e2Location = EndPoint.RIGHT;
            percent = (double)(posY - node.getPolygon().ypoints[0]) / height;
        }
        
        //IMPLEMENTS LINK
        if (rtype == 0) {
            UMLClassNode c = umlCanvas.addUMLClass(true, newNodePosX, newNodePosY);  
            if (c == null) {
                return;
            }
        
            node.getModelElement().addSuperClass(c.getModelElement());
            
            UMLEndPoint.EPType ept1 = UMLEndPoint.EPType.INHERITER;
            UMLEndPoint.EPType ept2 = UMLEndPoint.EPType.INHERITEE;
            UMLEndPoint e1 = new UMLEndPoint(ept1, node, umlCanvas);        
            UMLEndPoint e2 = new UMLEndPoint(ept2, c, umlCanvas);              
            umlCanvas.addUMLInheritanceLink(false, e1, e2, e1Location, e2Location, percent);
        }
        
        //INHERITANCE LINK
        if (rtype == 1) {
            UMLClassNode c = umlCanvas.addUMLClass(false, newNodePosX, newNodePosY);  
            if (c == null) {
                return;
            }
            
            node.getModelElement().addSuperClass(c.getModelElement());
            
            UMLEndPoint.EPType ept1 = UMLEndPoint.EPType.INHERITER;
            UMLEndPoint.EPType ept2 = UMLEndPoint.EPType.INHERITEE;
            UMLEndPoint e1 = new UMLEndPoint(ept1, node, umlCanvas);        
            UMLEndPoint e2 = new UMLEndPoint(ept2, c, umlCanvas);              
            umlCanvas.addUMLInheritanceLink(false, e1, e2, e1Location, e2Location, percent);
        }
            
        //AGGREGATION LINK
        if (rtype == 2) {
            UMLClassNode c = umlCanvas.addUMLClass(false, newNodePosX, newNodePosY);  
            if (c == null) {
                return;
            }
            
            UMLEndPoint.EPType ept1 = UMLEndPoint.EPType.AGGREGATE;
            UMLEndPoint.EPType ept2 = UMLEndPoint.EPType.ASSOCIATE;
            UMLEndPoint e1 = new UMLEndPoint(ept1, node, umlCanvas);        
            UMLEndPoint e2 = new UMLEndPoint(ept2, c, umlCanvas); 
            umlCanvas.addUMLAssocOrAggregationLink(true, e1, e2, e1Location, e2Location, percent);
        }
        
        //AGGREGATION LINK TO SELF
        if (rtype == 3) {
            UMLEndPoint.EPType ept1 = UMLEndPoint.EPType.AGGREGATE;
            UMLEndPoint.EPType ept2 = UMLEndPoint.EPType.ASSOCIATE;
            UMLEndPoint e1 = new UMLEndPoint(ept1, node, umlCanvas);        
            UMLEndPoint e2 = new UMLEndPoint(ept2, node, umlCanvas); 
            
            //use the same location when aggregating to self
            umlCanvas.addUMLAssocOrAggregationLink(true, e1, e2, e1Location, e1Location, percent);
        }
        
        //ASSOCIATION LINK
        if (rtype == 4) {
            UMLClassNode c = umlCanvas.addUMLClass(false, newNodePosX, newNodePosY);  
            if (c == null) {
                return;
            }
            
            UMLEndPoint.EPType ept1 = UMLEndPoint.EPType.ASSOCIATE;
            UMLEndPoint.EPType ept2 = UMLEndPoint.EPType.ASSOCIATE;  
            UMLEndPoint e1 = new UMLEndPoint(ept1, node, umlCanvas);        
            UMLEndPoint e2 = new UMLEndPoint(ept2, c, umlCanvas); 
            umlCanvas.addUMLAssocOrAggregationLink(false, e1, e2, e1Location, e2Location, percent);
        }     
        
        //ASSOCIATION LINK TO SELF
        if (rtype == 5) {
            UMLEndPoint.EPType ept1 = UMLEndPoint.EPType.ASSOCIATE;
            UMLEndPoint.EPType ept2 = UMLEndPoint.EPType.ASSOCIATE;  
            UMLEndPoint e1 = new UMLEndPoint(ept1, node, umlCanvas);        
            UMLEndPoint e2 = new UMLEndPoint(ept2, node, umlCanvas); 
            
            //use the same location when associating to self
            umlCanvas.addUMLAssocOrAggregationLink(false, e1, e2, e1Location, e1Location, percent);
        }   
    }
    
    private UMLCanvas               umlCanvas;
    private UMLClassNode            node;   
    private int                     location;
    private JPopupMenu              popup;
    
    private int                     pressX;
    private int                     pressY;
    private int                     releaseX;
    private int                     releaseY;
}
