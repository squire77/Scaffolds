package scaffold.graphics.draggable;

import java.awt.event.MouseEvent;
import scaffold.graphics.lib.IDrawable;

public interface IDraggable extends ISelectable, IDrawable {
    boolean validate(int deltaX, int deltaY, int width, int height);
    void snapToGrid(Grid grid);
    void remove(DraggableCanvas canvas);
    
    //this indicates whether to treat the object as stand-alone or part of a group
    boolean isDepthLevelOne();
            
    //this is the main course so I copied it from IMousable as a reminder
    @Override
    void mouseDragged(MouseEvent event, int deltaX, int deltaY);
}
