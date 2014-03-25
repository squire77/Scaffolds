package scaffold.graphics.draggable;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;

public class Draggable extends Selectable implements IDraggable {
    public Draggable(Polygon polygon) {        
        super(polygon);
    }
            
    public Draggable(Polygon polygon, Color selCol, Color unselCol,
            DrawType dType, DrawProperties dProps) {
        super(polygon, selCol, unselCol, dType, dProps);
    }
    
    //only a subclass can call this constructor, and if it does,
    //initialize(Polygon) must be called after the contructor is called
    protected Draggable() {
    }    
    
    @Override
    public void initialize(Polygon polygon) {
        super.initialize(polygon);
    }

    @Override 
    public void mouseDragged(MouseEvent event, int deltaX, int deltaY) { 
        translate(deltaX, deltaY); 
    }

    @Override
    public boolean validate(int deltaX, int deltaY, int width, int height) {
        boolean result = true;

        int left = getPolygon().xpoints[0];
        int right = getPolygon().xpoints[0];
        int top = getPolygon().ypoints[0];
        int bottom = getPolygon().ypoints[0];

        //find the boundaries
        for (int i=1; i<getPolygon().npoints; i++) {
            if (getPolygon().xpoints[i] < left)
                left = getPolygon().xpoints[i];
            if (getPolygon().xpoints[i] > right)
                right = getPolygon().xpoints[i];
            if (getPolygon().ypoints[i] < top)
                top = getPolygon().ypoints[i];
            if (getPolygon().ypoints[i] > bottom)
                bottom = getPolygon().ypoints[i];
        }

        if ((left + deltaX < 0) || (right + deltaX > width) ||
                (top + deltaY < 0) || (bottom + deltaY > height)) { 
            result = false;
        }
        
        return result;
    }

    @Override
    public void snapToGrid(Grid grid) {
        if (grid.snapToGrid()) {
            for(int i=0; i<getPolygon().npoints; i++) {
                Point snapped = grid.snap(getPolygon().xpoints[i], getPolygon().ypoints[i]);
                getPolygon().xpoints[i] = snapped.x;
                getPolygon().ypoints[i] = snapped.y;
            }
        }
    }       
    
    @Override
    public void remove(DraggableCanvas canvas) {
        canvas.removeObject(this);
    }
    
    @Override
    public boolean isDepthLevelOne() {
        return true;
    }
}
