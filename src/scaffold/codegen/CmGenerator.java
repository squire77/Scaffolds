package scaffold.codegen;

import scaffold.uml.datatypes.AslInteger;
import scaffold.uml.datatypes.AslString;
import scaffold.uml.datatypes.AslVoid;
import scaffold.uml.datatypes.AslArray;
import scaffold.uml.datatypes.AslList;
import scaffold.uml.datatypes.AslMap;
import scaffold.uml.basic.UmlNamedElement;
import scaffold.uml.basic.UmlType;
import scaffold.uml.basic.UmlPackage;
import scaffold.uml.basic.cm.CmClass;
import scaffold.uml.basic.cm.CmProperty;
import scaffold.uml.basic.cm.CmAssocEnd;
import scaffold.uml.basic.cm.CmOperation;
import scaffold.uml.basic.cm.CmParameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.LineNumberReader;
import java.io.StringReader;


/*
 * This generator defines a language for the user to create custom fragment scripts.
 * 
 * The user's scripts would be labeled and referenced from a class template.
 * 
 * If any of the corresponding types are encountered, they will be generated at the corresponding location in the template.
 * 
 * ACCESSORS are not generated unless appropriate scripts are explicitly referenced or DEFAULT_ACCESSORS is set.
 * 
 * Here is a list of accessor script types and examples:
 * 
 *      SIMPLE ACCESSOR         $type $getName() { return this.$name; }
 *      SIMPLE SETTER           void setName($type $name) { $this.name = $name; }
 *      BOOLEAN ACCESSOR        $type $isName() { return this.$name; } or $type name() { return this.$name; }
 *      BOOLEAN SETTER          void setName($type $name) { this.$name = $name; } or void name($type $name) { this.$name = $name; }
 *      COMPLEX ACCESSOR        $type $getNames() { return this.$name; }
 *      COMPLEX SETTER          void setNames($type $name) { this.$name = $name; }
 *      LIST ACCESSOR           $basetype $getName(int index) { return this.$name[index]; }
 *      MAP ACCESSOR            $valuetype $getName($keytype key) { return this.$name.get($key); }
 * 
 * Here is a list of other script types and examples:
 * 
 *      LICENSE                 * this is my license text *
 *      IMPORT_PACKAGE          import $package or #include <$package>
 *      IMPORT_TYPE             import $package.$type or using namespace $type
 *      NAMESPACE               package $package or namespace $package { $class }
 *      CREATION_IN_METHOD      $name = new $type($param-1, $param-3) or $name = $type.create()
 *      ASSOCIATION             $accessLevel $isClassScope $isConstant $m-type $name
 *      ATTRIBUTE               $accessLevel $isClassScope $isConstant $type $name
 *      METHOD SIGNATURE        $isAbstract $accessLevel $isClassScope $isConstant $returnType $name($parameters) throws $exceptions
 *      METHOD BODY             {\n$body\n}
 * 
 */
public class CmGenerator {
    private Map<String,String> typeMap;
    private Map<String,String> containerMap;
    private Map<String,String> keywordMap;
    private Map<String,String> visibilityMap;

    public CmGenerator() {
        this.typeMap = new HashMap<String,String>();
        this.typeMap.put("VOID", "void");
        this.typeMap.put("STRING", "String");
        this.typeMap.put("INT", "int");
        
        this.containerMap = new HashMap<String,String>();
        this.containerMap.put("MAP_IMPORT", "java.util.Map");
        this.containerMap.put("MAP", "Map");
        this.containerMap.put("MAP_IMPL", "HashMap");
        this.containerMap.put("LIST_IMPORT", "java.util.List");
        this.containerMap.put("LIST", "List");
        this.containerMap.put("LIST_IMPL", "ArrayList");
        
        this.keywordMap = new HashMap<String,String>();
        this.keywordMap.put("ABSTRACT", "abstract");
        this.keywordMap.put("CONSTANT", "final");
        this.keywordMap.put("CLASS_SCOPE", "static");
        
        this.visibilityMap = new HashMap<String,String>();
        this.visibilityMap.put("PUBLIC", "public");
        this.visibilityMap.put("PROTECTED", "protected");
        this.visibilityMap.put("PRIVATE", "private");
    }       
    
    public String generateClass(CmClass clazz, String spec) throws Exception {
        StringBuilder strb = new StringBuilder();                                       
        LineNumberReader reader = new LineNumberReader(new StringReader(spec));        
        String line;
        boolean skipLine;
        
        //default unknown parameter types to "int"
        for (CmOperation operation: clazz.getOwnedOperations()) {
            for (CmParameter p: operation.getParameters()) {
                if (p.getType() == null) {
                    p.setTypeID(AslInteger.getInstance().getID());
                }
            }
        }       
        
        //default unknown attribute types to "int"
        for (CmProperty attr: clazz.getOwnedAttributes()) {
            if (attr.getType() == null) {
                attr.setTypeID(AslInteger.getInstance().getID());
            }
        }        
        
        while((line = reader.readLine()) != null) {     
            skipLine = false;
            
            if (line.contains("LICENSE")) {
                String license = GenTemplates.DefaultCoreTemplates[GenTemplates.LicenseGT];
                if (!spec.isEmpty()) {
                    strb.append(license);
                    strb.append("\n");
                }
                skipLine = true;
            }
            
            if (line.contains("PACKAGE")) {
                if (clazz.getPackage() != null) {
                    UmlPackage packge = UmlPackage.getPackage(clazz.getPackage());
                    Text substStr = new Text();
                    substStr.addLines(GenTemplates.DefaultCoreTemplates[GenTemplates.PackageGT]);              
                    substStr.substitute("$package", packge.getName());
                    strb.append(substStr.toString()); 
                    strb.append("\n");
                }
                skipLine = true;
            }

            if (line.contains("IMPORT")) {
                skipLine = true;
            }
            
            if (line.contains("CLASS")) {
                Text newLine = new Text();
                newLine.addLines(line);
                String clazzSpec = generateClassSpec(clazz,
                                        GenTemplates.DefaultCoreTemplates[GenTemplates.ClassSpecGT]);
                newLine.substitute("CLASS", clazzSpec);
                                
                if (!clazz.getSuperClassesThatAreNotInterfaces().isEmpty()) {
                    Text supers = new Text();
                    supers.addLines(GenTemplates.DefaultCoreTemplates[GenTemplates.ClassSupersGT]);
                    supers.substitute("$superClass", clazz.getSuperClassesThatAreNotInterfaces().get(0).getName());
                        
                    newLine.substitute("SUPERS", supers.toString());
                } else {
                    newLine.substitute("SUPERS", "");
                }
                
                if (!clazz.getSuperClassesThatAreInterfaces().isEmpty()) {                
                    Text interfaces = new Text();
                    interfaces.addLines(GenTemplates.DefaultCoreTemplates[GenTemplates.ClassInterfacesGT]);
                    interfaces.substitute("$interfaces", buildInterfaceListString(clazz));
                
                    newLine.substitute("INTERFACES", interfaces.toString());
                } else {
                    newLine.substitute("INTERFACES", "");
                }
                
                strb.append(newLine);
                strb.append("\n");
                skipLine = true;
            }
            
            if (line.contains("METHODS")) {
                int position = line.indexOf("METHODS");
                Text methods = new Text();
                methods.addLines(generateAllMethods(clazz, 
                                        GenTemplates.DefaultCoreTemplates[GenTemplates.MethodSignatureGT],
                                        GenTemplates.DefaultCoreTemplates[GenTemplates.MethodBodyGT])); 
                methods.tab(position);
                
                if (!methods.toString().trim().isEmpty()) { //this also gets rid of extraneous '\n'                    
                    strb.append(methods.toString());
                    strb.append("\n");
                }
                skipLine = true;
            }       
            
            if (line.contains("ASSOCIATION")) {
                int position = line.indexOf("ASSOCIATION");
                Text assocs = new Text();
                assocs.addLines(generateAllAssociations(clazz, 
                                        GenTemplates.DefaultCoreTemplates[GenTemplates.AssociationGT]));
                assocs.tab(position);
                
                if (!assocs.toString().trim().isEmpty()) { //this also gets rid of extraneous '\n'  
                    strb.append(assocs.toString());
                    strb.append("\n");
                }
                skipLine = true;
            }
            
            if (line.contains("ATTRIBUTE")) {
                int position = line.indexOf("ATTRIBUTE");
                Text attribs = new Text();
                attribs.addLines(generateAllAttributes(clazz, 
                                        GenTemplates.DefaultCoreTemplates[GenTemplates.AttributeGT]));
                attribs.tab(position);
                
                if (!attribs.toString().trim().isEmpty()) { //this also gets rid of extraneous '\n'  
                    strb.append(attribs.toString());
                    strb.append("\n");
                }
                skipLine = true;
            }
            
            //*** ACCESSORS **********************************                 
            
            if (line.contains("COMPLEX_GETTER")) {
                skipLine = true;
            }
            if (line.contains("COMPLEX_SETTER")) {
                skipLine = true;
            }
            if (line.contains("LIST_GETTER")) {
                skipLine = true;
            }
            if (line.contains("MAP_GETTER")) {
                skipLine = true;
            }
            
            if (line.contains("SIMPLE_GETTER")) {
                StringBuilder allAccessors = new StringBuilder();
                Text accessor = new Text();

                for (CmProperty attrib: clazz.getOwnedAttributes()) {  
                    if (attrib.getType().isBoolean()) {
                        continue;
                    }
                    int position = line.indexOf("SIMPLE_GETTER");

                    accessor.clear();
                    accessor.addLines(GenTemplates.DefaultAccessorTemplates[GenTemplates.SimpleGetterGT]);
                    substituteAccessorElements(attrib.getName(), attrib.getType().getName(), accessor);
                    accessor.tab(position);

                    allAccessors.append(accessor.toString());
                    allAccessors.append("\n");
                }

                String code = allAccessors.toString();
                if (!code.trim().isEmpty()) {
                    strb.append(code);
                    strb.append("\n");
                }

                skipLine = true;
            }
            
            if (line.contains("SIMPLE_SETTER")) {
                StringBuilder allAccessors = new StringBuilder();
                Text accessor = new Text();

                for (CmProperty attrib: clazz.getOwnedAttributes()) {  
                    if (attrib.getType().isBoolean()) {
                        continue;
                    }
                    int position = line.indexOf("SIMPLE_SETTER");

                    accessor.clear();
                    accessor.addLines(GenTemplates.DefaultAccessorTemplates[GenTemplates.SimpleSetterGT]);
                    substituteAccessorElements(attrib.getName(), attrib.getType().getName(), accessor);
                    accessor.tab(position);

                    allAccessors.append(accessor.toString());
                    allAccessors.append("\n");
                }

                String code = allAccessors.toString();
                if (!code.trim().isEmpty()) {
                    strb.append(code);
                    strb.append("\n");
                }

                skipLine = true;
            }
            
            if (line.contains("BOOLEAN_GETTER")) {
                StringBuilder allAccessors = new StringBuilder();
                Text accessor = new Text();

                for (CmProperty attrib: clazz.getOwnedAttributes()) {  
                    if (!attrib.getType().isBoolean()) {
                        continue;
                    }
                    int position = line.indexOf("BOOLEAN_GETTER");

                    accessor.clear();
                    accessor.addLines(GenTemplates.DefaultAccessorTemplates[GenTemplates.BooleanGetterGT]);
                    substituteAccessorElements(attrib.getName(), attrib.getType().getName(), accessor);
                    accessor.tab(position);

                    allAccessors.append(accessor.toString());
                    allAccessors.append("\n");
                }

                String code = allAccessors.toString();
                if (!code.trim().isEmpty()) {
                    strb.append(code);
                    strb.append("\n");
                }

                skipLine = true;
            }
            
            if (line.contains("BOOLEAN_SETTER")) {
                StringBuilder allAccessors = new StringBuilder();
                Text accessor = new Text();

                for (CmProperty attrib: clazz.getOwnedAttributes()) {  
                    if (!attrib.getType().isBoolean()) {
                        continue;
                    }
                    int position = line.indexOf("BOOLEAN_SETTER");

                    accessor.clear();
                    accessor.addLines(GenTemplates.DefaultAccessorTemplates[GenTemplates.BooleanSetterGT]);
                    substituteAccessorElements(attrib.getName(), attrib.getType().getName(), accessor);
                    accessor.tab(position);

                    allAccessors.append(accessor.toString());
                    allAccessors.append("\n");
                }

                String code = allAccessors.toString();
                if (!code.trim().isEmpty()) {
                    strb.append(code);
                    strb.append("\n");
                }

                skipLine = true;
            }            
            
            if (!skipLine) {
                strb.append(line).append("\n");
            }
        }
                    
        return strb.toString();
    }
    
    public String generateClassSpec(CmClass clazz, String spec) {
        Text substStr = new Text();
        substStr.addLines(spec);
        
        String subst = (clazz.isAbstract()) ? keywordMap.get("ABSTRACT") : "";
        substStr.substitute("$isAbstract", subst);
        
        subst = (clazz.isFinalSpecialization()) ? keywordMap.get("CONSTANT") : "";
        substStr.substitute("$isConstant", subst); 
                   
        substStr.substitute("$accessLevel", buildVisibility(clazz));        
        substStr.substitute("$name", clazz.getName());
        
        return substStr.toString();
    }
    
    //*** GENERATE METHODS ******************************************************
    
    public String generateAllMethods(CmClass clazz, String operationSpec, String methodSpec) {
        StringBuilder methods = new StringBuilder();

        for (int i=0; i<clazz.getOwnedOperations().size(); i++) {            
            methods.append(generateMethodDefinition(clazz.getOwnedOperations().get(i), 
                                                    operationSpec, methodSpec));
            methods.append("\n");
        }
         
        return methods.toString();
    }
    
    public String generateMethodDeclaration(CmOperation operation, String spec) {
        Text substStr = new Text();
        substStr.addLines(spec);
        
        String subst = (operation.isAbstract()) ? keywordMap.get("ABSTRACT") : "";
        substStr.substitute("$isAbstract", subst);
        
        subst = (operation.isStatic()) ? keywordMap.get("CLASS_SCOPE") : "";
        substStr.substitute("$isClassScope", subst); 
        
        subst = (operation.isFinalSpecialization()) ? keywordMap.get("CONSTANT") : "";
        substStr.substitute("$isConstant", subst); 
                   
        substStr.substitute("$accessLevel", buildVisibility(operation));        
        substStr.substitute("$returnType", buildTypeString(operation.getType()));
        substStr.substitute("$name", operation.getName());
        substStr.substitute("$parameters", buildParameterListString(operation.getParameters()));
        
        String exceptionsList = buildExceptionListString(operation.getRaisedExceptions());
        if (!exceptionsList.isEmpty()) {            
            substStr.substitute("$exceptions", "throws " + exceptionsList); 
        } else {
            substStr.substitute("$exceptions", "");
        }               
            
        return substStr.toString();
    }

    public String generateMethodDefinition(CmOperation operation, String operationSpec, String methodBodySpec) {
        Text substStr = new Text();
        substStr.addLines(methodBodySpec);
        
        if (operation.getBody() != null && !operation.getBody().equals("")) {
            substStr.substitute("$body", operation.getBody() + "\n");
        } else {
            substStr.substitute("$body", "");
        }
        
        String methodDecl = generateMethodDeclaration(operation, operationSpec);
            
        return methodDecl + substStr.toString();       
    }
    
    //*** GENERATE ASSOCIATIONS **************************************************
    
    public String generateAllAssociations(CmClass clazz, String spec) throws Exception {
        StringBuilder assocStr = new StringBuilder();
        
        List<CmAssocEnd> assocs = clazz.getNavigableAssociates();
        
        for (CmAssocEnd assoc: assocs) {            
            assocStr.append(generateAssociation(assoc, spec));
            assocStr.append("\n");
        }
         
        return assocStr.toString();
    }
        
    public String generateAssociation(CmAssocEnd assoc, String spec) throws Exception {
        Text substStr = new Text();
        substStr.addLines(spec);
        
        String subst = (assoc.isStatic()) ? keywordMap.get("CLASS_SCOPE") : "";
        substStr.substitute("$isClassScope", subst); 
                  
        subst = (assoc.isReadOnly()) ? keywordMap.get("CONSTANT") : "";
        substStr.substitute("$isConstant", subst);
        
        substStr.substitute("$accessLevel", buildVisibility(assoc)); 
        
        if (assoc.getName() != null && assoc.getName().isEmpty()) {
            String newName = "a" + assoc.getClazz().getName();
            substStr.substitute("$name", newName);
        } else {
            substStr.substitute("$name", assoc.getName());
        }
        
        if (assoc.getMultiplicity().getExpression().contains("*")) {
            AslList container = AslList.create(assoc.getClazz());
            substStr.substitute("$m-type", buildTypeString(container));
        } else {
            substStr.substitute("$m-type", buildTypeString(assoc.getClazz()));      
        }
        
        return substStr.toString();                  
    }
   
    //*** GENERATE ATTRIBUTES ****************************************************
    
    public String generateAllAttributes(CmClass clazz, String spec) throws Exception {
        StringBuilder attribs = new StringBuilder();
        
        for (int i=0; i<clazz.getOwnedAttributes().size(); i++) {            
            attribs.append(generateAttribute((CmProperty) clazz.getOwnedAttributes().get(i), spec));
            attribs.append("\n");
        }
         
        return attribs.toString();
    }
        
    public String generateAttribute(CmProperty attribute, String spec) throws Exception {
        Text substStr = new Text();
        substStr.addLines(spec);
        
        String subst = (attribute.isStatic()) ? keywordMap.get("CLASS_SCOPE") : "";
        substStr.substitute("$isClassScope", subst); 
                  
        subst = (attribute.isReadOnly()) ? keywordMap.get("CONSTANT") : "";
        substStr.substitute("$isConstant", subst);
        
        substStr.substitute("$accessLevel", buildVisibility(attribute));        
        substStr.substitute("$type", buildTypeString(attribute.getType()));
        substStr.substitute("$name", attribute.getName());
        
        return substStr.toString();            
    }

    //*** HELPERS ********************************
    
    private void substituteAccessorElements(String name, String type, Text text) {
        text.substitute("$name", name);
        text.substitute("$type", type);                        
        StringBuilder upperFirst = new StringBuilder();
        upperFirst.append(name.substring(0,1).toUpperCase());
        upperFirst.append(name.substring(1));                        
        text.substitute("$getName", "get" + upperFirst.toString());
        text.substitute("$setName", "set" + upperFirst.toString());
    }
   
    private String buildVisibility(UmlNamedElement element) {
        StringBuilder strb = new StringBuilder();
        
        switch (element.getVisibilityKind()) {
            case 0:
                strb.append(visibilityMap.get("PUBLIC"));
                break;
            case 1:
                strb.append(visibilityMap.get("PRIVATE"));
                break;
            case 2:
                strb.append(visibilityMap.get("PROTECTED"));
                break;
            default:
                //append nothing
        }
        
        return strb.toString();
    }    
    
    private String buildTypeString(UmlType type) {
        StringBuilder typeStr = new StringBuilder();
        
        if (type instanceof AslMap) {
            AslMap map = (AslMap) type;
            typeStr.append(containerMap.get("MAP"));
            typeStr.append("<").append(map.getBaseType().getName()).append(">");
        } else { 
            if (type instanceof AslList) {
                AslList list = (AslList) type;
                typeStr.append(containerMap.get("LIST"));
                typeStr.append("<").append(list.getBaseType().getName()).append(">");
            } else {
                if (type instanceof AslArray) {
                    AslArray arr = (AslArray) type;
                    typeStr.append(arr.getBaseType().getName());
                    typeStr.append("[]");
                } else {
                    if (type instanceof AslVoid) {
                        typeStr.append(typeMap.get("VOID"));
                    } else {
                        if (type instanceof AslString) {
                           typeStr.append(typeMap.get("STRING")); 
                        } else {
                            if (type instanceof AslInteger) {
                                typeStr.append(typeMap.get("INT"));
                            } else {
                                typeStr.append(type.getName());
                            }
                        }
                    }
                        
                }
            }
        }
        
        return typeStr.toString();
    }
    
    private String buildInterfaceListString(CmClass clazz) {
        StringBuilder interfacesStr = new StringBuilder();
        
        boolean first = true;
        
        for (CmClass intf: clazz.getSuperClassesThatAreInterfaces()) {
            if (!first) {
                interfacesStr.append(", ");
            } else {
                first = false;
            }
            
            interfacesStr.append(intf.getName());
        }
        
        return interfacesStr.toString();
    }
    
    private String buildParameterListString(List<CmParameter> parameters) {
        StringBuilder parametersStr = new StringBuilder();
        
        boolean first = true;
        
        for (CmParameter p: parameters) {
            if (!first) {
                parametersStr.append(", ");
            } else {
                first = false;
            }
            
            parametersStr.append(buildTypeString(p.getType()));
            parametersStr.append(" ");
            parametersStr.append(p.getName());
        }
        
        return parametersStr.toString();
     }
          
    public String buildExceptionListString(List<UmlType> exceptions) {
        StringBuilder exceptionsStr = new StringBuilder();

        boolean first = true;

        for (UmlType e: exceptions) {
            if (!first) {
                exceptionsStr.append(", ");
            } else {
                first = false;
            }

            exceptionsStr.append(e.getName());
        }                

        return exceptionsStr.toString();
     }         
}
