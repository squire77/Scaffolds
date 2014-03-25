package scaffold.graphics.graph.uml;

import scaffold.uml.basic.cm.CmAssociation;
import scaffold.graphics.graph.Graph;
import scaffold.forms.UMLAssociationForm;
import java.awt.event.MouseEvent;


public class UMLAssociationLink extends UMLLink {         
    // use the UMLLink.create() to ensure initialize() is called   
    UMLAssociationLink(LinkType ltype, Graph graph, UMLEndPoint ep1, UMLEndPoint ep2, CmAssociation assoc) {
        super(ltype, graph, ep1, ep2);
        
        this.setModelElement(assoc);      
    }                              

    @Override
    public void mouseClicked(MouseEvent event) {      
        if (event.getClickCount() == 2) {
            //lazy create the form to save on load time
            if (associationForm == null) {
                this.associationForm = new UMLAssociationForm(assoc);  
            }
            
            associationForm.loadValues();
            associationForm.setLocation(event.getXOnScreen(), event.getYOnScreen());
            associationForm.setLocationRelativeTo(null); //center dialog on screen
            associationForm.setVisible(true);
        }
    }            
       
    private UMLAssociationForm      associationForm;         
}
