package scaffold.uml.datatypes;

import scaffold.uml.basic.UmlPrimitiveType;
import javax.xml.bind.annotation.XmlRootElement;

   
@XmlRootElement(namespace = "scaffold.uml.datatype")
public class AslInteger extends UmlPrimitiveType {
    private AslInteger() {
        super("int");
    }

    @Override
    public boolean isInteger() { return true; }
        
    public static AslInteger getInstance() {
        if (_instance == null) {
            _instance = new AslInteger();
        }
        
        return _instance;
    }

    private static AslInteger _instance;
}
