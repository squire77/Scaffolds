package scaffold.uml.msc;

import java.io.Serializable;
import java.util.List;


public class MscInvocation implements Serializable {
    public MscInvocation( String name, MscActivation activation, MscObject target ) {
        this.name = name;
        this.activation = activation;
	this.target = target;
    }

    public MscObject invoke( List args ) {
        return null;
    }

    @Override
    public String toString() {
        return activation.lifeLine.object.name + " invokes " + target.name + "." + name;
    }

    public  String                   name;
    public  MscActivation            activation;
    public  boolean                  repeat = false;
    public  String                   guardExpr = "";
    public  MscObject                target;
}
