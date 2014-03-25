package scaffold.codegen;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class GeneratorTypeListPanel extends JPanel implements ListSelectionListener {
    public GeneratorTypeListPanel(Border border, GeneratorListPanel genListPanel) {
        super(new BorderLayout());  
        
        this.genListPanel = genListPanel;
        this.setBorder(border);
                        
        this.genTypeList = new JList();
        this.genTypeList.setBorder(border);
        this.genTypeList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.genTypeList.setListData(GenTemplates.CoreGeneratorNames);                
        this.genTypeList.addListSelectionListener(this);
        
        this.add(new JLabel("Generator Type"), BorderLayout.NORTH);
        this.add(new JScrollPane(genTypeList), BorderLayout.CENTER);
        
        this.genTypeList.setSelectedIndex(0); 
    }    
    
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            //note: value is -1 if nothing is selected
            genListPanel.setGeneratorType(genTypeList.getSelectedIndex());
        }
    }
    
    private JList                   genTypeList;
    private GeneratorListPanel      genListPanel;
}
