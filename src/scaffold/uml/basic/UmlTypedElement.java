package scaffold.uml.basic;


abstract public class UmlTypedElement extends UmlNamedElement {  
    public UmlTypedElement() {        
    }
    
    public UmlTypedElement(String name) {
        super(name);        
    }
    
    public String getTypeID() {
        return this.typeID;
    }
    public void setTypeID(String typeID) {
        this.typeID = typeID;
        
        setChanged();
        notifyObservers();
    }
    
    public UmlType getType() {
        return UmlType.getType(typeID);
    }    
        
    private String typeID;      
}
