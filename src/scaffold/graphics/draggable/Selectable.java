package scaffold.graphics.draggable;

import scaffold.graphics.lib.Drawable;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.event.MouseEvent;

public class Selectable extends Drawable implements ISelectable {
    public static final Color LIGHT_RED = new Color( 255, 30 , 30 );
    public static final Color DARK_RED = new Color( 150, 0, 0 );
    public static final Color LIGHT_GREEN = new Color( 51, 255, 102 );
    
    final public static Color DEFAULT_SELECTED_COLOR = LIGHT_GREEN;
    final public static Color DEFAULT_UNSELECTED_COLOR = LIGHT_GREEN;
    final public static Color DEFAULT_SELECTED_BORDER_COLOR = LIGHT_RED;
    final public static Color DEFAULT_UNSELECTED_BORDER_COLOR = Color.black;
    
    public Selectable(Polygon polygon) {
        super(polygon);               
        init();
    }    
    
    public Selectable(Polygon polygon, Color selCol, Color unselCol, DrawType dType, DrawProperties dProps) {
        super(polygon, dType, dProps);
        init();     
    }    
        
    //only a subclass can call this constructor, and if it does,
    //initialize(Polygon) must be called after the contructor is called
    protected Selectable() {
        super(DrawType.Default, new DrawProperties()); 
        init();
    }   
    protected Selectable(Color selCol, Color unselCol, DrawType dType, DrawProperties dProps) {
        super(dType, dProps); 
        init();
    }  
    
    private void init() {        
        this.selectedColor = DEFAULT_SELECTED_COLOR;
        this.unselectedColor = DEFAULT_UNSELECTED_COLOR;
        this.selectedBorderColor = DEFAULT_SELECTED_BORDER_COLOR;
        this.unselectedBorderColor = DEFAULT_UNSELECTED_BORDER_COLOR;
        
        isSelected = false; 
        super.setColor(unselectedColor); 
        super.setBorderColor(unselectedBorderColor);
    }   
    
    @Override
    public void initialize(Polygon polygon) {
        super.initialize(polygon);
    }

    @Override
    public void mouseEntered(MouseEvent event) {}
    @Override
    public void mouseMoved(MouseEvent event) {}
    @Override
    public void mouseExited(MouseEvent event) {}
    @Override
    public void mousePressed(MouseEvent event) {}
    @Override
    public void mouseDragged(MouseEvent event, int deltaX, int deltaY) {}
    @Override
    public void mouseReleased(MouseEvent event) {}
    @Override
    public void mouseClicked(MouseEvent event) {}

    @Override
    public boolean isSelected() { 
        return isSelected; 
    }
    @Override
    public void select() { 
        isSelected = true; 
        super.setColor(selectedColor); 
        super.setBorderColor(selectedBorderColor);
    }
    @Override
    public void unselect() { 
        isSelected = false; 
        super.setColor(unselectedColor); 
        super.setBorderColor(unselectedBorderColor);
    }
    
    @Override
    public Color getSelectedColor() { return this.selectedColor; }
    @Override
    public Color getUnselectedColor() { return this.unselectedColor; }       
    @Override
    public void setSelectedColor(Color color) { selectedColor = color; }
    @Override
    public void setUnselectedColor(Color color) { unselectedColor = color; }    

    @Override
    public Color getSelectedBorderColor() { return this.selectedBorderColor;  }
    @Override
    public Color getUnselectedBorderColor() { return this.unselectedBorderColor; }     
    @Override
    public void setSelectedBorderColor(Color color) { selectedBorderColor = color;  }
    @Override
    public void setUnselectedBorderColor(Color color) { unselectedBorderColor = color; } 
    
    private boolean     isSelected;
    private Color       selectedColor;
    private Color       unselectedColor;
    private Color       selectedBorderColor;
    private Color       unselectedBorderColor;
}
