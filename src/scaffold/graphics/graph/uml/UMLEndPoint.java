package scaffold.graphics.graph.uml;

import scaffold.uml.basic.cm.CmAssocEnd;
import scaffold.graphics.graph.EndPoint;
import scaffold.graphics.graph.Node;
import static scaffold.graphics.draggable.Selectable.DEFAULT_SELECTED_BORDER_COLOR;
import static scaffold.graphics.draggable.Selectable.DEFAULT_UNSELECTED_BORDER_COLOR;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Graphics2D;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;


/**
 * UMLEndPoint differs from CmAssocEndPoint since it may represent an
 * inheritance end point. In this case, the modeled endpoint,, stored in 
 * member variable "endPoint", will be null.
 */
public class UMLEndPoint extends EndPoint implements Observer {        
    public enum EPType { INHERITER, INHERITEE, ASSOCIATE, AGGREGATE }
    
    //endpoint.initialize() must be called by UMLAssociationLink's create() method
    public UMLEndPoint(EPType epType, UMLClassNode node, UMLCanvas canvas) {
        super(node, canvas);        
        
        this.epType = epType;
        
        //use default border color as color for endpoint
        setSelectedColor(DEFAULT_SELECTED_BORDER_COLOR);
        setUnselectedColor(DEFAULT_UNSELECTED_BORDER_COLOR);
        
        //start off with the right color since we changed it
        unselect();
    }    
    
    public void initialize(CmAssocEnd endPoint) {
        //set the endpoint first since it is accessed in super.initialize()
        if (endPoint != null) {
            this.endPoint = endPoint;
            this.endPoint.addObserver(this);
        }
                
        super.initialize();        
    }
    
    @Override
    public void update(Observable o, Object obj) {        
        canvas.drawObjects();
    }
    
    public EPType getEPType() {
        return this.epType;
    }
        
    @Override
    public void drawEndPoint(Graphics2D g) { 
        super.drawEndPoint(g);        

        //no role or multiplicity for inheritance relationship
        if (getEPType() == EPType.INHERITEE || 
                ((UMLEndPoint)link.getOtherEndPoint(this)).getEPType() == EPType.INHERITEE) {
            return;
        }
        
        Point pos1 = getPosition();
        Point pos2 = link.getOtherEndPoint(this).getPosition();

        //assume positive quadrant
        double rise = pos2.y - pos1.y;
        double run = pos2.x - pos1.x;
        double angle = Math.atan( Math.abs(rise) / Math.abs(run) );

        int baseX;
        int baseY;
              
        switch (epType) {
            case ASSOCIATE:
                baseX = (int) (Math.cos(angle));
                baseY = (int) (Math.sin(angle));          
                break;
            case AGGREGATE:
                baseX = (int) (Math.cos(angle) * 22.0);
                baseY = (int) (Math.sin(angle) * 22.0);
                break;
            default: //epType == EPType.INHERITEE or epType == EPType.INHERITER
                // no role on "inherit" end point type so just return
                return; 
        }
            
        if (endPoint.getName() != null && !endPoint.getName().equals("")) {            
            drawText(g, baseX, baseY, endPoint.getName(), true, rise, run);            
        }
        
        if (endPoint.getMultiplicity() != null && !endPoint.getMultiplicity().getExpression().equals("")) {
            drawText(g, baseX, baseY, endPoint.getMultiplicity().getExpression(), false, rise, run);            
        }        
    }
    
    private void drawText(Graphics2D g, int baseX, int baseY, String textStr, boolean flip,
            double rise, double run) { // these params are used to determinet what quadrant we're in
        char[] text = textStr.toCharArray();

        switch (location) {
            case LEFT:
                baseX += text.length * FONT_WIDTH + 4;
                if (flip) {
                    baseY -= 2;
                } else {
                    baseY += FONT_HEIGHT + 2;
                }
                break;
            case RIGHT:
                baseX += 4;
                if (flip) {
                    baseY -= 2;
                } else {
                    baseY += FONT_HEIGHT + 2;
                }
                break;
            case TOP:
                if (flip) {
                    baseX -= (text.length * FONT_WIDTH + 4);
                } else {
                    baseX += 4;
                }
                baseY += 2;
                break;                            
            default: //BOTTOM
                if (flip) {
                    baseX -= (text.length * FONT_WIDTH + 4);
                } else {
                    baseX += 4;
                }
                baseY += FONT_HEIGHT + 2;                        
        }

        g.setPaint(getColor());
        g.setFont(new Font ("Ariel", Font.PLAIN, 16));

        if (rise >= 0.0) {
            if (run >= 0.0) {                
                g.drawChars(text, 0, text.length, posX + baseX, posY + baseY);          
            } else {
                g.drawChars(text, 0, text.length, posX - baseX, posY + baseY);            
            }
        } else { //rise < 0.0 
            if (run >= 0.0) {
                g.drawChars(text, 0, text.length, posX + baseX, posY - baseY);          
            } else {
                g.drawChars(text, 0, text.length, posX - baseX, posY - baseY);         
            }
        }       
    }     
    
    //this is called by super.mouseReleased() to handle movement of an
    //endpoint to a new node
    @Override
    public boolean relocateToNode(Node sourceNode, Node targetNode, Node otherNode) {
        UMLClassNode source = (UMLClassNode) sourceNode;
        UMLClassNode target = (UMLClassNode) targetNode;
        UMLClassNode other = (UMLClassNode) otherNode;
        
        //do not allow relocation of any end point from a class to an interface or vice versa
        if ((!source.getModelElement().isInterface() && target.getModelElement().isInterface()) ||
            (source.getModelElement().isInterface()) && !target.getModelElement().isInterface()) {
            return false;
        }
        
        //relocate from associate node
        if (this.epType != EPType.INHERITER && this.epType != EPType.INHERITEE) {
            source.getModelElement().removeEndPoint(endPoint);
            target.getModelElement().addEndPoint(endPoint);
        }
                
        //relocate from inheriter node
        if (this.epType == EPType.INHERITER) {
            //prevent double inheritance
            if (target.getModelElement().getSuperClassesThatAreNotInterfaces().contains(other.getModelElement())) {
                return false;
            }
            
            source.getModelElement().removeSuperClass(other.getModelElement());
            target.getModelElement().addSuperClass(other.getModelElement());
        }
         
        //relocate from inheritee node
        if (this.epType == EPType.INHERITEE) {
            //prevent double inheritance
            if (other.getModelElement().getSuperClasses().contains(target.getModelElement())) {
                return false;
            }
            
            other.getModelElement().removeSuperClass(source.getModelElement());
            other.getModelElement().addSuperClass(target.getModelElement());
        }
                
        //relocate the graph end point
        super.relocateToNode(sourceNode, targetNode, other);
        
        return true;
    }       
    
    @Override
    public Polygon getPolygon() {
        switch (epType) {
            case INHERITEE:
                hide = false;
                setDrawFill(false);          
                return getTriangle();
            case AGGREGATE:
                hide = false;                
                this.setDrawFill(endPoint.isComposite());
                return getDiamond();                                 
            default: //ASSOCIATE,INHERITER
                hide = true;
                return getSquare();                    
        }
    }   
    
    private Polygon getTriangle() {
        Polygon triangle = new Polygon();
        triangle.npoints = 3;
        triangle.xpoints = new int[3];
        triangle.ypoints = new int[3];

        Point pos1 = getPosition();
        Point pos2 = link.getOtherEndPoint(this).getPosition();
        
        //assume positive quadrant
        double rise = pos2.y - pos1.y;
        double run = pos2.x - pos1.x;
        double angle = Math.atan( Math.abs(rise) / Math.abs(run) );
        
        int baseX1 = (int) (Math.cos(angle + .55) * 20.0);
        int baseY1 = (int) (Math.sin(angle + .55) * 20.0);
        int baseX2 = (int) (Math.cos(angle - .55) * 20.0);
        int baseY2 = (int) (Math.sin(angle - .55) * 20.0);        
                
        triangle.xpoints[0] = pos1.x; triangle.ypoints[0] = pos1.y;
        
        if (rise >= 0.0) {
            if (run >= 0.0) {            
                triangle.xpoints[1] = pos1.x + baseX1; triangle.ypoints[1] = pos1.y + baseY1;            
                triangle.xpoints[2] = pos1.x + baseX2; triangle.ypoints[2] = pos1.y + baseY2;
            } else {
                triangle.xpoints[1] = pos1.x - baseX1; triangle.ypoints[1] = pos1.y + baseY1;            
                triangle.xpoints[2] = pos1.x - baseX2; triangle.ypoints[2] = pos1.y + baseY2;
            }
        } else { //rise < 0.0 
            if (run >= 0.0) {
                triangle.xpoints[1] = pos1.x + baseX1; triangle.ypoints[1] = pos1.y - baseY1;            
                triangle.xpoints[2] = pos1.x + baseX2; triangle.ypoints[2] = pos1.y - baseY2;
            } else {
                triangle.xpoints[1] = pos1.x - baseX1; triangle.ypoints[1] = pos1.y - baseY1;            
                triangle.xpoints[2] = pos1.x - baseX2; triangle.ypoints[2] = pos1.y - baseY2;
            }
        }
        
        return triangle;
    }    
    
    private Polygon getDiamond() {
        Polygon diamond = new Polygon();
        diamond.npoints = 4;
        diamond.xpoints = new int[4];
        diamond.ypoints = new int[4];

        Point pos1 = getPosition();
        Point pos2 = link.getOtherEndPoint(this).getPosition();
        
        double rise;
        double run;
                
        //check for connection to self
        if (super.getLocation() == super.link.getOtherEndPoint(this).getLocation()) {
            switch (super.getLocation()) {
                case EndPoint.TOP: 
                    rise = -30;
                    run = 0;
                    break;
                case EndPoint.BOTTOM:
                    rise = 30;
                    run = 0;
                    break;
                case EndPoint.RIGHT:
                    rise = 0;
                    run = 30;
                    break; 
                default: //EndPoint.LEFT
                    rise = 0;
                    run = -30;
            }
        } else {
            //assume positive quadrant
            rise = pos2.y - pos1.y;
            run = pos2.x - pos1.x;
        }
        
        double angle = Math.atan( Math.abs(rise) / Math.abs(run) );
                        
        int baseX1 = (int) (Math.cos(angle + .42) * 11.0); //.3927 rads = 22.5 degs
        int baseY1 = (int) (Math.sin(angle + .42) * 11.0);
        int baseX2 = (int) (Math.cos(angle) * 22.0);
        int baseY2 = (int) (Math.sin(angle) * 22.0);         
        int baseX3 = (int) (Math.cos(angle - .42) * 11.0);
        int baseY3 = (int) (Math.sin(angle - .42) * 11.0);  
                
        diamond.xpoints[0] = pos1.x; diamond.ypoints[0] = pos1.y;
        
        if (rise >= 0.0) {
            if (run >= 0.0) {            
                diamond.xpoints[1] = pos1.x + baseX1; diamond.ypoints[1] = pos1.y + baseY1;            
                diamond.xpoints[2] = pos1.x + baseX2; diamond.ypoints[2] = pos1.y + baseY2;
                diamond.xpoints[3] = pos1.x + baseX3; diamond.ypoints[3] = pos1.y + baseY3;
            } else {
                diamond.xpoints[1] = pos1.x - baseX1; diamond.ypoints[1] = pos1.y + baseY1;            
                diamond.xpoints[2] = pos1.x - baseX2; diamond.ypoints[2] = pos1.y + baseY2;
                diamond.xpoints[3] = pos1.x - baseX3; diamond.ypoints[3] = pos1.y + baseY3;
            }
        } else { //rise < 0.0 
            if (run >= 0.0) {
                diamond.xpoints[1] = pos1.x + baseX1; diamond.ypoints[1] = pos1.y - baseY1;            
                diamond.xpoints[2] = pos1.x + baseX2; diamond.ypoints[2] = pos1.y - baseY2;
                diamond.xpoints[3] = pos1.x + baseX3; diamond.ypoints[3] = pos1.y - baseY3;
            } else {
                diamond.xpoints[1] = pos1.x - baseX1; diamond.ypoints[1] = pos1.y - baseY1;            
                diamond.xpoints[2] = pos1.x - baseX2; diamond.ypoints[2] = pos1.y - baseY2;
                diamond.xpoints[3] = pos1.x - baseX3; diamond.ypoints[3] = pos1.y - baseY3;
            }
        }
        
        return diamond;
    }    
        
    private Polygon getSquare() {
        Polygon rect = new Polygon();
        rect.npoints = 4;
        rect.xpoints = new int[4];
        rect.ypoints = new int[4];

        //create a rectangle around the current position
        Point pos = getPosition();
        rect.xpoints[0] = pos.x-4; rect.ypoints[0] = pos.y-4;
        rect.xpoints[1] = pos.x+4; rect.ypoints[1] = pos.y-4;
        rect.xpoints[2] = pos.x+4; rect.ypoints[2] = pos.y+4;
        rect.xpoints[3] = pos.x-4; rect.ypoints[3] = pos.y+4;

        return rect;
    }
        
    private final static int FONT_HEIGHT = 16;
    private final static int FONT_WIDTH = 7;
    
    private EPType          epType;
    private CmAssocEnd      endPoint;
}
