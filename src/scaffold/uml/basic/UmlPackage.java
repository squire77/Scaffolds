package scaffold.uml.basic;

import scaffold.uml.UmlIdentifiable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(namespace = "scaffold.uml.basic")

@XmlType(propOrder = { "ID", "nestingPackageID", "nestedPackageIDs", "ownedTypeIDs" })
public class UmlPackage extends UmlNamedElement implements UmlIdentifiable {   
    private static final String DEFAULT_PACKAGE_NAME = "default";
    private static final String PRIMITIVE_TYPES_PACKAGE_NAME = "infrastructure.core.primitivetypes";
    
    public static void initialize() {
        resetIDGenerator();
        packages.clear();
        create(PRIMITIVE_TYPES_PACKAGE_NAME);
        create(DEFAULT_PACKAGE_NAME);
    }
    
    public static UmlPackage create(String name) {
        if (!isPackageNameUndefined(name)) {
            return null; //not an exception. this is a possible user error case
        }
        
        UmlPackage packge = new UmlPackage(name);
        packge.setID(createUniquePackageID());            
        packages.put(packge.getID(), packge);        
        return packge;
    }
    
    public static boolean isPackageNameUndefined(String name) {
        for (UmlPackage p: packages.values()) {
            if (p.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }
        
    public static Map<String,UmlPackage> getAllPackages() {
        return packages;
    }
            
    public static UmlPackage removePackage(String id) { 
        UmlPackage pkg = getPackage(id);
        
        if (pkg == null) {
            return null;
        }        
        
        //remove pkg from nesting package
        UmlPackage nestingPkg = getPackage(pkg.getNestingPackage());
        if (nestingPkg != null) {
            nestingPkg.removeNestedPackage(pkg.getID());
        }
        
        //remove all nested packages
        List<String> nestedPkgs = new ArrayList<String>();        
        for (String nestedID: pkg.getNestedPackages()) {
            nestedPkgs.add(nestedID);
        }
        for (String nestedID: nestedPkgs) {
            pkg.removeNestedPackage(nestedID);
        }
        
        //remove all owned types
        List<String> ownedTypes = new ArrayList<String>();
        for (String ownedID: pkg.getOwnedTypes()) {
            ownedTypes.add(ownedID);
        }
        for (String ownedID: ownedTypes) {
            pkg.removeOwnedType(ownedID);
        }
        
        return packages.remove(id);
    }
    
    public static UmlPackage getPackage(String id) {
        return packages.get(id);
    }
    
    public static UmlPackage getPackageByName(String name) {
        UmlPackage packge = null;
        
        for (UmlPackage p: packages.values()) {
            if (p.getName().equals(name)) {
                packge = p;
                break;
            }
        }
        
        return packge;
    }    
            
    //for convenience
    public static UmlPackage getDefaultPackage() {
        return getPackageByName(UmlPackage.DEFAULT_PACKAGE_NAME);
    }
            
    //for convenience
    public static UmlPackage getPrimitiveTypesPackage() {
        return getPackageByName(UmlPackage.PRIMITIVE_TYPES_PACKAGE_NAME);
    }
        
    private static void resetIDGenerator() {
        uniquePackageIDCount = 1;  
    }
                
    private static String createUniquePackageID() {
        String packageKey = "PACKAGE_" + Integer.toString(uniquePackageIDCount++);
        
        while (packages.containsKey(packageKey)) {
            packageKey = "PACKAGE_" + Integer.toString(uniquePackageIDCount++);
        }
        
        return packageKey;
    }
    
    private static int uniquePackageIDCount = 1;   
    
    private static Map<String,UmlPackage> packages = new HashMap<String,UmlPackage>();
    
    //*** INSTANCE BEGINS HERE ***********************************************
    
    //for internalization only
    private UmlPackage() {                
    }
    
    public UmlPackage(String name) {        
        super(name);
    }
    
    public String getID() {
        return this.ID;
    }
    public void setID(String ID) {
        this.ID = ID; 
    }
    
    public final String getURI() {
        return this.URI;
    } 

    public String getFullPackageName() {
        String fullPackageName = getName();
        
        while (getNestingPackage() != null) {
            UmlPackage pack = getPackage(getNestingPackage());
            fullPackageName = pack.getName() + "." + fullPackageName;
        }
        
        return fullPackageName;
    }
        
    public String getNestingPackage() {
        return this.nestingPackageID;
    }    
    //Use addNestedPackage() to set nesting package
    void setNestingPackage(String outerPackID) {        
        this.nestingPackageID = outerPackID;
    }
    
    public List<String> getNestedPackages() {
        return this.nestedPackageIDs;
    }
    public boolean addNestedPackage(String innerPackID) {
        if (innerPackID == null) {
            return false;
        }
        
        UmlPackage inner = getPackage(innerPackID);
        
        if (inner == null) {
            return false;
        }
        
        //check if already added
        if (nestedPackageIDs.contains(innerPackID)) {
            return false;
        }
        
        boolean success = this.nestedPackageIDs.add(innerPackID);
        
        if (success) {            
            //remove package from its outer package            
            UmlPackage outer = getPackage(inner.getNestingPackage());
            if (outer != null) {
                outer.removeNestedPackage(innerPackID);
            }

            //set us as new outer package
            inner.setNestingPackage(this.getID());

            setChanged();
            notifyObservers();
        }
        
        return success;
    }
    public boolean removeNestedPackage(String innerPackID) {
        if (innerPackID == null) {
            return false;
        }
        
        boolean success = nestedPackageIDs.remove(innerPackID);
               
        if (success) {
            UmlPackage inner = getPackage(innerPackID);
            
            //nullify outer package
            inner.setNestingPackage(null);

            setChanged();
            notifyObservers();
        }
        
        return success;
    }     
    
    public List<String> getOwnedTypes() {
        return this.ownedTypeIDs;
    }
    public boolean addOwnedType(String typeID) {
        //check if already added
        if (ownedTypeIDs.contains(typeID)) {
            return false;
        }
                
        UmlType type = UmlType.getType(typeID);
        
        if (type == null) {
            return false;
        }
        
        boolean success = this.ownedTypeIDs.add(typeID);

        if (success) {                            
            //remove type from its current package
            UmlPackage current = getPackage(type.getPackage());
            if (current != null) {
                current.removeOwnedType(typeID);
            }

            //set package on the type
            type.setPackage(this.getID());

            setChanged();
            notifyObservers();
        }
        
        return success;
    }
    public boolean removeOwnedType(String typeID) {
        boolean success = ownedTypeIDs.remove(typeID);

        if (success) {            
            //nullify the package on the type
            UmlType.getType(typeID).setPackage(null);

            setChanged();
            notifyObservers();
        }
        
        return success;
    }                        
    
    @XmlTransient
    private String              URI;    
    
    //thse use a bean convention (get/set) and so, are XmlElements by default
    private String              ID;
    
    @XmlElement(name = "nestingPackage")
    private String              nestingPackageID;
    
    @XmlElementWrapper(name = "nestedPackages")
    @XmlElement(name = "packageID")
    private List<String>        nestedPackageIDs = new ArrayList<String>();    
    
    @XmlElementWrapper(name = "ownedTypes")
    @XmlElement(name = "typeID")
    private List<String>        ownedTypeIDs = new ArrayList<String>();
}
