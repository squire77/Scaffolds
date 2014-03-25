package scaffold.uml.basic.cm;

import scaffold.uml.basic.UmlElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import scaffold.uml.UmlIdentifiable;


@XmlRootElement(namespace = "scaffold.uml.basic.cm")

@XmlType(propOrder = { "ID", "navigabilityEnabled", "composite", "aggregation", "endPoint1", "endPoint2"})
public class CmAssociation extends UmlElement implements Observer, UmlIdentifiable{
    public static void initialize() {
        resetIDGenerator();
        assocs.clear();
    }        
    
    public static CmAssociation create(boolean isAggregation, boolean end1IsAggregate, boolean end2IsAggregate, CmClass clazz1, CmClass clazz2) {
        CmAssociation assoc = new CmAssociation(isAggregation);
        assoc.setID(createUniqueAssociationID());  
        assoc.createEndPoints(end1IsAggregate, end2IsAggregate, clazz1, clazz2);
        assocs.put(assoc.getID(), assoc);        
        return assoc;
    }
        
    public static Map<String,CmAssociation> getAllAssociations() {
        return assocs;
    }
    
    public static CmAssociation getAssociation(String id) {
        return assocs.get(id);
    }    
    
    public static CmAssociation removeAssociation(String id) {
        return assocs.remove(id);
    }
    
    private static void resetIDGenerator() {
        uniqueAssocIDCount = 1;  
    }
    
    private static String createUniqueAssociationID() {
        String assocKey = "ASSOC_" + Integer.toString(uniqueAssocIDCount++);
        
        while (assocs.containsKey(assocKey)) {
            assocKey = "ASSOC_" + Integer.toString(uniqueAssocIDCount++);
        }
        
        return assocKey;
    }
    
    private static int                          uniqueAssocIDCount = 1;
    
    private static Map<String,CmAssociation>    assocs = new HashMap<String,CmAssociation>();
    
    //*** INSTANCE BEGINS HERE ***********************************************   
        
    private CmAssociation() {
    }
    
    private CmAssociation(boolean isAggregation) {
        this.isComposite = false;
        this.isAggregation = isAggregation;
    } 
    
    //call this after setting the association ID
    private void createEndPoints(boolean end1IsAggregate, boolean end2IsAggregate, CmClass clazz1, CmClass clazz2) {
        this.endPoint1 = new CmAssocEnd(this, end1IsAggregate);
        this.endPoint2 = new CmAssocEnd(this, end2IsAggregate); 
        
        clazz1.addEndPoint(this.endPoint1);        
        clazz2.addEndPoint(this.endPoint2);
        
        //bi-directional navigability is assumed by default
        this.endPoint1.setNavigable(true);
        this.endPoint2.setNavigable(true);
        
        this.endPoint1.addObserver(this);
        this.endPoint2.addObserver(this);                
    }       

    @Override
    public void update(Observable o, Object obj) {
        //only one on means default of bi-directional navigability IS NOT assumed
        if ((endPoint1.isNavigable() && !endPoint2.isNavigable()) ||
            (!endPoint1.isNavigable() && endPoint2.isNavigable())) {
            setNavigabilityEnabled(true);
        }
        
        //both off means bi-directional navigability IS assumed
        if (!endPoint1.isNavigable() && !endPoint2.isNavigable()) {
            setNavigabilityEnabled(true);
        }
        
        setChanged();
        notifyObservers(obj);
    }
        
    public String getID() {
        return this.ID;
    }
    public void setID(String ID) {
        this.ID = ID; 
    }
    
    public CmAssocEnd getOtherEndPoint(CmAssocEnd ep) {
        if (this.endPoint1 == ep) {
            return this.endPoint2;
        } else {
            return this.endPoint1;
        }
    }
        
    public CmAssocEnd getEndPoint1() {
        return this.endPoint1;
    }
    
    public CmAssocEnd getEndPoint2() {
        return this.endPoint2;
    }    
    
    public boolean isNavigabilityEnabled() {
        return this.isNavigabilityEnabled;
    }
    public void setNavigabilityEnabled(boolean enabled) {
        this.isNavigabilityEnabled = enabled;
        
        setChanged();
        notifyObservers();
    }
    
    public boolean isComposite() {
        return isComposite;
    }
    public void setComposite(boolean isComposite) {
        this.isComposite = isComposite;      
        
        if (isAggregation()) {
            if (endPoint1.isAggregate()) {
                endPoint1.setComposite(this.isComposite);
            }
            if (endPoint2.isAggregate()) {
                endPoint2.setComposite(this.isComposite);
            }
        }
        
        setChanged();
        notifyObservers();
    }
    
    public boolean isAggregation() {
        return isAggregation;
    }
    public void setAggregation(boolean isAggregation) {
        this.isAggregation = isAggregation;           
        
        setChanged();
        notifyObservers();
    }  
         
    private String                  ID;
    private boolean                 isNavigabilityEnabled;
    private boolean                 isComposite;
    private boolean                 isAggregation;    
    
    @XmlElement(name = "endPoint1")
    private CmAssocEnd              endPoint1;
    
    @XmlElement(name = "endPoint2")
    private CmAssocEnd              endPoint2;   
}
