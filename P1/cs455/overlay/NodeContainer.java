package cs455.overlay;

import java.util.ArrayList;

public class NodeContainer{

    private int nodeID;
    private byte[] inetAddress;
    private int port;
    private String hostname;
    private ArrayList<NodeContainer> overlay;

    public NodeContainer(int nodeID, byte[] inetAddress, int port, String hostname){
        this.nodeID = nodeID;
        this.inetAddress = inetAddress;
        this.port = port;
        this.hostname = hostname;
    }

    public int getNodeID(){
        return nodeID;
    }

    public void setNodeID(int id){ nodeID = id; }

    public byte[] getIPAddress(){
        return inetAddress;
    }

    public int getPort(){ return port; }

    public String getHostname(){ return hostname; }

    public void setOverlay(ArrayList<NodeContainer> overlay){
        this.overlay = overlay;
    }

    public String getRoutingTableString() {
        String routingTable = "Distance|Node ID| Hostname\t\t\t\t| Port\n";
        for (int i = 0; i < overlay.size(); i++) {
            routingTable += Math.pow(2,i) + "\t\t| " + overlay.get(i).getNodeID()
                            + "\t| " + overlay.get(i).getHostname() + "| " + overlay.get(i).getPort() + "\n";
        }
        return routingTable;
    }

}
