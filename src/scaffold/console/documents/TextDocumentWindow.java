package scaffold.console.documents;

import javax.swing.*;
import java.awt.*;

public class TextDocumentWindow extends JFrame {
    public TextDocumentWindow() {
        super("Model Test Data");
        viewer = new TextDocumentViewer();
        add(viewer.getComponent());

        setPreferredSize(new Dimension(400, 300));
        pack();
    }

    public TextDocumentViewer getDocumentViewer() {
        return this.viewer;
    }

    private TextDocumentViewer viewer;
}
