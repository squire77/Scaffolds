package scaffold.uml.basic.cm;

import scaffold.uml.basic.UmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder = { "ordered", "unique", "expression" })
public class CmMultiplicityElement extends UmlElement {
    public CmMultiplicityElement() {
        this.isOrdered = false;
        this.isUnique = true;
        this.expr = "";
    }
    
    public boolean isOrdered() {
        return this.isOrdered;
    }
    public void setOrdered(boolean isOrdered) {
        this.isOrdered = isOrdered;
        
        setChanged();
        notifyObservers();        
    }
    
    public boolean isUnique() {
        return this.isUnique;
    }
    public void setUnique(boolean isUnique) {
        this.isUnique = isUnique;
        
        setChanged();
        notifyObservers();        
    }        
    
    public String getExpression() {
        return this.expr;
    }
        
    public void setExpression(String expr) {
        this.expr = expr;
        
        setChanged();
        notifyObservers();
    }  
    
    private boolean         isOrdered;
    private boolean         isUnique;        
    private String          expr; 
}
