package scaffold.uml.datatypes;

import scaffold.uml.basic.UmlPackage;
import scaffold.uml.basic.UmlPrimitiveType;
import scaffold.uml.basic.UmlType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(namespace = "scaffold.uml.datatype")
public class AslUserDefinedType extends UmlPrimitiveType {
    public static AslUserDefinedType create(String name) {
        AslUserDefinedType ut = new AslUserDefinedType(name);
        UmlType.registerType(ut);
        UmlPackage.getDefaultPackage().addOwnedType(ut.getID());        
        return ut;
    }
    
    private AslUserDefinedType() {        
    }
    
    private AslUserDefinedType(String name) {
        super(name);
    }    
    
    @Override
    public boolean isUserDefinedType() { return true; }    
}
