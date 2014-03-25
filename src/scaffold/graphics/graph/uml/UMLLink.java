package scaffold.graphics.graph.uml;

import scaffold.uml.basic.cm.CmAssociation;
import scaffold.uml.basic.cm.CmAssocEnd;
import scaffold.graphics.draggable.DraggableCanvas;
import scaffold.graphics.graph.Graph;
import scaffold.graphics.graph.Link;
import java.util.Observer;
import java.util.Observable;


abstract public class UMLLink extends Link implements Observer {   
    public enum LinkType { INHERITANCE, AGGREGATION, ASSOCIATION }
    
    public static UMLLink create(LinkType ltype, 
            Graph graph, UMLEndPoint ep1, UMLEndPoint ep2, CmAssociation assoc) {   
        UMLLink link;
        
        switch (ltype) {
            case INHERITANCE:
                link = new UMLInheritanceLink(ltype, graph, ep1, ep2);
                break;
            case AGGREGATION:
                link = new UMLAggregationLink(ltype, graph, ep1, ep2, assoc); 
                break;
            default: //ASSOCIATION
                link = new UMLAssociationLink(ltype, graph, ep1, ep2, assoc); 
        }
                      
        link.initialize();
        
        //Inheritance links do not initialize end points with the model element
        if (assoc != null) {
            //the endpoint polygons depend on the position of the other endpoint
            //so, we can't initialize them until now where we are assured the relationship is created
            //and initialized
            ep1.initialize(assoc.getEndPoint1());
            ep2.initialize(assoc.getEndPoint2());                        
        } else {
            //but, the end points must still be initialized to get their polygons installed
            ep1.initialize((CmAssocEnd) null);
            ep2.initialize((CmAssocEnd) null);
        }
        
        ep1.setDefaultLocation();
        ep2.setDefaultLocation();
        
        return link;
    }    
    
    protected UMLLink(LinkType ltype, Graph graph, UMLEndPoint ep1, UMLEndPoint ep2) {
        super(graph, ep1, ep2);  
        
        this.ltype = ltype;
    }
            
    //CmAssociation is optional since it is not used by UMLInheritance link
    public CmAssociation getModelElement() {
        return this.assoc;
    }
    protected void setModelElement(CmAssociation assoc) {
        this.assoc = assoc;
        this.assoc.addObserver(this);
    }
    
    @Override
    public void update(Observable o, Object obj) {
        ep1.getCanvas().drawObjects();
    }
    
    public String getId() {
        if (assoc != null) {            
            return assoc.getID();
        } else {
            return "no ID";
        }
    } 
            
    @Override
    public void remove(DraggableCanvas canvas) {  
        super.remove(canvas);
        
        //remove from model, but remember, inheritance links don't have an assoc
        if (this.assoc != null) {
            CmAssociation.removeAssociation(this.assoc.getID());
        }
    }
    
    public LinkType getLinkType() {
        return this.ltype;
    }

    @Override
    public UMLEndPoint getEndPoint1() {
        return (UMLEndPoint) ep1;
    }
    
    @Override
    public UMLEndPoint getEndPoint2() {
        return (UMLEndPoint) ep2;
    }    
    
    private LinkType        ltype;
    protected CmAssociation assoc;
}
