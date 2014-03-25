package scaffold.codegen;

import java.util.ArrayList;
import java.util.List;


public class Text {    
    public void clear() {
        lines.clear();
    }
    
    public void prefix(String pref) {
        for (int i=0; i<lines.size(); i++) {
            StringBuilder newLine = new StringBuilder();
            newLine.append(pref);            
            newLine.append(lines.get(i));
            lines.remove(i);
            lines.add(i, newLine.toString());            
        }
    }
    
    public void tab(int numTabs, int numSpacesPerTab) {
        tab(numTabs * numSpacesPerTab);
    }
    
    public void tab(int numSpaces) {
        for (int i=0; i<lines.size(); i++) {
            StringBuilder newLine = new StringBuilder();
            
            for (int j=0; j<numSpaces; j++) {
                newLine.append(' ');
            }
            
            newLine.append(lines.get(i));
            lines.remove(i);
            lines.add(i, newLine.toString());            
        }
    }
    
    //rules for replacing with an empty string:
    //  - if at beginning of line, if space after tag, remove it
    //  - if in middle of line, if space before and after tag, remove space after
    //  - if in middle of line, if space only before tag, do not remove space
    //  - if in middle of line, if space only after tag, do not remove space
    //  - if at end of line, if space before tag, remove it
    public void substitute(String tag, String replacement) {
        int index = 0;
        
        for (int i=0; i<lines.size(); i++) {
            String line = lines.get(i);
            int length = line.length();
            index = line.indexOf(tag, index);
            
            while (index > -1) {
                int endOfTag = index + tag.length();
                
                if (replacement.isEmpty()) {               
                    //handle repacing with an empty string
                    if (index == 0) {
                        if (length > endOfTag  && Character.isWhitespace(line.charAt(endOfTag))) {
                            //trim to remove leading whitespace
                            String newLine = line.substring(endOfTag).trim(); 
                            lines.remove(i);
                            lines.add(i, newLine);
                            
                            line = newLine;
                            index = line.indexOf(tag, endOfTag); 
                            continue;
                        }
                    }
                    if (index > 0) {
                        if (Character.isWhitespace(line.charAt(index-1))) {
                            if (length > endOfTag && Character.isWhitespace(line.charAt(endOfTag))) {
                                //trim after tag to remove leading whitespace
                                String newLine = line.substring(0, index) + line.substring(endOfTag).trim();
                                lines.remove(i);
                                lines.add(i, newLine);
                                
                                line = newLine;
                                index = line.indexOf(tag, endOfTag); 
                                continue;
                            }
                        }

                        if (endOfTag == length) {
                            if (Character.isWhitespace(line.charAt(index-1))) {
                                //trim any whitespace before the tag
                                String newLine = line.substring(0, index).trim();
                                lines.remove(i);
                                lines.add(i, newLine);

                                line = newLine;
                                index = line.indexOf(tag, endOfTag); 
                                continue;
                            }
                        }   
                    }                        
                }
                
                //do not trim to remove whitespace
                String newLine = line.substring(0, index) + replacement + line.substring(endOfTag);
                lines.remove(i);
                lines.add(i, newLine);
               
                line = newLine;
                index = line.indexOf(tag, endOfTag);                
            }
        }
    }
    
    public void addLine(String lineStr) {
        lines.add(lineStr);
    }
    public void addLines(String multipleLineStr) {
        // this version removes blank lines
        //List<String> moreLines = multipleLineStr.readLines()

        String[] moreLines = multipleLineStr.split( "\r\n|\n" );
        addLines(moreLines);
    }
    public void addLines(String[] moreLines) {
        for (String line: moreLines) {            
            addLine(line);
        }
    }
    public void addLines(List<String> moreLines) {
        lines.addAll(moreLines);
    }
    
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        
        int size = lines.size();
        
        if (size > 0) {
            for (int i=0; i<size-1; i++) {
                text.append(lines.get(i)).append('\n');
            }

            text.append(lines.get(size-1));
        }
        
        return text.toString();
    }

    protected List<String> lines = new ArrayList<String>();
}
