package scaffold.uml.basic.cm;

import scaffold.uml.basic.UmlTypedElement;
import java.util.Observable;
import java.util.Observer;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import scaffold.uml.datatypes.AslInteger;


@XmlType(propOrder = { "static", "readOnly", "composite", "derived", "isID", "default", "multiplicity" })
public class CmProperty extends UmlTypedElement implements Observer {
    private CmProperty() {
    }
    
    public CmProperty(String name) {
        super(name);           
        
        //Attributes are private by default
        setVisibilityKind("PRIVATE");
        
        //Attributes have type int by default
        setTypeID(AslInteger.getInstance().getID()); 
        
        this.isStatic = false;
        this.isReadOnly = false;
        this.isComposite = false;
        this.isDerived = false;
        this.isID = false;
    }
    
    @Override
    public void update(Observable o, Object obj) {
        setChanged();
        notifyObservers(obj);
    }   
    
    public boolean isStatic() {
        return this.isStatic;
    }
    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;

        setChanged();
        notifyObservers();
    }
    
    public boolean isReadOnly() {
        return this.isReadOnly;
    }
    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
        
        setChanged();
        notifyObservers();        
    }    
    
    public String getDefault() {
        return this.defaultValue;
    }
    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
        
        setChanged();
        notifyObservers();        
    }
    
    public boolean isComposite() {
        return this.isComposite;
    }
    public void setComposite(boolean isComposite) {
        this.isComposite = isComposite;
        
        setChanged();
        notifyObservers();        
    }
    
    public boolean isDerived() {
        return this.isDerived;
    }
    public void setDerived(boolean isDerived) {
        this.isDerived = isDerived;
        
        setChanged();
        notifyObservers();        
    }
        
    //ID would conflict with UmlNamedElement.ID so use isID
    public boolean getIsID() {
        return this.isID;
    }
    public void setIsID(boolean isID) {
        this.isID = isID;
        
        setChanged();
        notifyObservers();        
    }    
            
    public CmMultiplicityElement getMultiplicity() {
        if (this.multiplicity == null) {
            this.multiplicity = new CmMultiplicityElement();
            this.multiplicity.addObserver(this);
        }
        
        return this.multiplicity;
    }
    
    
    private boolean         isStatic;  // feature property, but I'm not creating a CmFeature    
    private boolean         isReadOnly;
    private String          defaultValue;
    private boolean         isComposite;
    private boolean         isDerived;
    private boolean         isID;
        
    @XmlElement(name = "multiplicity")
    private CmMultiplicityElement multiplicity;    
}
