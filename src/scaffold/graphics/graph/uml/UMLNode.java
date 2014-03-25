package scaffold.graphics.graph.uml;

import scaffold.graphics.graph.Graph;
import scaffold.graphics.graph.Node;
import scaffold.uml.basic.UmlPackage;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Polygon;
import java.util.Observable;
import java.util.Observer;
import scaffold.uml.basic.UmlElement;


abstract public class UMLNode extends Node implements Observer {
    public enum UMLNodeType { PACKAGE_NODE_TYPE, CLASS_NODE_TYPE }
    
    protected UMLNode(Graph graph, UMLCanvas canvas) {
        super(graph, initialPolygon);
          
        this.canvas = canvas;
        this.gridSpacing = canvas.getGridSpacing();
    }    
    
    abstract public UmlElement getModelElement();
    
    abstract public UMLNodeType getNodeType();
            
    abstract public String getPackageID();
    
    @Override
    abstract public void update(Observable o, Object obj);
        
    @Override
    public boolean isDepthLevelOne() {
        //Note: 
        String packageID = getPackageID();
        
        //top-level packages have a nesting package of null while other top-level elements
        //such as classes are in the default package
        return (packageID == null || packageID.equals(UmlPackage.getDefaultPackage().getID()));
    }
        
    public static Dimension recalculateSize(int oldWidth, int oldHeight, double newSizeX, double newSizeY, int gridSpacing) {
        int newWidth = (int)(gridSpacing * newSizeX + 0.5);
        int newHeight = (int)(gridSpacing * newSizeY + 0.5);
        
        boolean changed = false;        
        int width = 0;
        int height = 0;
        
        if (oldWidth != newWidth) {
            width = newWidth;
            changed = true;
        }
        
        if (oldHeight != newHeight) {
            height = newHeight;
            changed = true;
        }
        
        if (changed) {
            return new Dimension(width, height);
        } else {
            return null;
        }
    }
    
    protected static final Font               ARIEL_PLAIN_16 = new Font("Ariel", Font.PLAIN, 16);
    protected static final Font               ARIEL_BOLD_16 = new Font("Ariel", Font.BOLD, 16);
    protected static final Font               ARIEL_ITALIC_16 = new Font("Ariel", Font.ITALIC, 16);
    protected static final Font               ARIEL_BOLD_ITALIC_16 = new Font("Ariel", Font.BOLD | Font.ITALIC, 16);

    //protected static final int                CHAR_WIDTH = 9;  //unused, use gridSpacing instead
    protected static final int                CHAR_HEIGHT = 10;    
    protected static final int                TITLE_WIDTH = 8;
    protected static final int                TITLE_HEIGHT = 2;         
        
    protected static Polygon                  initialPolygon;
    
    protected int                             gridSpacing;
    protected UMLCanvas                       canvas;    
}
