package scaffold.codegen;


public class GenTemplates {
    //*** ACCESSOR GENERATOR TEMPLATES ****************************************
    
    public static final int NUM_ACCESSOR_GENERATORS = 8;
    
    public static final int SimpleGetterGT=0;
    public static final int SimpleSetterGT=1;
    public static final int BooleanGetterGT=2;
    public static final int BooleanSetterGT=3;
    public static final int ComplexGetterGT=4;
    public static final int ComplexSetterGT=5;
    public static final int ListGetterGT=6;
    public static final int MapGetterGT=7;
    
    public static final String[] AccessorGeneratorNames = {
        "Simple getter",
        "Simple setter",
        "Boolean getter",
        "Boolean setter",
        "Complex getter",
        "Complex setter",
        "List getter",
        "Map getter"
    };   
    
    public static final String[] AccessorGeneratorKeywords = {
        "SIMPLE_GETTER",
        "SIMPLE_SETTER",
        "BOOLEAN_GETTER",
        "BOOLEAN_SETTER",
        "COMPLEX_GETTER",
        "COMPLEX_SETTER",
        "LIST_GETTER",
        "MAP_GETTER"
    };

    public static final String[] DefaultAccessorTemplates = {
        "$type $getName() { return this.$name; }", //SIMPLE GETTER         
        "void $setName($type $name) { this.$name = $name; }", //SIMPLE SETTER           
        "$type $isName() { return this.$name; }", // or $type name() { return this.$name; } //BOOLEAN GETTER        
        "void setName($type $name) { this.$name = $name; }", //void name($type $name) { this.$name = $name; } //BOOLEAN SETTER
        "$type $getNames() { return this.$name; }", //COMPLEX GETTER        
        "void setNames($type $name) { this.$name = $name; }", //COMPLEX SETTER          
        "$basetype $getName(int index) { return this.$name[index]; }", //LIST GETTER          
        "$valuetype $getName($keytype key) { return this.$name.get(key); }", //MAP GETTER
    };

    //*** CORE GENERATOR TEMPLATES ********************************************
    
    public static final int NUM_CORE_GENERATORS = 11;
    
    public static final int LicenseGT=0;
    public static final int ImportPackageGT=1;
    public static final int ImportTypeGT=2;
    public static final int PackageGT=3;
    public static final int ClassSpecGT=4;
    public static final int ClassSupersGT = 5;
    public static final int ClassInterfacesGT = 6;
    public static final int AssociationGT=7;
    public static final int AttributeGT=8;
    public static final int MethodSignatureGT=9;
    public static final int MethodBodyGT=10;
    
    public static final String[] CoreGeneratorNames = {        
        "License",
        "Import a package",
        "Import a type",
        "Package",
        "Class specifier",
        "Class super classes",
        "Class interfaces",
        "Association",
        "Attribute",
        "Method signature",
        "Method body",
    };            
    
    public static final String[] DefaultCoreTemplates = {
        "//***\n//* This is my license text.\n//****",
        "import $package;",  //#include <$package>
        "import $package.$type;", //using namespace $type
        "package $package;", //namespace $package { $class }
        "$isAbstract $accessLevel $isConstant class $name", //CLASS SPEC
        "extends $superClass", //CLASS SUPERS
        "implements $interfaces", // CLASS INTERFACES
        "$accessLevel $isClassScope $isConstant $m-type $name;", //ASSOCIATION
        "$accessLevel $isClassScope $isConstant $type $name;", //ATTRIBUTE
        "$isAbstract $accessLevel $isClassScope $isConstant $returnType $name($parameters) $exceptions", //METHOD SIGNATURE
        " {\n$body}", //METHOD BODY
    };
            
    public static GenTemplates DefaultGenerators[] = new GenTemplates[NUM_CORE_GENERATORS];
       
    static {
        for (int i=0; i<NUM_CORE_GENERATORS; i++) {
            DefaultGenerators[i] = new GenTemplates("Default", i, DefaultCoreTemplates[i]);
        }
    }    
    
    //*** THE CLASS ***************************************************************************
    
    public GenTemplates(String name, int genType, String template) {
        this.name = name;
        this.genType = genType;
        this.template = template;
    }
    
    @Override
    public String toString() {
        return name;        
    }
    
    public String              name;
    public int                 genType;
    public String              template;   
}
