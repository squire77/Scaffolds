package test.scaffold;

import scaffold.uml.ClassModel;
import scaffold.uml.basic.UmlPackage;
import scaffold.uml.basic.UmlType;
import scaffold.uml.basic.cm.CmClass;
import scaffold.uml.basic.cm.CmOperation;
import scaffold.uml.basic.cm.CmParameter;
import scaffold.uml.basic.cm.CmProperty;
import scaffold.uml.basic.cm.CmAssociation;
import scaffold.uml.datatypes.AslString;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.lang.reflect.Method;
import java.util.List;


public class JAXBTestMain {
    @XmlRootElement(namespace = "test.scaffold")
    static class MyType extends UmlType { 
        public static MyType create(String name) {
            MyType myType = new MyType(name);
            UmlType.registerType(myType);
            UmlPackage.getDefaultPackage().addOwnedType(myType.getID());
            return myType;
        }
        public MyType() {            
        }
        private MyType(String name) {
            super(name);
        }
    }               
    
    public static void main(String[] args) throws NoSuchMethodException {
        ClassModel.initialize();   
        
        JAXBTestMain test = new JAXBTestMain();
        test.reflectiveTest("testJAXB_UmlPackage_1");   
        test.reflectiveTest("testJAXB_UmlType");        
        test.reflectiveTest("testJAXB_CmClass");
        test.reflectiveTest("testJAXB_UmlPackage_2");  
        test.reflectiveTest("testJAXB_CmOperation"); 
        test.reflectiveTest("testJAXB_CmProperty"); 
        test.reflectiveTest("testJAXB_CmAssociation");
    }    
    
    //package with nesting and nested package
    //UmlNamedElement: name, visibility
    //UmlPackage: ID, nestingPackage, nestedPackageIDs, ownedTypeIDs
    public boolean testJAXB_UmlPackage_1() throws JAXBException {
        pack1 = UmlPackage.create("pack1");
        UmlPackage.getDefaultPackage().addNestedPackage(pack1.getID());           
        pack2 = UmlPackage.create("pack2");
        pack1.addNestedPackage(pack2.getID());        
        writeXML("test/pack1.xml", UmlPackage.class, pack1);                 
        
        String ID = pack1.getID();
        
        UmlPackage test = (UmlPackage) readXML("test/pack1.xml", UmlPackage.class);  
                
        if ((!test.getName().equals("pack1")) || (!test.getID().equals(ID) ||
            (!UmlPackage.getPackage(test.getNestingPackage()).getName().equals("default"))) ||
            (!UmlPackage.getPackage(test.getNestedPackages().get(0)).getName().equals("pack2"))) {
            return false;
        }
        
        return true;
    }
    
    //package with owned MyType and owned UmlClass
    public boolean testJAXB_UmlPackage_2() throws JAXBException {       
        pack1.addOwnedType(myType1.getID());
        pack1.addOwnedType(myClass1.getID());
        writeXML("test/pack1-ownedTypes.xml", UmlPackage.class, pack1); 
        
        UmlPackage test = (UmlPackage) readXML("test/pack1-ownedTypes.xml", UmlPackage.class);  
        List<String> ownedTypeIDs = test.getOwnedTypes();
        UmlType type1 = UmlType.getType(ownedTypeIDs.get(0));
        UmlType type2 = UmlType.getType(ownedTypeIDs.get(1));
        
        if ((!type1.getName().equals("my-type") && !type1.getName().equals("MyClass")) ||
            (!type2.getName().equals("my-type") && !type2.getName().equals("MyClass")) ) {
            return false;
        }
        
        return true;
    }        
    
    //UmlNamedElement: name, visibility
    //UmlType: ID (key), package:ID
    public boolean testJAXB_UmlType() throws JAXBException {
        myType1 = MyType.create("my-type");        
        writeXML("test/my-type.xml", MyType.class, myType1);
        
        String ID = myType1.getID();
        String name = myType1.getName();
        int visibility = myType1.getVisibilityKind();
        String packageID = myType1.getPackage();
        
        MyType test = (MyType) readXML("test/my-type.xml", MyType.class);
        
        if ( (!test.getID().equals(ID)) ||
             (!test.getName().equals("my-type") || !test.getName().equals(name)) ||
             (test.getVisibilityKind() != 0 || test.getVisibilityKind() != visibility) ||
             (!packageID.equals(packageID))) {
            return false;
        }
        
        return true;
    }
    
    //MyClass implements MyInterface
    //UmlNamedElement: name, visibility
    //UmlType: ID (key), package:ID
    //CmClass: interface, abstract, finalSpecialization, superClasses, ownedOperations, ownedAttributes
    public boolean testJAXB_CmClass() throws JAXBException {
        myClass1 = CmClass.create("MyClass", false);
        myClass1.setAbstract(true);        
        myInterface1 = CmClass.create("MyInterface", true);
        myClass1.addSuperClass(myInterface1);
        writeXML("test/MyClass.xml", CmClass.class, myClass1);
        
        String ID = myClass1.getID();
        String name = myClass1.getName();
        int visibility = myClass1.getVisibilityKind();
        String packageID = myClass1.getPackage();
        boolean isInterface = myClass1.isInterface();
        boolean isAbstract = myClass1.isAbstract();
        boolean isFinal = myClass1.isFinalSpecialization();
        List<String> supers = myClass1.getSuperClassIDs();
        List<CmOperation> ops = myClass1.getOwnedOperations();
        List<CmProperty> props = myClass1.getOwnedAttributes();
        
        CmClass test = (CmClass) readXML("test/MyClass.xml", CmClass.class);
        
        if ( (!test.getID().equals(ID)) ||
             (!test.getName().equals("MyClass") || !test.getName().equals(name)) ||
             (test.getVisibilityKind() != 0 || test.getVisibilityKind() != visibility) ||
             (!packageID.equals(packageID)) ||
             (test.isInterface() != false) || (test.isAbstract() != true) || (test.isFinalSpecialization() != false) ||
             (test.isInterface() != isInterface) || (test.isAbstract() != isAbstract) || (test.isFinalSpecialization() != isFinal) ||
             (!CmClass.getClass(supers.get(0)).getName().equals("MyInterface")) ||
             (!CmClass.getClass(test.getSuperClassIDs().get(0)).getName().equals("MyInterface")) ||
             (!test.getOwnedOperations().isEmpty()) ||
             (!test.getOwnedAttributes().isEmpty())) {
            return false;
        }
        
        return true;
    }        
    
    //ownedOperations = { method1:CmOperation }
    //UmlNamedElement: name, visibility
    //UmlTypedElement: typeID
    //CmOperation: abstract, static, final, parameters, exceptions, body
    //CmParameter: name, visibility, typeID, direction, multiplicity?
    public boolean testJAXB_CmOperation() throws JAXBException {
        CmOperation method1 = myClass1.addOwnedOperation("method1");
        method1.setBody("this is the body");
        method1.setStatic(true);
        method1.addParameter("p1");
        CmParameter p2 = method1.addParameter("p2");
        p2.setTypeID(AslString.getInstance().getID());    
        p2.setDirection(2); //IN_OUT
        writeXML("test/MyClass-method1.xml", CmClass.class, myClass1);
        
        CmOperation op = myClass1.getOwnedOperations().get(0);
        String name = op.getName();
        int visibility = op.getVisibilityKind();
        String typeID = op.getTypeID();
        boolean isAbstract = op.isAbstract();
        boolean isStatic = op.isStatic();
        boolean isFinal = op.isFinalSpecialization();
        List<CmParameter> params = op.getParameters();
        List<UmlType> exceps = op.getRaisedExceptions();
        String body = op.getBody();
        
        CmClass testClass = (CmClass) readXML("test/MyClass-method1.xml", CmClass.class);
        CmOperation testOp = testClass.getOwnedOperations().get(0);
        
        if ((!testOp.getName().equals("method1") || !testOp.getName().equals(name)) ||
            (testOp.getVisibilityKind() != 0 || testOp.getVisibilityKind() != visibility) ||
            (!UmlType.getType(typeID).getName().equals("void")) ||
            (testOp.isAbstract() != false) || (testOp.isStatic() != true) || (testOp.isFinalSpecialization() != false) ||
            (testOp.isAbstract() != isAbstract) || (testOp.isStatic() != isStatic) || (testOp.isFinalSpecialization() != isFinal) ||
            (!testOp.getParameters().get(0).getName().equals("p1")) ||
            (!testOp.getParameters().get(1).getName().equals("p2")) ||
            (!testOp.getParameterByName("p1").getType().getName().equals("int")) ||
            (!testOp.getParameterByName("p2").getType().getName().equals("string")) ||
            (testOp.getParameterByName("p2").getDirection() != 2) ||
            (!testOp.getBody().equals("this is the body")) ) {
             return false;
        }
                
        return true;
    }
    
    //ownedAttributes = { attr1:CmProperty }
    //UmlNamedElement: name, visibility
    //UmlTypedElement: typeID
    //CmProperty: static, readOnly, composite, derived, isID, multiplicity?
    public boolean testJAXB_CmProperty() throws JAXBException {
        CmProperty attr1 = myClass1.addOwnedAttribute("attr1");
        attr1.setStatic(true);
        attr1.setReadOnly(true);
        attr1.setComposite(true);
        attr1.setDerived(true);
        attr1.setIsID(true);
        writeXML("test/MyClass-attr1.xml", CmClass.class, myClass1);
        return true;
    }
    
    //CmAssociation: ID, navigabilityEnabled, composite, aggregation, endPoint1, endPoint2
    public boolean testJAXB_CmAssociation() throws JAXBException {
        CmClass c1 = CmClass.create("C1", false);
        CmClass c2 = CmClass.create("C2", false);
        CmAssociation myAssoc1 = CmAssociation.create(true, true, false, c1, c2);
        writeXML("test/assoc1.xml", CmAssociation.class, myAssoc1);
        
        CmAssociation test = (CmAssociation) readXML("test/assoc1.xml", CmAssociation.class);
        return true;
    }

    //*** HELPERS ********************************************************
    
    private void reflectiveTest(String methodName) throws NoSuchMethodException {
        Method m = this.getClass().getDeclaredMethod(methodName, (Class[]) null);
        
        try {
            if ( (Boolean) m.invoke(this, (Object[]) null) ) {            
                System.out.println("Test " + m.getName() + " PASSED.");
            } else {
                System.out.println("Test " + m.getName() + " FAILED.");
            }
            
        } catch(Exception ex) {
            System.out.println("Test " + m.getName() + " failed.");
            ex.printStackTrace();
        }
    }    
        
    private void writeXML(String fn, Class clazz, Object instance) throws JAXBException {        
        JAXBContext classContext = JAXBContext.newInstance(clazz);
        Marshaller m = classContext.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);   
        
        File file = new File(fn);
        m.marshal(instance, file);
    }    
        
    private Object readXML(String fn, Class clazz) throws JAXBException {        
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller um = context.createUnmarshaller();                    
        
        File file = new File(fn);
        return um.unmarshal(file);
    }          
        
    private MyType myType1;
    private UmlPackage pack1;
    private UmlPackage pack2;
    private CmClass myClass1;
    private CmClass myInterface1;
}
