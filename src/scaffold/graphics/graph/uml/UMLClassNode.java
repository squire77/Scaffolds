package scaffold.graphics.graph.uml;

import scaffold.uml.basic.UmlType;
import scaffold.uml.basic.cm.CmClass;
import scaffold.uml.basic.cm.CmOperation;
import scaffold.uml.basic.cm.CmParameter;
import scaffold.uml.basic.cm.CmProperty;
import scaffold.uml.basic.UmlPackage;
import scaffold.graphics.graph.Graph;
import scaffold.graphics.graph.EndPoint;
import scaffold.graphics.draggable.DraggableCanvas;
import scaffold.forms.UMLClassForm;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Observable;


public class UMLClassNode extends UMLNode {       
    public static UMLClassNode create(Graph graph, UMLCanvas canvas, CmClass clazz) {
        UMLClassNode c = new UMLClassNode(graph, canvas, clazz);
        c.initialize();                                
        return c;
    }
    
    // use the create method to ensure initialize() is called  
    private UMLClassNode(Graph graph, UMLCanvas canvas, CmClass clazz) {
        super(graph, canvas);
          
        this.controls = new ArrayList<Control>();                      
        this.clazz = clazz;        
    }
    
    //must call this after constructor
    @Override
    public void initialize() {
        super.initialize();
        
        resizePolygon();
        
        //create controls for passing mouse events
        controls.add(new LinkToControl(canvas, this, LinkToControl.TOP).initialize());
        controls.add(new LinkToControl(canvas, this, LinkToControl.BOTTOM).initialize());
        controls.add(new LinkToControl(canvas, this, LinkToControl.RIGHT).initialize());
        controls.add(new LinkToControl(canvas, this, LinkToControl.LEFT).initialize());
        controls.add(new ResizeControl(canvas, this, ResizeControl.UPPER_LEFT).initialize());
        controls.add(new ResizeControl(canvas, this, ResizeControl.UPPER_RIGHT).initialize());
        controls.add(new ResizeControl(canvas, this, ResizeControl.LOWER_RIGHT).initialize());
        controls.add(new ResizeControl(canvas, this, ResizeControl.LOWER_LEFT).initialize());        
        
        //observe model changes
        clazz.addObserver(this);
    }    
    
    @Override
    public UMLNodeType getNodeType() {
        return UMLNodeType.CLASS_NODE_TYPE;
    }
            
    @Override
    public String getPackageID() {
        return getModelElement().getPackage();
    }
    
    public String getId() {
        return clazz.getID();
    }
    
    public CmClass getModelElement() {
        return this.clazz;
    }
    
    @Override
    public void update(Observable o, Object obj) {
        resizePolygon();
        canvas.drawObjects();
    }
    
    @Override
    public void remove(DraggableCanvas canvas) { 
        super.remove(canvas);    
    
        //remove from model        
        UmlPackage pkg = UmlPackage.getPackage(this.clazz.getPackage());
        pkg.removeOwnedType(this.clazz.getID());
        CmClass.removeClass(this.clazz.getID());
        UmlType.unregisterType(this.clazz.getID());
    }
                
    @Override
    public void translate(int deltaX, int deltaY) {
        super.translate(deltaX, deltaY);
                
        for (Control c: controls)
            c.translate(deltaX, deltaY);
    }    
    
    @Override
    public void mouseReleased(MouseEvent event) {        
        //controls are not explictily on the canvas so we delegate to them directly        
        for (Control c: controls) {
            if (c.contains(event.getX(), event.getY())) {
                c.mouseReleased(event);               
                return;
            }
        }
        
        super.mouseReleased(event); 
    }
    
    @Override
    public void mousePressed(MouseEvent event) {        
        //controls are not explictily on the canvas so we delegate to them directly        
        for (Control c: controls) {
            if (c.contains(event.getX(), event.getY())) {
                c.mousePressed(event);
                return;
            }
        }
        
        super.mousePressed(event); 
    }

    @Override
    public void mouseDragged(MouseEvent event, int deltaX, int deltaY) {      
        //controls are not explictily on the canvas so we delegate to them directly        
        for (Control c: controls) {
            if (c.contains(event.getX(), event.getY())) {
                c.mouseDragged(event, deltaX, deltaY);
                return;
            }
        }   
        
        super.mouseDragged(event, deltaX, deltaY); 
    }

    @Override
    public void mouseClicked(MouseEvent event) {      
        if (event.getClickCount() == 2) {//&& !event.isConsumed()) {
            //lazy create the form to save on load time
            if (classForm == null) {
                this.classForm = new UMLClassForm(clazz);   
            }
            
            classForm.loadValues();
            classForm.setLocation(event.getXOnScreen(), event.getYOnScreen());
            classForm.setLocationRelativeTo(null); //center dialog on screen
            classForm.setVisible(true);
            //event.consume();
        }
    }        
    
    @Override
    public void doDraw(Graphics2D g) {
        super.doDraw(g);        
        
        FontMetrics fm = canvas.getFontMetrics(ARIEL_BOLD_16);
        int nameWidth = fm.stringWidth(clazz.getName());
        
        //draw title
        int x0 = getPolygon().xpoints[0];
        int y0 = getPolygon().ypoints[0];
        g.setPaint(super.getBorderColor());
        g.setFont(ARIEL_BOLD_16);
        g.drawString(clazz.getName(), x0+width()/2-nameWidth/2, y0+(gridSpacing+CHAR_HEIGHT/2));
        y0 += 2 * gridSpacing;                
        
        //draw separator
        g.drawLine(x0, y0, x0+width(), y0);
        
        AttributedString attribStr;
        
        //draw attributes       
        if (!clazz.getOwnedAttributes().isEmpty()) {
            for (CmProperty attrib: clazz.getOwnedAttributes()) {
                String attribName = attrib.getName();
                attribStr = new AttributedString(getAttribStr(attribName));
                attribStr.addAttribute(TextAttribute.FONT, ARIEL_PLAIN_16);
                if (clazz.getOwnedAttributeByName(attribName).isStatic()) {
                    attribStr.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                }
                g.drawString(attribStr.getIterator(), x0+gridSpacing, y0+(gridSpacing+CHAR_HEIGHT/2));
                y0 += 2 * gridSpacing;
            }
        }
        
        //draw separator
        g.drawLine(x0, y0, x0+width(), y0);
        
        //draw methods      
        if (!clazz.getOwnedOperations().isEmpty()) {
            for (CmOperation method: clazz.getOwnedOperations()) {
                String methodName = method.getName();
                attribStr = new AttributedString(getMethodStr(methodName));
                if (clazz.getOwnedOperationByName(methodName).isAbstract()) {
                    attribStr.addAttribute(TextAttribute.FONT, ARIEL_ITALIC_16);
                } else {
                    attribStr.addAttribute(TextAttribute.FONT, ARIEL_PLAIN_16);
                }
                if (clazz.getOwnedOperationByName(methodName).isStatic()) {
                    attribStr.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                }
                
                g.drawString(attribStr.getIterator(), x0+gridSpacing, y0+(gridSpacing+CHAR_HEIGHT/2));
                y0 += 2 * gridSpacing;
            }
        }        
    }   
    
    private String getAttribStr(String name) {
        CmProperty attr = clazz.getOwnedAttributeByName(name);
        
        String visiStr;
        
        switch (attr.getVisibilityKind()) {
            case 0:
                visiStr = "+";
                break;
            case 1:
                visiStr = "-";
                break;
            case 2:   
                visiStr = "#";
                break;
            default:
                visiStr = "";
        }
        
        StringBuilder typeStr = new StringBuilder();
        
        if (attr.getType() != null) {
            typeStr.append(":");
            typeStr.append(attr.getType().getName());
        }
        
        return visiStr + name + typeStr.toString();        
    }
    
    private String getMethodStr(String name) {
        CmOperation operation = clazz.getOwnedOperationByName(name);
        
        String visiStr;
        
        switch (operation.getVisibilityKind()) {
            case 0:
                visiStr = "+";
                break;
            case 1:
                visiStr = "-";
                break;
            case 2:   
                visiStr = "#";
                break;
            default: //PACKAGE
                visiStr = "~";
        }
        
        String retStr = "";
        
        if (!operation.getType().isVoid()) {
            retStr = ":" + operation.getType().getName();
        }
        
        StringBuilder paramStr = new StringBuilder();        
        boolean first = true;
        
        for (CmParameter p: operation.getParameters()) {
            if (!first) {
                paramStr.append(",");
            } else {
                first = false;
            }
            
            paramStr.append(p.getName());
            
            if (p.getType() != null) {
                paramStr.append(":");
                paramStr.append(p.getType().getName());
            }
        }
        
        return visiStr + name + "(" + paramStr + ")" + retStr;     
    }
    
    public void resizePolygon() {    
        //cheat by using bold italic which uses maximum space
        FontMetrics fm = canvas.getFontMetrics(ARIEL_BOLD_ITALIC_16);
        
        int maxStrWidth = fm.stringWidth(clazz.getName());
        for (CmProperty attrib: clazz.getOwnedAttributes()) {          
            String attribStr = getAttribStr(attrib.getName());
            int strWidth = fm.stringWidth(attribStr);
            if (strWidth > maxStrWidth) {
                maxStrWidth = strWidth;
            }
        }
        for (CmOperation method: clazz.getOwnedOperations()) {
            String methodStr = getMethodStr(method.getName());
            int strWidth = fm.stringWidth(methodStr);
            if (strWidth > maxStrWidth) {
                maxStrWidth = strWidth;
            }
        }                        
        
        // max(name width, max_chars(attribs), max_chars(methods)) + 2
        double newWidth = maxStrWidth / gridSpacing + 2;
        
        // title height + (# attribs + # methods) * 2
        double newHeight = TITLE_HEIGHT + (clazz.getOwnedAttributes().size() + clazz.getOwnedOperations().size()) * 2;
        
        //calculate new height/width for polygon
        //Title: 6
        //Attributes: max(6, letter width * max_num_chars(attribs))
        //Methods: max(6, letter width * max_num_chars(methods))
        Dimension dim = recalculateSize(width()/gridSpacing, height()/gridSpacing, newWidth, newHeight, gridSpacing);
        
        if (dim != null) {
            //resize the polygon
            int posX = getPolygon().xpoints[0];
            int posY = getPolygon().ypoints[0];
            super.initialize(recreatePolygon(dim.width, dim.height));
            translate(posX, posY);

            //reposition on the endpoints
            for (EndPoint ep: endPoints) {
                ep.resetPosition();                
            }        
        }
    }
     
    private static Polygon recreatePolygon(int width, int height) {       
        //node with controls is always 4 points in clockwise order
        //use multiples of grid spacing to prevent resizing when snap is enabled
        Polygon rectangle = new Polygon();
        rectangle.addPoint(0, 0);
        rectangle.addPoint(width, 0);
        rectangle.addPoint(width, height);
        rectangle.addPoint(0, height);
        
        return rectangle;
    } 
    
    private java.util.List<Control>         controls;    
    private UMLClassForm                    classForm;     
    private CmClass                         clazz;
    
    //initialize polygon (must be static to pass into super constructor)
    static
    {
        Dimension dim = UMLNode.recalculateSize(0, 0, TITLE_WIDTH, TITLE_HEIGHT, 12);
        
        if (dim != null) {
            initialPolygon = recreatePolygon(dim.width, dim.height);
        }
    }           
}
