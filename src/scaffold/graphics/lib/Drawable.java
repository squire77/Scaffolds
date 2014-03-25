package scaffold.graphics.lib;

import scaffold.graphics.draggable.DraggableCanvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;


public class Drawable implements IDrawable {
    public enum DrawType { Default, GeneralPath }
    public static class DrawProperties {}
    public DrawType        drawType;
    public DrawProperties  drawProperties;    
   
    public Drawable(Polygon polygon) {
        this(polygon, DrawType.Default, new DrawProperties());
    }
    
    public Drawable(Polygon polygon, DrawType dType, DrawProperties dProps) {
        if (polygon == null)
            throw new IllegalArgumentException("new Drawable requires a non-null Polygon");
                
        init(polygon, dType, dProps);        
    }   
    
    //only a subclass can call this constructor, and if it does,
    //initialize(Polygon) must be called after the contructor is called
    protected Drawable(DrawType dType, DrawProperties dProps) {
        init(null, DrawType.Default, new DrawProperties()); 
    }   
    
    private void init(Polygon polygon, DrawType dType, DrawProperties dProps) {
        if (polygon != null) 
        {
            initialize(polygon);
        }
                              
        this.drawType = dType;
        this.drawProperties = dProps;
        
        this.isVisible = true;
        this.drawFill = true;
        this.drawBorder = true;
    }
    
    public void initialize(Polygon polygon) {
        if (polygon == null)
            throw new IllegalArgumentException("new Drawable requires a non-null Polygon");
        
        setPolygon(polygon);
            
        //save the original
        this.originalPolygon = new Polygon();
        for (int i=0; i<polygon.npoints; i++)
        {                        
            this.originalPolygon.addPoint(polygon.xpoints[i], polygon.ypoints[i]);
        }
        rotations[0] = this.originalPolygon;      
    }    
    
    @Override
    public Color getColor() {
        return this.color;
    }
    
    @Override
    public void setColor(Color color) {
        this.color = color;
    }
    
    @Override
    public Color getBorderColor() {
        return this.borderColor;
    }
    
    @Override
    public void setBorderColor(Color color){
        this.borderColor = color;
    }
    
    @Override
    public boolean isVisible() {
        return this.isVisible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }
    
    @Override
    public void setDrawFill(boolean drawFill) {        
        this.drawFill = drawFill;
    }
        
    @Override
    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
    }
    
    //not allowed to modify polygon once it's set
    //but it will get auto-updated as we move around
    private void setPolygon(Polygon polygon) {        
        this.polygon = polygon;                
    }

    //subclass may override getPolygon() to provide a dynamically moving or
    //changing polygon
    @Override
    public Polygon getPolygon() {
        return this.polygon;
    }

    @Override
    public int width() {
        return getPolygon().xpoints[1] - getPolygon().xpoints[0];
    }
    
    @Override
    public int height() {
        return getPolygon().ypoints[2] - getPolygon().ypoints[1];
    }
    
    @Override
    public Point getCenter() { return center; }
    
    //*** containment and intersection are performed on this.getPolygon() which
    //*** may be a different shape that is based on Drawable.polygon
    
    @Override
    public Rectangle2D getBounds2D() { return getPolygon().getBounds2D(); }
    @Override
    public boolean contains(int x, int y) { //return myContains(getPolygon(), x, y); }
            //this appears to be broken!
            return getPolygon().contains(x, y); }
    @Override
    public boolean intersects(IDrawable region) { return getPolygon().intersects(region.getBounds2D()); }    
    
    private boolean myContains(Polygon p, int x, int y) {
        int a1 = polygon.xpoints[0];
        int b1 = polygon.ypoints[0];
                               
        int n = polygon.npoints;
        
        for (int i=1; i<=n; i++)
        {
            int a2 = polygon.xpoints[i%n];
            int b2 = polygon.ypoints[i%n];
            
            if (PolygonSplit.isPointAboveLine(a1, b1, a2, b2, x, y) != 0) {
                return false;
            }
        }
        
        return true;
    }
        
    //*** drawing occurs on this.getPolygon() which may be a different shape
    //*** that is based on Drawable.polygon
    
    @Override
    final public void draw(Graphics2D g) {
        if (isVisible) {            
            doDraw(g);
        }
    }

    //subclass may override doDraw(), but is required to use getPolygon()
    //since Drawable.polygon is private
    protected void doDraw(Graphics2D g) { 
        if (drawFill) {
            g.setPaint(getColor());
        } else {
            g.setPaint(DraggableCanvas.BACKGROUND_COLOR);            
        }

        g.fill(getPolygon());
        
        if (drawBorder) {
            g.setPaint(getBorderColor()); 
            g.draw(getPolygon());               
        }
    }

    //*** transformations are performed on Drawable.polygon
    
    @Override
    public void translate(int deltaX, int deltaY) {
        transX += deltaX;
        transY += deltaY;
        
        center.x += deltaX;
        center.y += deltaY;
        
        setPolygon(Primitives.translatePolygon(polygon, deltaX, deltaY));
    }        

    @Override    
    public void rotate(int newAngle) {   
        //normalize the angle
        angle = (angle + newAngle) % 360;
        if (angle < 0)
        {
            angle += 360;       
        }       
        
        if (rotations[angle] == null)
        {
            //polygon center is presumed to be (0,0)
            rotations[angle] = Primitives.rotatePolygon(originalPolygon, angle, 0, 0);  
            System.out.println("angle=" + angle);
        }
        
        setPolygon(Primitives.translatePolygon(rotations[angle], transX, transY));
    }   

    @Override
    public void scale(int deltaX, int deltaY) {
        //not supported
    }       
    
    private int transX = 0;
    private int transY = 0;
    
    private Polygon originalPolygon;
    private Polygon polygon;
    private Polygon rotations[] = new Polygon[360];
            
    private Color color;
    private Color borderColor;
    
    private boolean isVisible;	
    private boolean drawFill;
    private boolean drawBorder;
    
    protected Point center = new Point();
    protected int angle = 0;    
}
