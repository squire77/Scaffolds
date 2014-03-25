package scaffold.codegen;

import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


public class GeneratorListPanel extends JPanel implements ListSelectionListener {
    public GeneratorListPanel(Border border, GeneratorEditorPanel genEditorPanel) {
        super(new BorderLayout());

        this.genEditorPanel = genEditorPanel;
        this.generatorType = -1;
        this.setBorder(border);
        
        this.genListModel = new DefaultListModel();
        this.genList = new JList(genListModel);
        this.genList.addListSelectionListener(this);

        this.addButton = new JButton("Add");
        this.addButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addAction(null);
                    }
                }
        );

        this.removeButton = new JButton("Remove");
        this.removeButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ListSelectionModel lsm = genList.getSelectionModel();
                        int firstSelected = lsm.getMinSelectionIndex();
                        genListModel.remove(firstSelected);

                        if (genListModel.isEmpty()) {
                            //genList is empty; disable remove button
                            removeButton.setEnabled(false);
                        } else {
                            //Adjust the selection.
                            if (firstSelected == genListModel.getSize())
                                firstSelected--; //removed item in last position
                            genList.setSelectedIndex(firstSelected);
                        }

                        //updateRuleDescriptionAndActions();
                    }
                }
        );

        this.addButton.setEnabled(true);
        this.removeButton.setEnabled(false);
        
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(this.addButton);
        controlPanel.add(this.removeButton);

        add(new JLabel("Generators"), BorderLayout.NORTH);
        add(new JScrollPane(genList), BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);      
    }
    
    public DefaultListModel getGenListModel() {
        return this.genListModel;
    }
    
    public void setGeneratorType(int generatorType) {
        this.generatorType = generatorType;
        
        genListModel.clear();
        
        if (generatorType >= 0) {       
            
            //the last type is UserDefined which has no default
            if (generatorType < GenTemplates.NUM_CORE_GENERATORS) {
                //default generator is always the first selection 
                genListModel.addElement(GenTemplates.DefaultGenerators[generatorType]);
            }
            
            //select the default
            this.genList.setSelectedIndex(0); 
        }                
    }
            
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (genList.getSelectedIndex() == -1) { //No selection: disable remove button.
                removeButton.setEnabled(false);
            } else { //Single selection: permit all operations.
                removeButton.setEnabled(true);             
            }
            
            //note: value is -1 if nothing is selected
            genEditorPanel.setGenerator(generatorType, genList.getSelectedIndex());
        }
    }
    
    public void addAction(String action) {
        int index = genList.getSelectedIndex();
        int size = genListModel.getSize();
        GenTemplates selectedAction = (GenTemplates)genList.getSelectedValue();
        
        //valueChanged() will be triggered causing genEditor to be updated
    }           

    private GeneratorEditorPanel    genEditorPanel;
    private DefaultListModel        genListModel;
    private JList                   genList;
    private JButton                 addButton;
    private JButton                 removeButton;
    private int                     generatorType;
}


