package scaffold.graphics.draggable;

import java.awt.Color;

public interface ISelectable extends IMousable {
    boolean isSelected();
    void select();
    void unselect();
    
    Color getSelectedColor();
    Color getUnselectedColor(); 
    void setSelectedColor(Color color);
    void setUnselectedColor(Color color);  
    
    Color getSelectedBorderColor();
    Color getUnselectedBorderColor();
    void setSelectedBorderColor(Color color);
    void setUnselectedBorderColor(Color color); 
}
