package scaffold.uml.msc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MscLifeLine implements Serializable {
    public MscLifeLine( MscObject object ) {
            this.object = object;
    }

    public MscActivation addActivation() {
        activations.add(new MscActivation( this ));
        return activations.get(activations.size() - 1);
    }

    public  MscObject               object;
    public  List<MscActivation>     activations = new ArrayList<MscActivation>();
}

