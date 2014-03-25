package scaffold.graphics.graph;

import scaffold.graphics.draggable.DraggableCanvas;
import scaffold.graphics.draggable.IDraggable;
import scaffold.console.ErrorDialog;
import scaffold.fileexplorer.FileUtility;
import java.awt.Graphics2D;
import java.io.IOException;


public class GraphCanvas extends DraggableCanvas {    
    public GraphCanvas() {
        this.graph = new Graph();
    }
    
    //must be called after calling constructor
    @Override
    public void init() {
        super.init();
    }           
    
    @Override
    public void reset() {
        super.reset();
        this.graph = new Graph();
    }
    
    public Node getNodeAtPosition(int x, int y) {
        // select objects from the top down in the z-buffer (in case they overlap)
        if(zBuffer.size() > 0) {
            for(int i=zBuffer.size() - 1; i >= 0; i--) {
                IDraggable next = zBuffer.get(i);
                if( next instanceof Node && ((Node) next).containsIgnoreEndPoints(x, y))
                    return (Node) next;
            }
        }

        return null;
    }
    
    @Override
    protected void doDrawObjects(Graphics2D g) {
        grid.draw(g);
        graph.snapToGrid(grid);
        graph.draw(g);
    }           
    
    @Override
    protected boolean isGroupSelectable(IDraggable d) {
        //add only nodes to the group
        if (d instanceof Node) {
            return true;
        }
        
        return false;
    }
    
    @Override
    protected void highlightNodeAndLinks(IDraggable d) {   
        super.highlightNodeAndLinks(d);
        
        for (EndPoint ep: ((Node) d).endPoints) {
            //make sure both nodes are in the group
            Node node = ep.link.getOtherEndPoint(ep).node;
            
            if (groupSelectionBounds.contains(node.getPolygon().xpoints[0], node.getPolygon().ypoints[0]) &&
                groupSelectionBounds.contains(node.getPolygon().xpoints[2], node.getPolygon().ypoints[2])) {
                ep.setColor(ep.getSelectedColor());
                ep.setBorderColor(ep.getSelectedBorderColor());
                ep.link.setColor(ep.link.getSelectedColor());
                ep.link.setBorderColor(ep.link.getSelectedBorderColor());
            }
        }
    }
    
    @Override
    protected void unhighlightNodeAndLinks(IDraggable d) { 
        super.unhighlightNodeAndLinks(d);
        
        for (EndPoint ep: ((Node) d).endPoints) {
            ep.setColor(ep.getUnselectedColor());
            ep.setBorderColor(ep.getUnselectedBorderColor());
            ep.link.setColor(ep.link.getUnselectedColor());
            ep.link.setBorderColor(ep.link.getUnselectedBorderColor());
        }
    }
    
    //*** methods for reading and writing the graph ********************  
      
    public void writeGraph(String graphFileName) {
        StringBuilder strBuf = new StringBuilder();        

        if (!graph.getNodes().isEmpty()) {
            for (Node n: graph.getNodes()) {                                                
                strBuf.append(n.getPolygon().xpoints[0]);
                strBuf.append(",");
                strBuf.append(n.getPolygon().ypoints[0]);            
                
                //let sub-class write data
                doWriteNodeData(n, strBuf);
                
                strBuf.append("\n");
            }
        } else {
            strBuf.append("<empty>");
        }
        
        strBuf.append(":\n");
                      
        if (!graph.getLinks().isEmpty()) {
            for (Link l: graph.getLinks()) {
                strBuf.append(getNodeIndex(l.getEndPoint1()));
                strBuf.append(",");
                strBuf.append(getNodeIndex(l.getEndPoint2()));
                strBuf.append(",");
                strBuf.append(l.getEndPoint1().getLocation());
                strBuf.append(",");
                strBuf.append(l.getEndPoint1().getPercent());    
                strBuf.append(",");
                strBuf.append(l.getEndPoint2().getLocation());
                strBuf.append(",");
                strBuf.append(l.getEndPoint2().getPercent()); 
                
                //let sub-class write data
                doWriteLinkData(l, strBuf);             
                
                strBuf.append("\n");
            }  
        } else {
            strBuf.append("<empty>");
        }
        
        try {
            FileUtility.writeFile(graphFileName, strBuf.toString());
        } catch (IOException ex) {
            ErrorDialog.error("Unable to write graph: " + graphFileName);
        }
    }
    
    public void readGraph(String graphFileName) {
        long graphStartTime = System.currentTimeMillis();
        
        this.reset(); // clear all for new graph()
        
        String graphDesc;
        try {
            graphDesc = FileUtility.readFile(graphFileName);
        } catch (IOException ex) {
            ErrorDialog.error("Unable to open graph: " + graphFileName);
            return;
        }

        //graphData[0] : Node descriptors
        //graphData[1] : Link descriptors
        String[] graphData = graphDesc.split(":");
        if (graphData.length < 2) {
            ErrorDialog.error("Invalid graph file: " + graphFileName);
            return;
        }
        graphData[0] = graphData[0].trim();
        graphData[1] = graphData[1].trim();
        
        String[] nodeData = graphData[0].split("\n");
        if (!nodeData[0].startsWith("<")) {
            for (int i=0; i<nodeData.length; i++) {
                String[] nodeElements = nodeData[i].split(",");
                doCreateNode(nodeElements);                                
            }
        }
        
        String[] linkData = graphData[1].split("\n"); 
        if (!linkData[0].startsWith("<")) {               
            for (int i=0; i<linkData.length; i++) {
                String[] linkElements = linkData[i].split(",");
                doCreateLink(linkElements);
            }        
        }
        
        long graphTotalTime = System.currentTimeMillis() - graphStartTime;        
        System.out.println("Time to load graphics: " + graphTotalTime/1000 + "." + graphTotalTime%1000);
        
        long drawStartTime = System.currentTimeMillis();
        
        //refresh after snapToGrid() occurs
        drawObjects();
        
        long drawTotalTime = System.currentTimeMillis() - drawStartTime;        
        System.out.println("Draw time: " + drawTotalTime/1000 + "." + drawTotalTime%1000);
    }
    
    
    protected void doWriteNodeData(Node n, StringBuilder strBuf) {
    }
    
    protected void doWriteLinkData(Link l, StringBuilder strBuf) {             
    }
    
    protected void doCreateNode(String[] nodeElements) {
    }
    
    protected void doCreateLink(String[] linkElements) {
    }      

    private int getNodeIndex(EndPoint ep) {
        for (int i=0; i<graph.getNodes().size(); i++) {
            if (ep.getNode() == graph.getNodes().get(i)) {
                return i;
            }
        }
        ErrorDialog.error("Unable to write graph, unknown node found");
        return -1;
    }
    
    protected Graph             graph;   
}
