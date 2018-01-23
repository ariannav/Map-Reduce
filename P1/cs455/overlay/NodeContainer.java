package cs455.overlay;

public class NodeContainer {

    private Thread thread;
    private int nodeID;
    private byte[] inetAddress;

    public NodeContainer(Thread thread, int nodeID, byte[] inetAddress){
        this.thread = thread;
        this.nodeID = nodeID;
        this.inetAddress = inetAddress;
    }

    public Thread getThread(){
        return thread;
    }

    public int getNodeID(){
        return nodeID;
    }

    public byte[] getInetAddress(){
        return inetAddress;
    }
}
