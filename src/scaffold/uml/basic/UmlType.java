package scaffold.uml.basic;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import scaffold.uml.UmlIdentifiable;
import scaffold.uml.datatypes.AslBoolean;
import scaffold.uml.datatypes.AslInteger;
import scaffold.uml.datatypes.AslString;
import scaffold.uml.datatypes.AslUnlimitedNatural;
import scaffold.uml.datatypes.AslVoid;


@XmlType(propOrder = { "ID", "packageID" })
public class UmlType extends UmlNamedElement  implements UmlIdentifiable {  
    public static boolean isPrimitiveType(String typeName) {
        if (typeName.equals("TYPE_Boolean")) return true;
        if (typeName.equals("TYPE_Integer")) return true;
        if (typeName.equals("TYPE_String")) return true;
        if (typeName.equals("TYPE_UnlimitedNatural")) return true;
        if (typeName.equals("TYPE_Void")) return true;
        
        return false;
    }
    
    public static void initialize() {
        resetIDGenerator();        
        types.clear();
        
        //register primitive types
        UmlPackage primitiveTypesPackage = UmlPackage.getPrimitiveTypesPackage();
        AslBoolean.getInstance().setID("TYPE_Boolean");
        types.put("TYPE_Boolean", AslBoolean.getInstance());
        primitiveTypesPackage.addOwnedType("TYPE_Boolean"); 
        AslInteger.getInstance().setID("TYPE_Integer");
        types.put("TYPE_Integer", AslInteger.getInstance());
        primitiveTypesPackage.addOwnedType("TYPE_Integer");
        AslString.getInstance().setID("TYPE_String");
        types.put("TYPE_String", AslString.getInstance());
        primitiveTypesPackage.addOwnedType("TYPE_String");
        AslUnlimitedNatural.getInstance().setID("TYPE_UnlimitedNatural");
        types.put("TYPE_UnlimitedNatural", AslUnlimitedNatural.getInstance());
        primitiveTypesPackage.addOwnedType("TYPE_UnlimitedNatural");
        AslVoid.getInstance().setID("TYPE_Void");
        types.put("TYPE_Void", AslVoid.getInstance());
        primitiveTypesPackage.addOwnedType("TYPE_Void");
    }                
    
    protected static void registerType(UmlType type) {
        type.setID(createUniqueTypeID());        
        types.put(type.getID(), type);
    }    
    
    public static UmlType unregisterType(String id) {
        return types.remove(id);
    }
    
    public static Map<String,UmlType> getAllTypes() {
        return types;
    }
    
    public static UmlType getType(String id) {
        return types.get(id);
    }        
    
    public static UmlType getTypeByName(String name) {
        UmlType type = null;
        
        for (UmlType t: types.values()) {
            if (t.getName().equals(name)) {
                type = t;
                break;
            }
        }
        
        return type;
    }
                
    private static void resetIDGenerator() {
        uniqueTypeIDCount = 1;  
    }
    
    private static String createUniqueTypeID() {
        String typeKey = "TYPE_" + Integer.toString(uniqueTypeIDCount++);
        
        while (types.containsKey(typeKey)) {
            typeKey = "TYPE_" + Integer.toString(uniqueTypeIDCount++);
        }
        
        return typeKey;
    }
    
    private static int                  uniqueTypeIDCount = 1;
    
    private static Map<String,UmlType>  types = new HashMap<String,UmlType>();
    
    //*** INSTANCE BEGINS HERE ***********************************************   
    
    protected UmlType() {        
    }
    
    public UmlType(String name) {
        super(name);
    }
    
    public boolean isVoid() { return false; }
    
    public boolean isUserDefinedType() { return false; }     
    
    public boolean isInteger() { return false; }
    public boolean isBoolean() { return false; }
    public boolean isString() { return false; }
    public boolean isUnlimitedNatural() { return false; }
    
    public boolean isArray() { return false; }
    public boolean isList() { return false; }
    public boolean isMap() { return false; }    
    
    public String getID() {
        return this.ID;
    }
    public void setID(String ID) {
        this.ID = ID; 
    }
        
    public String getPackage() {
        return this.packageID;
    }    
    //only call this from UmlPackage.addType()/removeType()
    void setPackage(String packageID) {
        this.packageID = packageID;
        
        setChanged();
        notifyObservers();
    }
    
        
    private String                      ID;
    
    @XmlElement(name="package")
    private String                      packageID;
}
