package scaffold.graphics.graph.uml;

import scaffold.graphics.graph.Graph;
import scaffold.graphics.graph.Node;
import scaffold.uml.basic.UmlPackage;
import scaffold.graphics.draggable.DraggableCanvas;
import scaffold.graphics.draggable.Grid;
import static scaffold.graphics.graph.uml.UMLNode.TITLE_WIDTH;
import static scaffold.graphics.graph.uml.UMLNode.ARIEL_BOLD_16;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Observable;


public class UMLPackageNode extends UMLNode {
    public static UMLPackageNode create(Graph graph, UMLCanvas canvas, UmlPackage packge, int sizeX, int sizeY) {
        UMLPackageNode p = new UMLPackageNode(graph, canvas, packge, sizeX, sizeY);
        p.initialize();                                
        return p;
    }
    
    // use the create method to ensure initialize() is called  
    private UMLPackageNode(Graph graph, UMLCanvas canvas, UmlPackage packge, int sizeX, int sizeY) {
        super(graph, canvas);
          
        this.nodes = new ArrayList<Node>();                   
        this.packge = packge; 
        
        resizePolygon(sizeX, sizeY);        
    }
    
    //must call this after constructor
    @Override
    public void initialize() {
        super.initialize();
        
        packge.addObserver(this);
    }       
    
    @Override
    public UMLNodeType getNodeType() {
        return UMLNodeType.PACKAGE_NODE_TYPE;
    }
            
    @Override
    public String getPackageID() {
        return getModelElement().getNestingPackage();
    }
    
    public String getId() {
        return packge.getID();
    }
    
    public UmlPackage getModelElement() {
        return this.packge;
    }
    
    @Override
    public void update(Observable o, Object obj) {
        //resizePolygon();
        canvas.drawObjects();
    }
    
    public void addNode(UMLNode n) {
        if (n.getNodeType() == UMLNodeType.PACKAGE_NODE_TYPE) {
            String modelElementID = ((UMLPackageNode) n).getModelElement().getID();
            getModelElement().addNestedPackage(modelElementID);
        }
        
        if (n.getNodeType() == UMLNodeType.CLASS_NODE_TYPE) {
            String modelElementID = ((UMLClassNode) n).getModelElement().getID();
            getModelElement().addOwnedType(modelElementID);
        }        
        
        this.nodes.add(n);
    }
    
    //*** OVERRIDE DRAGGABLE TO ACT AS A GROUP ****************************
    
    @Override
    public void snapToGrid(Grid grid) {
        super.snapToGrid(grid);
        
        for (Node n: nodes) {
            n.snapToGrid(grid);
        }
    }
    
    @Override
    public void remove(DraggableCanvas canvas) { 
        super.remove(canvas);    
    
        //remove from model        
        UmlPackage.removePackage(packge.getID());
    }
                
    //*** OVERRIDE DRAWABLE TO ACT AS A GROUP *****************************
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        
        for (Node n: nodes) {
            n.setVisible(visible);
        }
    }
    
    @Override
    public void translate(int deltaX, int deltaY) {
        super.translate(deltaX, deltaY);
                
        for (Node n: nodes) {
            n.translate(deltaX, deltaY);
        }
    }            
    
    @Override
    public void doDraw(Graphics2D g) {
        g.setPaint(getBorderColor()); 
        g.draw(getPolygon());   
        
        int x0 = getPolygon().xpoints[0];
        int y0 = getPolygon().ypoints[0];
                                                   
        //draw title
        FontMetrics fm = canvas.getFontMetrics(ARIEL_BOLD_16);
        int nameWidth = fm.stringWidth(packge.getName());
        g.setPaint(super.getBorderColor());
        g.setFont(ARIEL_BOLD_16);
        g.drawString(packge.getName(), x0+width()/2-nameWidth/2, y0+(gridSpacing+CHAR_HEIGHT/2));
        y0 += 2 * gridSpacing;       
        
        g.drawLine(x0, y0, x0+width(), y0);

        for (Node n: nodes) {           
            n.draw(g);
        }
    }
    
    private void resizePolygon(int newWidth, int newHeight) {
        FontMetrics fm = canvas.getFontMetrics(ARIEL_BOLD_16);
        double titleWidth = fm.stringWidth(packge.getName()) / gridSpacing + 2;
        double titleHeight = TITLE_HEIGHT;  
        Dimension titleDim = recalculateSize(0, 0, titleWidth, titleHeight, 12);   
        
        Dimension dim = recalculateSize(width()/gridSpacing, height()/gridSpacing, newWidth/gridSpacing, newHeight/gridSpacing, gridSpacing);
        
        if (dim != null) {
            //resize the polygon
            int posX = getPolygon().xpoints[0];
            int posY = getPolygon().ypoints[0];
            super.initialize(recreatePolygon(titleDim.width, titleDim.height, dim.width, dim.height));
            translate(posX, posY-titleDim.height);    
        }
    }
        
    private static Polygon recreatePolygon(int titleWidth, int titleHeight, int width, int height) {                      
        if (titleWidth > width) {
            width = titleWidth + TITLE_WIDTH;
        }
        
        Polygon rectangle = new Polygon();
        rectangle.addPoint(0, 0);
        rectangle.addPoint(titleWidth, 0);
        rectangle.addPoint(titleWidth, titleHeight);
        rectangle.addPoint(width, titleHeight);
        rectangle.addPoint(width, height+titleHeight);
        rectangle.addPoint(0, height+titleHeight);
        
        return rectangle;
    } 
    
    private java.util.List<Node>            nodes;      
    private UmlPackage                      packge;    
    
    //initialize polygon (must be static to pass into super constructor)
    static
    {
        Dimension dim = recalculateSize(0, 0, TITLE_WIDTH, TITLE_HEIGHT, 12);
        
        if (dim != null) {
            initialPolygon = recreatePolygon(dim.width, dim.height, dim.width*2, dim.height*4);
        }
    }  
}
