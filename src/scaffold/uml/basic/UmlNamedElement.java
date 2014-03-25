package scaffold.uml.basic;

//import javax.xml.bind.annotation.XmlAttribute;
//import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder = { "name", "visibilityKind" })
abstract public class UmlNamedElement extends UmlElement {
    //@XmlType(name = "visibilityKind")
    //@XmlEnum
    //public enum VisibilityKind { PUBLIC, PRIVATE, PROTECTED, PACKAGE }

    public static String visibilityKindToString(int kind) {
        switch (kind){
            case 0: return "PUBLIC";
            case 1: return "PRIVATE";
            case 2: return "PROTECTED";
            case 3: return "PACKAGE";
            default: return "UNKNOWN VISIBLITY KIND (" + kind + ")";
        }
    }
    
    public static int valueOfVisibilityKind(String name) {
        if (name.equalsIgnoreCase("PUBLIC")) return 0;
        if (name.equalsIgnoreCase("PRIVATE")) return 1;
        if (name.equalsIgnoreCase("PROTECTED")) return 2;
        if (name.equalsIgnoreCase("PACKAGE")) return 3;
        return -1;
    }
    public static boolean isValidVisibilityKind(String name) {
        return (name != null && 
                (name.equalsIgnoreCase("PUBLIC") ||
                 name.equalsIgnoreCase("PRIVATE") ||
                 name.equalsIgnoreCase("PROTECTED") ||
                 name.equalsIgnoreCase("PACKAGE")));
    }
    
    protected UmlNamedElement() {
    }
    
    protected UmlNamedElement(String name) {        
        this.name = name;
        this.visibilityKind = 0;
        //this.visibility = VisibilityKind.PUBLIC;
    }        
    
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        if (name == null) {
            return;
        }
        
        this.name = name;
        
        setChanged();
        notifyObservers();
    }
    
    public int getVisibilityKind() {
        return this.visibilityKind;
    }
    public void setVisibilityKind(int visibilityKind) {
        this.visibilityKind = visibilityKind;
    }
    public String getVisibilityKindAsString() {
        return visibilityKindToString(visibilityKind);
    }
    public boolean setVisibilityKind(String visibilityStr) {
        if (isValidVisibilityKind(visibilityStr)) {
            this.visibilityKind = valueOfVisibilityKind(visibilityStr);
            return true;
        }
        
        return false;
    }
    
    /*
    public VisibilityKind getVisibilityKind() {
        return this.visibility;
    }
    public void setVisibility(VisibilityKind visibility) {
        this.visibility = visibility;
        
        setChanged();
        notifyObservers();
    } 
    */ 
    
    private String              name; 
    private int                 visibilityKind;
    //private VisibilityKind      visibility;
}