package scaffold.uml.basic;

import java.util.ArrayList;
import java.util.List;


public class UmlComment extends UmlElement {
    public String getBody() {
        return this.body;
    }    
    public void setBody(String body) {
        this.body = body;
    }
    
    public List<UmlElement> getAnnotatedElements() {
        return this.annotatedElements;
    }
    public boolean addAnnotatedElement(UmlElement element) {
        if (element == null) {
            return false;
        }
        
        boolean success = this.annotatedElements .add(element);
        
        if (success) {
            setChanged();
            notifyObservers();
        }
        
        return success;
    }
    public boolean removeAnnotatedElement(UmlElement element) {
        if (element == null) {
            return false;
        }
        
        boolean success = this.annotatedElements.remove(element);
        
        if (success) {
            setChanged();
            notifyObservers();
        }
        
        return success;
    }
    
    private String body;
    private List<UmlElement> annotatedElements = new ArrayList<UmlElement>();
}
