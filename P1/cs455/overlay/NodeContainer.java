package cs455.overlay;

public class NodeContainer {

    private Thread thread;
    private int nodeID;
    private byte[] inetAddress;
    private int port;

    public NodeContainer(Thread thread, int nodeID, byte[] inetAddress, int port){
        this.thread = thread;
        this.nodeID = nodeID;
        this.inetAddress = inetAddress;
        this.port = port;
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

    public int getPort(){ return port; }

    public void setPort( int port ){ this.port = port; }
}
