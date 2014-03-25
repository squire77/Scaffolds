package scaffold.forms;

import scaffold.uml.basic.cm.CmOperation;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;


class ButtonEditor extends DefaultCellEditor {
    public ButtonEditor(JCheckBox checkBox, UMLParametersForm popupFrame) {
        super(checkBox);
    
        this.label = "Edit";
        this.popupFrame = popupFrame; 
        button = new JButton(label);
        button.setOpaque(true);
    
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
  }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                boolean isSelected, int row, int column) {
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }
    
        //label = (value == null) ? "" : value.toString();
        button.setText("Edit");
        isPushed = true;
        
        this.operation = (CmOperation) value;
        
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed && operation != null) { // null method means we pushed an empty row
            popupFrame.setOperationToEdit(operation);
            popupFrame.loadValues();
            popupFrame.setLocationRelativeTo(null); //center dialog on screen
            popupFrame.setVisible(true);
            //JOptionPane.showMessageDialog(button, label + ": Ouch!");
        }
    
        isPushed = false;
        return operation;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
  
    private UMLParametersForm       popupFrame;
    private JButton                 button;
    private String                  label;
    private boolean                 isPushed;  
    private CmOperation             operation;
}