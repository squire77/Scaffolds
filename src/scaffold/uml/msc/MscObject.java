package scaffold.uml.msc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class MscObject implements Serializable {
    public MscObject( String name ) {
        this.name = name;
        this.lifeLine = new MscLifeLine( this );
    }

    @Override
    public String toString() {
        //return name + attribs.flatten(' = ')*.prepend('\t').flatten('\n')
        //return name + "[ " + attribs.flatten('=').flatten(", ") + " ]";
        return "";
    }

    public  String                  name;
    public  MscLifeLine             lifeLine;
    public  Map<String, Object>     attribs = new HashMap<String,Object>();
}

