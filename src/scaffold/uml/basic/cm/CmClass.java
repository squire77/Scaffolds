package scaffold.uml.basic.cm;

import scaffold.uml.basic.UmlType;
import scaffold.uml.basic.UmlPackage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import scaffold.uml.UmlIdentifiable;


@XmlRootElement(namespace = "scaffold.uml.basic.cm")

@XmlType(propOrder = { "isInterface", "abstract", "finalSpecialization", "superClassIDs", "ownedOperations", "ownedAttributes" })
public class CmClass extends UmlType implements Observer, UmlIdentifiable  {   
    public static void initialize() {
        classes.clear();
    }
    
    public static CmClass create(String name, boolean isInterface) {
        if (!isClassNameUndefined(name)) {
            return null; //not an exception. this is a possible user error case
        }
        
        CmClass clazz = new CmClass(name, isInterface);        
        UmlType.registerType(clazz);        
        UmlPackage.getDefaultPackage().addOwnedType(clazz.getID());        
        classes.put(clazz.getID(), clazz);
        return clazz;
    }
    
    public static boolean isClassNameUndefined(String name) {
        for (CmClass c: classes.values()) {
            if (c.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }
    
    public static Map<String,CmClass> getAllClasses() {
        return classes;        
    }
    
    public static CmClass getClass(String id) {
        return classes.get(id);
    }
    
    public static CmClass removeClass(String id) {
        return classes.remove(id);
    }
    
    public static CmClass getClassByName(String name) {
        //ensure the class exists
        if (isClassNameUndefined(name)) {
            return null; //not an exception. this is a possible user error case
        }
        
        CmClass clazz = null;
        
        for (CmClass c: classes.values()) {
            if (c.getName().equals(name)) {
                clazz = c;
            }
        }
        
        return clazz;
    }
    
    private static Map<String,CmClass> classes = new HashMap<String,CmClass>();
    
    //*** INSTANCE BEGINS HERE ***********************************************
    
    //for internalization only
    private CmClass() {        
    }    
    
    private CmClass(String name, boolean isInterface) {
        super(name);    
        
        this.isInterface = isInterface;       
        this.isAbstract = false;
        this.isFinalSpecialization = false;  
    }
    
    @Override
    public void update(Observable o, Object obj) {
        setChanged();
        notifyObservers(obj);
    }          
        
    public boolean isInterface() {
        return this.isInterface;
    }    
    
    public boolean isAbstract() {
        return isAbstract;
    }
    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
        
        setChanged();
        notifyObservers();
    }    
    
    public boolean isFinalSpecialization() {
        return isFinalSpecialization;
    }
    public void setFinalSpecialization(boolean isFinalSpecialization) {
        this.isFinalSpecialization = isFinalSpecialization;
        
        setChanged();
        notifyObservers();
    }

    //*** CONTAINED PROPERTY LISTS *******************************************    
   
    public List<CmClass> getSuperClassesThatAreInterfaces() {
        List<CmClass> interfaces = new ArrayList<CmClass>();       
        
        for (CmClass c: this.superClasses) {
            if (c.isInterface()) { 
                interfaces.add(c);
            }
        }
        
        return interfaces;
    }
            
    public List<CmClass> getSuperClassesThatAreNotInterfaces() {
        List<CmClass> supers = new ArrayList<CmClass>();       
        
        for (CmClass c: this.superClasses) {
            if (!c.isInterface()) { 
                supers.add(c);
            }
        }
        
        return supers;          
    }                
       
    public List<CmClass> getSuperClasses() {
        return this.superClasses;
    }
    
    public List<String> getSuperClassIDs() {
        return this.superClassIDs;
    }
    
    public List<CmOperation> getOwnedOperations() {
        return this.ownedOperations;
    }
    
    public List<CmProperty> getOwnedAttributes() {
        return this.ownedAttributes;
    }
    
    public List<CmAssocEnd> getAllAssociates() {
        List<CmAssocEnd> assocs = new ArrayList<CmAssocEnd>();
        
        for (CmAssocEnd ep: this.endPoints) {
            CmAssociation rel = ep.getAssociation();
            CmAssocEnd otherEP = rel.getOtherEndPoint(ep);
            assocs.add(otherEP);
        }
        
        return assocs; 
    }
    
    public List<CmAssocEnd> getNavigableAssociates() {
        List<CmAssocEnd> assocs = new ArrayList<CmAssocEnd>();
        
        for (CmAssocEnd ep: this.endPoints) {
            CmAssociation assoc = ep.getAssociation();
            CmAssocEnd otherEP = assoc.getOtherEndPoint(ep);

            //if assoc.isNavigabilityEnabled() then bi-directional navigability is assumed
            if (assoc.isNavigabilityEnabled() || otherEP.isNavigable()) {                
                assocs.add(otherEP);
            }
        }
        
        return assocs; 
    }               
    
    //*** GET PROPERTY BY NAME ***********************************************
    
    public CmClass getInterfaceByName(String name) {
        List<CmClass> interfaces = getSuperClassesThatAreInterfaces();
        
        for (CmClass intrfce: interfaces) {
            if (intrfce.getName().equals(name)) {
                return intrfce;
            }
        }
        return null;
    }
    
    public CmClass getSuperClassByName(String name) {
        List<CmClass> supers = getSuperClassesThatAreNotInterfaces();
        
        for (CmClass superClass: supers) {
            if (superClass.getName().equals(name)) {
                return superClass;
            }
        }
        return null;
    }    
    
    public CmOperation getOwnedOperationByName(String name) {
        for (CmOperation op: this.ownedOperations) {
            if (op.getName().equals(name)) {
                return op;
            }
        } 
        return null;
    }
    
    public CmProperty getOwnedAttributeByName(String name) {
        for (CmProperty attrib: this.ownedAttributes) {
            if (attrib.getName().equals(name)) {
                return attrib;
            }
        }
        return null;
    }
    
    //*** ADD/REMOVE METHODS *************************************************    
                 
    public boolean addSuperClass(CmClass c) {
        if (c == null || c == this || this.superClasses.contains(c)) {
            return false;
        }
        
        boolean success = this.superClasses.add(c);

        if (success) {   
            this.superClassIDs.add(c.getID());
            
            setChanged();
            notifyObservers();        
            c.addObserver(this);
        }
        
        return success;
    }
    public boolean removeSuperClass(CmClass c) {
        if (c == null) {
            return false;
        }
        
        boolean success = this.superClasses.remove(c);            
        
        if (success) {
            this.superClassIDs.remove(c.getID());
            
            setChanged();
            notifyObservers();            
            c.deleteObserver(this);            
        }
        
        return success;
    }   
    
    public CmOperation addOwnedOperation(String name) {
        if (name == null) {
            return null;
        }
        
        CmOperation o = new CmOperation(name);
        
        if (this.ownedOperations.add(o)) {        
            setChanged();
            notifyObservers();        
            o.addObserver(this);

            return o;
        }
        
        return null;
    }
    public CmOperation removeOwnedOperation(String name) {
        if (name == null) {
            return null;
        }
        
        CmOperation o = getOwnedOperationByName(name);
        
        boolean success = this.ownedOperations.remove(o);            
        
        if (success) {
            setChanged();
            notifyObservers();            
            o.deleteObserver(this);            
        }
        
        return o;
    }            

    public CmProperty addOwnedAttribute(String name) {
        if (name == null) {
            return null;
        }
        
        CmProperty a = new CmProperty(name);
        
        boolean success = this.ownedAttributes.add(a); //make sure we add before we notify        
        
        if (success) {
            setChanged();
            notifyObservers();        
            a.addObserver(this);

            return a;
        }
        
        return null;
    }
    public CmProperty removeOwnedAttribute(String name) {
        if (name == null) {
            return null;
        }
        
        CmProperty a = getOwnedAttributeByName(name);        
        
        boolean success = this.ownedAttributes.remove(a);
        
        if (success) { 
            setChanged();
            notifyObservers();       
            a.deleteObserver(this); 
        }        
        
        return a;
    }                 
    
    //only used in this package; use getSuperClassesThatAreNotInterfaces(), getSuperClassesThatAreInterfaces(), getAssociates()
    //to derive relationships
    List<CmAssocEnd> getEndPoints() {
        return this.endPoints;
    }
    public boolean addEndPoint(CmAssocEnd endPoint) {
        if (endPoint == null) {
            return false;
        }
        
        boolean success = this.endPoints.add(endPoint);
        
        if (success) {
            endPoint.setClassID(this.getID());
            setChanged();
            notifyObservers(); 
        }
        
        return success;
    }
    public boolean removeEndPoint(CmAssocEnd endPoint) {
        if (endPoint == null) {
            return false;
        }
        
        boolean success = this.endPoints.remove(endPoint);
        
        if (success) {            
            setChanged();
            notifyObservers(); 
        }
        
        return success;
    }        
    
    // the name of the property is 'isInterface' but in XML, it is renamed to 'interface'
    @XmlElement(name = "interface")
    private boolean              isInterface;
    
    //these use a bean convention (get/set) and so, are XmlElements by default
    private boolean              isAbstract;
    private boolean              isFinalSpecialization;        
        
    @XmlTransient
    private List<CmClass>        superClasses = new ArrayList<CmClass>();
    
    @XmlElementWrapper(name = "superClasses")
    @XmlElement(name = "superClassID")
    private List<String>         superClassIDs = new ArrayList<String>();
          
    @XmlElementWrapper(name = "ownedOperations")
    @XmlElement(name = "operation")
    private List<CmOperation>    ownedOperations = new ArrayList<CmOperation>(); 
    
    @XmlElementWrapper(name = "ownedAttributes")
    //@XmlElement(name = "attribute")
    private List<CmProperty>     ownedAttributes = new ArrayList<CmProperty>();
    
    //this covers association and aggregation
    @XmlTransient
    private List<CmAssocEnd>     endPoints = new ArrayList<CmAssocEnd>();  
}
