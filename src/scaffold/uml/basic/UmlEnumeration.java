package scaffold.uml.basic;

import java.util.ArrayList;
import java.util.List;


public class UmlEnumeration extends UmlDataType {
    public static UmlEnumeration create(String name) {
        UmlEnumeration enumeration = new UmlEnumeration(name);
        registerType(enumeration);
        return enumeration;
    }
    
    private UmlEnumeration() {        
    }
    
    private UmlEnumeration(String name) {
        super(name);
    }    
    
    public List<UmlEnumerationLiteral> getOwnedLiterals() {
        return this.ownedLiterals;
    }
    public boolean addOwnedLiteral(UmlEnumerationLiteral literal) {
        if (literal == null) {
            return false;
        }
        
        boolean success = this.ownedLiterals.add(literal);
        
        if (success) {
            setChanged();
            notifyObservers();
        }
        
        return success;
    }
    public boolean removeOwnedLiteral(UmlEnumerationLiteral literal) {
        if (literal == null) {
            return false;
        }
        
        boolean success = this.ownedLiterals.remove(literal);
        
        if (success) {
            setChanged();
            notifyObservers();
        }
        
        return success;
    }
        
    private List<UmlEnumerationLiteral> ownedLiterals = new ArrayList<UmlEnumerationLiteral>();
}
