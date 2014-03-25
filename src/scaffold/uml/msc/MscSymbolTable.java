package scaffold.uml.msc;

import java.io.Serializable;


public class MscSymbolTable implements Serializable {
    public MscSymbolTable( MscActivation activation ) {
        this.activation = activation;
    }

    public  MscActivation               activation;
}


