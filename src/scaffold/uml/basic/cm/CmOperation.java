package scaffold.uml.basic.cm;

import scaffold.uml.basic.UmlTypedElement;
import scaffold.uml.datatypes.AslVoid;
import scaffold.uml.basic.UmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder = { "abstract", "static", "finalSpecialization", "parameters", "raisedExceptions", "body" })
public class CmOperation extends UmlTypedElement implements Observer {
    private CmOperation() {
    }
    
    public CmOperation(String name) {
        super(name);

        //Operations return void by default
        setTypeID(AslVoid.getInstance().getID());
    }

    @Override
    public void update(Observable o, Object obj) {
        setChanged();
        notifyObservers(obj);
    }
    
    public boolean isAbstract() {
        return this.isAbstract;
    }
    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
        
        setChanged();
        notifyObservers();        
    } 
        
    public boolean isStatic() {
        return this.isStatic;
    }
    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
        
        setChanged();
        notifyObservers();        
    }    
    
    public boolean isFinalSpecialization() {
        return this.isFinalSpecialization;
    }
    public void setFinalSpecialization(boolean isFinal) {
        this.isFinalSpecialization = isFinal;
        
        setChanged();
        notifyObservers();        
    }      
    
    //*** CONTAINED PROPERTY LISTS *******************************************    
    
    public List<CmParameter> getParameters() {
        return this.parameters;
    }
    
    public List<UmlType> getRaisedExceptions() {
        return this.raisedExceptions;
    }
    
    //*** GET PROPERTY BY NAME ***********************************************
    
    public CmParameter getParameterByName(String name) {
        for (CmParameter param: parameters) {
            if (param.getName().equals(name)) {
                return param;
            }                
        }
        return null;
    }    

    //*** ADD/REMOVE METHODS *************************************************  
    
    public CmParameter addParameter(String name) {
        if (name == null) {
            return null;
        }
        
        CmParameter p = new CmParameter(name);
        
        boolean success = this.parameters.add(p); //make sure we add before we notify       
        
        if (success) {
            setChanged();
            notifyObservers();   
            p.addObserver(this);

            return p;
        }
        
        return null;
    }
    public CmParameter removeParameter(String name) {
        if (name == null) {
            return null;
        }
        
        CmParameter p = getParameterByName(name);
                
        boolean success = this.parameters.remove(p); 
            
        if (success) {
            setChanged();
            notifyObservers(); 
            p.deleteObserver(this);
        }
        
        return p;
    }      

    public boolean addRaisedException(UmlType excep) {
        if (excep == null) {
            return false;
        }
        
        boolean success = this.raisedExceptions.add(excep);
        
        if (success) {
            setChanged();
            notifyObservers();
            excep.addObserver(this);
        }
        
        return success;
    }
    public boolean removeRaisedException(UmlType excep) {
        if (excep == null) {
            return false;
        }
        
        boolean success = this.raisedExceptions.remove(excep);
        
        if (success) {
            setChanged();
            notifyObservers();
            excep.addObserver(this);
        }
        
        return success;
    }    

    public String getBody() {
        return this.body;
    }
    public void setBody(String body) {
        this.body = body;
        
        setChanged();
        notifyObservers();        
    }
    
    public void addStatement(String statement) {
        setChanged();
        notifyObservers();
    }            
    
    @Override
    public String toString() {
        return getName();
    }
    
    private boolean isAbstract;
    private boolean isStatic; // feature property, but I'm not creating a CmFeature
    private boolean isFinalSpecialization;               
    
    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    private List<CmParameter>     parameters = new ArrayList<CmParameter>(); 
    
    @XmlElementWrapper(name = "exceptions")
    @XmlElement(name = "exception")
    private List<UmlType>         raisedExceptions = new ArrayList<UmlType>();
    
    //this is null when the method is simply a signature or abstract method        
    private String body;      
}
