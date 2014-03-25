package scaffold.uml.datatypes;

import scaffold.uml.basic.UmlDataType;
import scaffold.uml.basic.UmlPackage;
import scaffold.uml.basic.UmlType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(namespace = "scaffold.uml.datatype")
public class AslMap extends UmlDataType {   
    public static AslMap create(UmlType baseType) {
        AslMap map = (AslMap) UmlType.getTypeByName("Map<" + baseType.getName() + ">");
        
        if (map == null) {
            map = new AslMap(baseType);
            UmlType.registerType(map);
            UmlPackage.getDefaultPackage().addOwnedType(map.getID());        
        }
        
        return map;
    }
    
    private AslMap() {        
    }
    
    private AslMap(UmlType baseType) {
        super("Map<" + baseType.getName() + ">");
        this.baseTypeID = baseType.getID();
    }   

    @Override
    public boolean isMap() { return true; }    
    
    public UmlType getBaseType() {
        return UmlType.getType(this.baseTypeID);
    }
    
    public String baseTypeID;
}
