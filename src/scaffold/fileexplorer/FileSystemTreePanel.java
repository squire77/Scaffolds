package scaffold.fileexplorer;

import javax.swing.*;
import java.io.*;
import java.awt.*;


public class FileSystemTreePanel extends JPanel {
    public FileSystemTreePanel(String startPath) {
        this(new FileSystemModel(startPath));
    }

    public FileSystemTreePanel(FileSystemModel model) {
        tree = new JTree(model) {
            @Override
            public String convertValueToText(Object value, boolean selected,
                                             boolean expanded, boolean leaf, int row,
                                             boolean hasFocus) {
                return ((File)value).getName();
            }
        };

        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.putClientProperty("JTree.lineStyle", "Angled");

        setLayout(new BorderLayout());
        add(tree, BorderLayout.CENTER);
    }

    public JTree getTree() { return tree; }

    private JTree tree;
}


