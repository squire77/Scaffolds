package scaffold.forms;

import scaffold.console.ErrorDialog;
import scaffold.uml.basic.UmlPackage;
import scaffold.uml.basic.UmlNamedElement;
import scaffold.uml.basic.UmlType;
import scaffold.uml.basic.cm.CmClass;
import scaffold.uml.basic.cm.CmProperty;
import scaffold.uml.basic.cm.CmOperation;
import scaffold.uml.datatypes.AslUserDefinedType;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.table.TableModel;


public class UMLClassForm extends javax.swing.JFrame {
    public UMLClassForm(CmClass clazz) {
        this.clazz = clazz;
        this.parametersForm = new UMLParametersForm();
        
        initComponents();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);        
        
        addAttribPopup();
        addMethodPopup();
    }
     
    private void addAttribPopup() {
        attribPopup = new PopupMenu();
        add(attribPopup);

        MenuItem remove = new MenuItem("Remove");
        attribPopup.add(remove);
        
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                removeAttribute(attribToDelete);
            }
        } );
    }
     
    private void addMethodPopup() {
        methodPopup = new PopupMenu();
        add(methodPopup);

        MenuItem remove = new MenuItem("Remove");
        methodPopup.add(remove);
        
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                removeMethod(methodToDelete);
            }
        } );
    }
    
    public void loadValues() {                
        nameField.setText(clazz.getName());
        
        if (clazz.getPackage() != null) {
            UmlPackage packge = UmlPackage.getPackage(clazz.getPackage());
            packageField.setText(packge.getName());
        }
        
        abstractCheckbox.setSelected(clazz.isAbstract());
        finalSpecializationCheckbox.setSelected(clazz.isFinalSpecialization());
        
        loadAttribValues();
        loadMethodValues();
    }
     
    private void loadAttribValues() {
        TableModel tabMod = attribTable.getModel();

        //first, clear the table
        attribNames.clear();
        for (int i = 0; i < tabMod.getRowCount(); i++) {
            for(int j = 0; j < tabMod.getColumnCount(); j++) {
                tabMod.setValueAt(null, i, j);
            }
        }               
        
        int row = 0;
        for (CmProperty attrib: clazz.getOwnedAttributes()) {
            attribNames.add(attrib.getName());
            tabMod.setValueAt(attrib.getName(), row, 0);
            
            if (attrib.getType() != null) {              
                tabMod.setValueAt(attrib.getType().getName(), row, 1);
            }
            
            tabMod.setValueAt(attrib.getVisibilityKindAsString(), row, 2);
            tabMod.setValueAt(attrib.isStatic(), row, 3);
            row++;
        }        
    }
    
    private void loadMethodValues() {
        TableModel tabMod = methodTable.getModel();

        //first, clear the table
        methodNames.clear();
        for (int i = 0; i < tabMod.getRowCount(); i++) {
            for(int j = 0; j < tabMod.getColumnCount(); j++) {
                tabMod.setValueAt(null, i, j);
            }
        }           
        
        int row = 0;
        for (CmOperation operation: clazz.getOwnedOperations()) {
            methodNames.add(operation.getName());
            tabMod.setValueAt(operation.getName(), row, 0);
            tabMod.setValueAt(operation.getType().getName(), row, 1);
            tabMod.setValueAt(operation.getVisibilityKindAsString(), row, 2);
            tabMod.setValueAt(operation.isStatic(), row, 3);
            tabMod.setValueAt(operation.isAbstract(), row, 4);
            tabMod.setValueAt(operation, row, 5);            
            row++;
        }            
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane4 = new javax.swing.JTabbedPane();
        generalPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        descriptionArea = new javax.swing.JTextArea();
        descriptionLabel = new javax.swing.JLabel();
        generalOkButton = new javax.swing.JButton();
        generalCancelButton = new javax.swing.JButton();
        packageLabel = new javax.swing.JLabel();
        packageField = new javax.swing.JTextField();
        abstractCheckbox = new javax.swing.JCheckBox();
        finalSpecializationCheckbox = new javax.swing.JCheckBox();
        generalApply = new javax.swing.JButton();
        attributePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        attribTable = new javax.swing.JTable();
        attribTable.putClientProperty("terminateEditOnFocusLost", true);

        attribTable.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent event) {                 
                if (event.isPopupTrigger()) {
                    attribToDelete = attribTable.rowAtPoint(event.getPoint());
                    attribPopup.show(UMLClassForm.this, 
                        event.getPoint().x, event.getPoint().y+55);  
                }
            }
        });
        attributeOK = new javax.swing.JButton();
        attributeCancel = new javax.swing.JButton();
        attribApply = new javax.swing.JButton();
        methodPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        methodTable = new javax.swing.JTable();
        methodTable.putClientProperty("terminateEditOnFocusLost", true);

        methodTable.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent event) {                 
                if (event.isPopupTrigger()) {
                    methodToDelete = methodTable.rowAtPoint(event.getPoint());
                    methodPopup.show(UMLClassForm.this, 
                        event.getPoint().x, event.getPoint().y+55);  
                }
            }
        });
        methodOK = new javax.swing.JButton();
        methodCancel = new javax.swing.JButton();
        opApply = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        nameLabel.setText("Name");

        nameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameFieldActionPerformed(evt);
            }
        });

        descriptionArea.setColumns(20);
        descriptionArea.setRows(5);
        jScrollPane3.setViewportView(descriptionArea);

        descriptionLabel.setText("Description");

        generalOkButton.setText("OK");
        generalOkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generalOkButtonActionPerformed(evt);
            }
        });

        generalCancelButton.setText("Cancel");
        generalCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generalCancelButtonActionPerformed(evt);
            }
        });

        packageLabel.setText("Package");

        packageField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packageFieldActionPerformed(evt);
            }
        });

        abstractCheckbox.setText("Abstract");

        finalSpecializationCheckbox.setText("Final Specialization");
        finalSpecializationCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finalSpecializationCheckboxActionPerformed(evt);
            }
        });

        generalApply.setText("Apply");
        generalApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generalApplyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout generalPanelLayout = new javax.swing.GroupLayout(generalPanel);
        generalPanel.setLayout(generalPanelLayout);
        generalPanelLayout.setHorizontalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(generalPanelLayout.createSequentialGroup()
                        .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nameField))
                    .addGroup(generalPanelLayout.createSequentialGroup()
                        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(generalPanelLayout.createSequentialGroup()
                                    .addComponent(generalApply)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(generalOkButton)
                                    .addGap(52, 52, 52)
                                    .addComponent(generalCancelButton)
                                    .addGap(77, 77, 77))
                                .addGroup(generalPanelLayout.createSequentialGroup()
                                    .addComponent(packageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(packageField, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(descriptionLabel)
                            .addGroup(generalPanelLayout.createSequentialGroup()
                                .addComponent(abstractCheckbox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(finalSpecializationCheckbox)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        generalPanelLayout.setVerticalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(packageLabel)
                    .addComponent(packageField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(abstractCheckbox)
                    .addComponent(finalSpecializationCheckbox))
                .addGap(18, 18, 18)
                .addComponent(descriptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(generalOkButton)
                    .addComponent(generalCancelButton)
                    .addComponent(generalApply))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("General", generalPanel);

        attribTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Name", "Type", "Visibility", "Class Scope"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(attribTable);

        attributeOK.setText("OK");
        attributeOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attributeOKActionPerformed(evt);
            }
        });

        attributeCancel.setText("Cancel");
        attributeCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attributeCancelActionPerformed(evt);
            }
        });

        attribApply.setText("Apply");
        attribApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attribApplyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout attributePanelLayout = new javax.swing.GroupLayout(attributePanel);
        attributePanel.setLayout(attributePanelLayout);
        attributePanelLayout.setHorizontalGroup(
            attributePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(attributePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(attribApply)
                .addGap(177, 177, 177)
                .addComponent(attributeOK)
                .addGap(46, 46, 46)
                .addComponent(attributeCancel)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
        );
        attributePanelLayout.setVerticalGroup(
            attributePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(attributePanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(attributePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(attributeCancel)
                    .addComponent(attributeOK)
                    .addComponent(attribApply))
                .addGap(0, 22, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("Attributes", attributePanel);

        methodTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Name", "Return Type", "Visibility", "Class Scope", "Abstract", "Parameters"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        methodTable.getColumn("Parameters").setCellRenderer(new ButtonRenderer());
        methodTable.getColumn("Parameters").setCellEditor(new ButtonEditor(new JCheckBox(), parametersForm));
        jScrollPane2.setViewportView(methodTable);
        methodTable.getColumnModel().getColumn(3).setResizable(false);
        methodTable.getColumnModel().getColumn(5).setResizable(false);

        methodOK.setText("OK");
        methodOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                methodOKActionPerformed(evt);
            }
        });

        methodCancel.setText("Cancel");
        methodCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                methodCancelActionPerformed(evt);
            }
        });

        opApply.setText("Apply");
        opApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opApplyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout methodPanelLayout = new javax.swing.GroupLayout(methodPanel);
        methodPanel.setLayout(methodPanelLayout);
        methodPanelLayout.setHorizontalGroup(
            methodPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(methodPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(opApply)
                .addGap(178, 178, 178)
                .addComponent(methodOK)
                .addGap(49, 49, 49)
                .addComponent(methodCancel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
        );
        methodPanelLayout.setVerticalGroup(
            methodPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(methodPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(methodPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(methodOK)
                    .addComponent(methodCancel)
                    .addComponent(opApply))
                .addGap(0, 22, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("Operations", methodPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 533, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jTabbedPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 487, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameFieldActionPerformed

    private void attributeOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attributeOKActionPerformed
        handleOK();        
        this.dispose();        
    }//GEN-LAST:event_attributeOKActionPerformed

    private void generalOkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generalOkButtonActionPerformed
        handleOK();        
        this.dispose();        
    }//GEN-LAST:event_generalOkButtonActionPerformed

    private void generalCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generalCancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_generalCancelButtonActionPerformed

    private void methodOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_methodOKActionPerformed
        handleOK();                
        this.dispose();
    }//GEN-LAST:event_methodOKActionPerformed

    private void attributeCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attributeCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_attributeCancelActionPerformed

    private void methodCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_methodCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_methodCancelActionPerformed

    private void packageFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packageFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_packageFieldActionPerformed

    private void finalSpecializationCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finalSpecializationCheckboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_finalSpecializationCheckboxActionPerformed

    private void generalApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generalApplyActionPerformed
        handleOK();
    }//GEN-LAST:event_generalApplyActionPerformed

    private void attribApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attribApplyActionPerformed
        handleOK();
    }//GEN-LAST:event_attribApplyActionPerformed

    private void opApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opApplyActionPerformed
        handleOK();
    }//GEN-LAST:event_opApplyActionPerformed

    private void handleOK() {
        if (!clazz.getName().equals(nameField.getText())) {
            if (CmClass.isClassNameUndefined(nameField.getText())) {
                clazz.setName(nameField.getText());
            } else {
                ErrorDialog.error("Duplicate class name " + nameField.getText() + ". Please choose a unique name.");
                return;
            }
        }
        
        if (clazz.isAbstract() != abstractCheckbox.isSelected()) {
            clazz.setAbstract(abstractCheckbox.isSelected());
        }
        if (clazz.isFinalSpecialization() != finalSpecializationCheckbox.isSelected()) {
            clazz.setFinalSpecialization(finalSpecializationCheckbox.isSelected());
        }
        
        UmlPackage packge = (clazz.getPackage() != null) ?
                UmlPackage.getPackage(clazz.getPackage()) : null;
        if (packge != null) {            
            if (!packge.getName().equals(packageField.getText())) {
                if (UmlPackage.isPackageNameUndefined(packageField.getText())) {
                    packge.setName(packageField.getText());
                } else {
                    ErrorDialog.error("Duplicate package name " + packageField.getText() + ". Please choose a unique name.");
                    return;
                }
            }
        } else {
            if (!packageField.getText().isEmpty()) {
                if (UmlPackage.isPackageNameUndefined(packageField.getText())) {
                    packge = UmlPackage.create(packageField.getText());
                    packge.setName(packageField.getText());
                    packge.addOwnedType(clazz.getID());
                } else {
                    packge = UmlPackage.getPackageByName(packageField.getText());
                    packge.addOwnedType(clazz.getID());
                }
            }
        }
        
        TableModel tabMod1 = attribTable.getModel();
        for (int i=0; i<tabMod1.getRowCount(); i++) {
            String attribName = (String) tabMod1.getValueAt(i, 0);
            if (attribName != null && !attribName.equals("")) {
                String type = (String) tabMod1.getValueAt(i, 1);
                String accessLevel = (String) tabMod1.getValueAt(i, 2);
                Boolean isClassScope = (Boolean) tabMod1.getValueAt(i, 3);
                
                UmlType t = null;
                
                //get type object for type name
                if (type != null && !type.isEmpty()) {
                    t = UmlType.getTypeByName(type);
                    
                    //create new type if it doesn't exist
                    if (t == null) {
                        t = AslUserDefinedType.create(type);                                        
                    }                    
                }
                               
                updateAttribute(i, attribName, t, accessLevel, isClassScope);
            }
        }        
        
        TableModel tabMod2 = methodTable.getModel();
        for (int i=0; i<tabMod2.getRowCount(); i++) {
            String methodName = (String) tabMod2.getValueAt(i, 0);
            if (methodName != null && !methodName.equals("")) {
                String returnTypeName = (String) tabMod2.getValueAt(i, 1);
                String accessLevel = (String) tabMod2.getValueAt(i, 2);
                Boolean isClassScope = (Boolean) tabMod2.getValueAt(i, 3);
                Boolean isAbstract = (Boolean) tabMod2.getValueAt(i, 4);
                
                UmlType rt = null;
                
                //get type object for type name
                if (returnTypeName != null) {
                    rt = UmlType.getTypeByName(returnTypeName);
                
                    //create new type if it doesn't exist
                    if (rt == null) {
                        rt = AslUserDefinedType.create(returnTypeName);                                        
                    }                                    
                }                

                updateMethod(i, methodName, rt, accessLevel, isClassScope, isAbstract);
            }
        }            
    }       

    private void removeAttribute(int row) {
        if (row >= 0) {
            String attribName = (String) attribTable.getModel().getValueAt(row, 0);
            
            if (attribName != null) {
                clazz.removeOwnedAttribute(attribName);
            }
            
            loadAttribValues();
        }
    }

    private void updateAttribute(int row, String name, UmlType type, String accessLevel, Boolean isClassScope) {
        CmProperty attr;
        if (row < attribNames.size()) {
            attr = clazz.getOwnedAttributeByName(attribNames.get(row));
            assert (attr != null) : "internal error: null attribute #" + row;
        } else {                
            attr = clazz.addOwnedAttribute(name);
        }
                
        if (name != null && !attr.getName().equals(name)) {
            attr.setName(name);
        }
        if (type != null && attr.getType() != type) {
            attr.setTypeID(type.getID());
        }        
        if (accessLevel != null && UmlNamedElement.isValidVisibilityKind(accessLevel)) {
            int access = UmlNamedElement.valueOfVisibilityKind(accessLevel);
            if (attr.getVisibilityKind() != access)  {
                attr.setVisibilityKind(access);
            }
        }
        if (isClassScope != null && attr.isStatic() != isClassScope) {
            attr.setStatic(isClassScope);
        }
    }
    
    private void removeMethod(int row) {
        if (row >= 0) {
            String methodName = (String) methodTable.getModel().getValueAt(row, 0);
            
            if (methodName != null) {
                clazz.removeOwnedOperation(methodName);
            }
            
            loadMethodValues();
        }
    }
    
    private void updateMethod(int row, String name, UmlType returnType, String accessLevel, Boolean isClassScope, Boolean isAbstract) {        
        CmOperation op;
        if (row < methodNames.size()) {
            op = clazz.getOwnedOperationByName(methodNames.get(row));
            assert (op != null) : "internal error: null attribute #" + row;
        } else {                
            op = clazz.addOwnedOperation(name);
        }
                
        if (name != null && !op.getName().equals(name)) {
            op.setName(name);
        }
        if (returnType != null && op.getType() != returnType) {
            op.setTypeID(returnType.getID());
        }
        if (accessLevel != null && UmlNamedElement.isValidVisibilityKind(accessLevel)) {
            int access = UmlNamedElement.valueOfVisibilityKind(accessLevel);
            if (op.getVisibilityKind() != access)  {
                op.setVisibilityKind(access);
            }
        }
        if (isClassScope != null && op.isStatic() != isClassScope) {
            op.setStatic(isClassScope);
        }
        if (isAbstract != null && op.isAbstract() != isAbstract) {
            op.setBody(null);
        }
    }
    
    private CmClass clazz;
    private UMLParametersForm parametersForm;
    
    private List<String> attribNames = new ArrayList<String>();
    private PopupMenu attribPopup;
    private int attribToDelete;
    
    private List<String> methodNames = new ArrayList<String>();
    private PopupMenu methodPopup;
    private int methodToDelete;    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox abstractCheckbox;
    private javax.swing.JButton attribApply;
    private javax.swing.JTable attribTable;
    private javax.swing.JButton attributeCancel;
    private javax.swing.JButton attributeOK;
    private javax.swing.JPanel attributePanel;
    private javax.swing.JTextArea descriptionArea;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JCheckBox finalSpecializationCheckbox;
    private javax.swing.JButton generalApply;
    private javax.swing.JButton generalCancelButton;
    private javax.swing.JButton generalOkButton;
    private javax.swing.JPanel generalPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JButton methodCancel;
    private javax.swing.JButton methodOK;
    private javax.swing.JPanel methodPanel;
    private javax.swing.JTable methodTable;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JButton opApply;
    private javax.swing.JTextField packageField;
    private javax.swing.JLabel packageLabel;
    // End of variables declaration//GEN-END:variables
}
