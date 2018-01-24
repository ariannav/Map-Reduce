package cs455.overlay;

public class NodeContainer {

    private int nodeID;
    private byte[] inetAddress;
    private int port;

    public NodeContainer(int nodeID, byte[] inetAddress, int port){
        this.nodeID = nodeID;
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public int getNodeID(){
        return nodeID;
    }

    public void setNodeID(int id){ nodeID = id; }

    public byte[] getIPAddress(){
        return inetAddress;
    }

    public int getPort(){ return port; }

}
