package scaffold.uml.datatypes;

import scaffold.uml.basic.UmlPrimitiveType;
import javax.xml.bind.annotation.XmlRootElement;

   
@XmlRootElement(namespace = "scaffold.uml.datatype")
public class AslUnlimitedNatural extends UmlPrimitiveType {
    private AslUnlimitedNatural() {
        super("uint");
    }

    @Override
    public boolean isUnlimitedNatural() { return true; }
        
    public static AslUnlimitedNatural getInstance() {
        if (_instance == null) {
            _instance = new AslUnlimitedNatural();
        }
        
        return _instance;
    }

    private static AslUnlimitedNatural _instance;
}
