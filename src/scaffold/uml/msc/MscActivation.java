package scaffold.uml.msc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MscActivation implements Serializable {
    public MscActivation( MscLifeLine lifeLine ) {
        this.lifeLine = lifeLine;
        this.symbolTable = new MscSymbolTable( this );
    }

    public MscInvocation addInvocation( String methodName, MscObject target ) {
        invocations.add(new MscInvocation( methodName, this, target ));
        return invocations.get(invocations.size() - 1);
    }

    public  MscLifeLine                 lifeLine;
    public  MscSymbolTable              symbolTable;
    public  List<MscInvocation>         invocations = new ArrayList<MscInvocation>();
}

