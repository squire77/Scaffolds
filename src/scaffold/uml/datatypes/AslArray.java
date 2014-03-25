package scaffold.uml.datatypes;

import scaffold.uml.basic.UmlDataType;
import scaffold.uml.basic.UmlPackage;
import scaffold.uml.basic.UmlType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(namespace = "scaffold.uml.datatype")
public class AslArray extends UmlDataType {
    public static AslArray create(UmlType baseType) {
        AslArray arr = (AslArray) UmlType.getTypeByName("Array[" + baseType.getName() + "]");
        
        if (arr == null) {
            arr = new AslArray(baseType);
            UmlType.registerType(arr);
            UmlPackage.getDefaultPackage().addOwnedType(arr.getID());
        }
        
        return arr;
    }
    
    private AslArray() {        
    }
    
    private AslArray(UmlType baseType) {
        super("Array[" + baseType.getName() + "]");
        this.baseTypeID = baseType.getID();
    }  

    @Override
    public boolean isArray() { return true; }        
    
    public UmlType getBaseType() {
        return UmlType.getType(this.baseTypeID);
    }
    
    public String baseTypeID;
}
