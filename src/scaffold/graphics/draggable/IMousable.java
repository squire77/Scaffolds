package scaffold.graphics.draggable;

import java.awt.event.*;

public interface IMousable {
    void mouseEntered(MouseEvent event);
    void mouseMoved(MouseEvent event);
    void mouseExited(MouseEvent event);
    void mousePressed(MouseEvent event);
    void mouseDragged(MouseEvent event, int deltaX, int deltaY);
    void mouseReleased(MouseEvent event);
    void mouseClicked(MouseEvent event);
}
