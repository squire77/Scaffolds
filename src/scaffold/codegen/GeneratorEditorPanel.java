package scaffold.codegen;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;


public class GeneratorEditorPanel extends JPanel {
    public GeneratorEditorPanel(Border border) {
        super(new BorderLayout());        
        this.setBorder(border);
        
        this.genEditor = new JEditorPane();
        this.genEditor.setEditable(false);
        this.genEditor.setContentType("text/plain");
        //this.genEditor.setContentType("text/html");
        //this.genEditor.addHyperlinkListener(
        //        new HyperlinkListener() {
        //            @Override
        //            public void hyperlinkUpdate(HyperlinkEvent event) {
        //                if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
        //                    GeneratorEditorPanel.this.collectPropertyValue(event.getDescription());
        //            }
        //        }
        //);

        this.add(new JLabel("Generator Template"), BorderLayout.NORTH);
        this.add(new JScrollPane(genEditor), BorderLayout.CENTER);
    }
    
    public void setGenerator(int generatorType, int generatorIndex) {
        if (generatorType < 0 || generatorIndex < 0) {
            // clear the editor
            genEditor.setText("");
        }
        
        //show default generator for value 0
        if (generatorIndex == 0) {
            genEditor.setText(GenTemplates.DefaultGenerators[generatorType].template);
        }
        
    }
    public void collectPropertyValue(String propertyName) {
        /*
        Action action = (Action) actionList.getSelectedValue();
        if (action == null) {
            scaffold.console.ErrorDialog.error("Why is there a description when there is no action selected?");
            return;
        }

        //Expression Properties are designated by a trailing '@'
        int markerIndex = propertyName.indexOf('@');

        //collect data for the selected property
        if (markerIndex != -1) { //this is an Expression Property
            //parse out the expression index and property name
            int expressionIndex = new Integer(propertyName.substring(markerIndex+1));
            propertyName = propertyName.substring(0, markerIndex+1);

            //lookup the property and collect the value
            Expression expression = action.getExpression(expressionIndex);
            Property property = expression.getProperty(propertyName);
            if (property == null) {
                scaffold.console.ErrorDialog.error("Can't find property: " + propertyName);
                return;
            }
            property.collectValue();

        } else { //this is an Action Property
            Property property = action.getProperty(propertyName);
            property.collectValue();
        }

        updateRuleDescriptionAndActions();
        * */
    }    
    
    private JEditorPane             genEditor;
}
