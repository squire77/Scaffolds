package scaffold.graphics.graph.uml;

import scaffold.graphics.draggable.DraggableCanvas;
import java.awt.*;
import java.awt.event.*;

public class ResizeControl extends Control {
    public static final int UPPER_LEFT=0, UPPER_RIGHT=1, LOWER_RIGHT=2, LOWER_LEFT=3;

    public ResizeControl(DraggableCanvas canvas, UMLClassNode node, int location) {
        this.node = node;
        this.canvas = canvas;
        this.location = location;
    }       

    @Override
    public Polygon getPolygon() {
        Polygon triangle = new Polygon();

        //recreate polygon relative to base shape
        if (location == UPPER_LEFT) {
            triangle.addPoint(node.getPolygon().xpoints[0], node.getPolygon().ypoints[0]);
            triangle.addPoint(node.getPolygon().xpoints[0]+5, node.getPolygon().ypoints[0]);
            triangle.addPoint(node.getPolygon().xpoints[0], node.getPolygon().ypoints[0]+5);
        } else if (location == UPPER_RIGHT) {
            triangle.addPoint(node.getPolygon().xpoints[1]-5, node.getPolygon().ypoints[1]);
            triangle.addPoint(node.getPolygon().xpoints[1], node.getPolygon().ypoints[1]);
            triangle.addPoint(node.getPolygon().xpoints[1], node.getPolygon().ypoints[1]+5);
        } else if (location == LOWER_RIGHT) {
            triangle.addPoint(node.getPolygon().xpoints[2], node.getPolygon().ypoints[2]-5);
            triangle.addPoint(node.getPolygon().xpoints[2], node.getPolygon().ypoints[2]);
            triangle.addPoint(node.getPolygon().xpoints[2]-5, node.getPolygon().ypoints[2]);
        } else { //location == LOWER_LEFT
            triangle.addPoint(node.getPolygon().xpoints[3]+5, node.getPolygon().ypoints[3]);
            triangle.addPoint(node.getPolygon().xpoints[3], node.getPolygon().ypoints[3]);
            triangle.addPoint(node.getPolygon().xpoints[3], node.getPolygon().ypoints[3]-5);
        }

        return triangle;
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        canvas.setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void mousePressed(MouseEvent event) {
        canvas.setCursor(Cursor.getPredefinedCursor(getCursor()));
    }
    
    @Override
    public void mouseDragged(MouseEvent event, int deltaX, int deltaY) {
        stretchNode(location, deltaX, deltaY);
    }

    private int getCursor() {
        if (location == UPPER_LEFT)
            return Cursor.NW_RESIZE_CURSOR;
        if (location == UPPER_RIGHT)
            return Cursor.NE_RESIZE_CURSOR;
        if (location == LOWER_RIGHT)
            return Cursor.SE_RESIZE_CURSOR;
        
        return Cursor.SW_RESIZE_CURSOR;
    }

    private void stretchNode(int location, int deltaX, int deltaY) {
        if (UPPER_LEFT == location) {
            node.getPolygon().xpoints[0] += deltaX;
            node.getPolygon().ypoints[0] += deltaY;
            node.getPolygon().ypoints[1] += deltaY;
            node.getPolygon().xpoints[3] += deltaX;
        } else if (UPPER_RIGHT == location) {
            node.getPolygon().xpoints[1] += deltaX;
            node.getPolygon().ypoints[1] += deltaY;
            node.getPolygon().ypoints[0] += deltaY;
            node.getPolygon().xpoints[2] += deltaX;
        } else if (LOWER_RIGHT == location) {
            node.getPolygon().xpoints[2] += deltaX;
            node.getPolygon().ypoints[2] += deltaY;
            node.getPolygon().ypoints[3] += deltaY;
            node.getPolygon().xpoints[1] += deltaX;
        } else { //LOWER_LEFT == location
            node.getPolygon().xpoints[3] += deltaX;
            node.getPolygon().ypoints[3] += deltaY;
            node.getPolygon().ypoints[2] += deltaY;
            node.getPolygon().xpoints[0] += deltaX;
        }
    }

    private UMLClassNode                node;
    private DraggableCanvas         canvas;
    private int                     location;
}
