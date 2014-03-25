package scaffold.graphics.lib;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

public interface IDrawable 
{
    Rectangle2D getBounds2D();
    boolean contains(int x, int y);      
    boolean intersects(IDrawable region);   
    
    Polygon getPolygon();
    int width();    
    int height();
    Point getCenter(); // starts at (0,0) then updates on translate()    
    
    void draw(Graphics2D g);
    void translate(int deltaX, int deltaY);
    void rotate(int angle);
    void scale(int deltaX, int deltaY);
    
    Color getColor();     
    void setColor(Color color);
    Color getBorderColor();
    void setBorderColor(Color color);
    boolean isVisible();
    void setVisible(boolean visible); 
    void setDrawFill(boolean drawFill);
    void setDrawBorder(boolean drawBorder);
}
