package scaffold.uml.basic;


public class UmlEnumerationLiteral extends UmlNamedElement {
    public UmlEnumerationLiteral() {        
    }
    
    public UmlEnumerationLiteral(String name) {
        super(name);
    }
    
    public UmlEnumeration getEnumeration() {
        return this.enumeration;
    }
    public void setEnumeration(UmlEnumeration enumeration) {
        this.enumeration = enumeration;
        
        setChanged();
        notifyObservers();
    }
    
    private UmlEnumeration enumeration;
}
