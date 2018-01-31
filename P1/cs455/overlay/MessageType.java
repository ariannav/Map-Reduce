package cs455.overlay;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class MessageType {

    private byte[] ip;
    private int port;
    private byte lastType;
    private byte[] infoString;
    private int nodeID;
    private DataInputStream incoming;
    private NodeContainer[] overlay;
    private int[] nodeIDs;
    private int numMessages;

    public MessageType(DataInputStream incoming){
            this.incoming = incoming;
            lastType = 0;
    }

    //====================================RECEIVED BY REGISTRY NODE=====================================================

    public void processType2() throws IOException{ //OVERLAY_NODE_SENDS_REGISTRATION
        //Get entire message
        int  dataLength = incoming.readInt();
        byte[] data = new byte[dataLength];
        incoming.readFully(data, 0, dataLength);

        //Process
        ByteBuffer message = ByteBuffer.wrap(data);
        lastType = message.get();
        if(lastType != 2){
            throw new IOException("Incorrect message type received: " + lastType);
        }

        byte ipLength = message.get();
        ip = new byte[ipLength];
        message.get(ip, 0, ipLength);

        //Get port number
        port = message.getInt();
    }


    public void processVariableFromMessenger() throws IOException{
        //Get entire message
        int  dataLength = incoming.readInt();
        byte[] data = new byte[dataLength];
        incoming.readFully(data, 0, dataLength);

        lastType = data[0];

        switch(lastType){
            case 4:
                processType4(data);
                break;
            case 7:
                processType7(data);
                break;
            case 10:
                //processType10(data);
                break;
            case 12:
                //processType12(data);
                break;
            default:
                throw new IOException("Incorrect type number. Type received: " + lastType);
        }
    }


    public void processType4(byte[] data){  //OVERLAY_NODE_SENDS_DEREGISTRATION
        ByteBuffer message = ByteBuffer.wrap(data);
        lastType = message.get();
        byte length = message.get();

        //Put IP in array
        ip = new byte[length];
        message.get(ip, 0, length);

        //Port number
        port = message.getInt();

        //Port NodeID
        nodeID = message.getInt();

    }


    private void processType7(byte[] data) throws IOException{  //NODE_REPORTS_OVERLAY_SETUP_STATUS
        ByteBuffer message = ByteBuffer.wrap(data);
        //Get node ID
        lastType = message.get();
        nodeID = message.getInt();

        byte length = message.get();
        infoString = new byte[length];
        message.get(infoString, 0, length);

        if(nodeID == -1 ) {
            throw new IOException("Node was unable to setup overlay. " + getInfoString());
        }
    }

    /*
    private void processType10(byte[] data) throws IOException{  //TODO: OVERLAY_NODE_REPORTS_TASK_FINISHED
        try{
            //Get IP length and read IP into variable ip.
            int ipLength = message.read();
            ip = new char[ipLength];
            message.read(ip);

            //Get port number
            port = message.read();
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 2.");
        }
    }


    private void processType12(byte[] data) throws IOException{  //TODO: OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY
        try{
            //Get IP length and read IP into variable ip.
            int ipLength = message.read();
            ip = new char[ipLength];
            message.read(ip);

            //Get port number
            port = message.read();
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 2.");
        }
    }
    */

    //=============================RECEIVED BY THE MESSENGER NODE=======================================================

    public void processType3() throws IOException{ //REGISTRY_REPORTS_REGISTRATION_STATUS
        //Get entire message
        int  dataLength = incoming.readInt();
        byte[] data = new byte[dataLength];
        incoming.readFully(data, 0, dataLength);

        //Process
        ByteBuffer message = ByteBuffer.wrap(data);
        lastType = message.get();
        nodeID = message.getInt();

        //Get Info String length and Info String
        byte infoStringLength = message.get();
        infoString = new byte[infoStringLength];
        message.get(infoString, 0, infoStringLength);

        if(nodeID == -1){
            throw new IOException("Message Node was not successfully registered. NodeID -1. " + getInfoString());
        }
    }


    public void processVariableFromRegistry() throws IOException{
        //Get entire message
        int  dataLength = incoming.readInt();
        byte[] data = new byte[dataLength];
        incoming.readFully(data, 0, dataLength);

        lastType = data[0];

        switch(lastType){
            case 5:
                processType5(data);
                break;
            case 6:
                processType6(data);
                break;
            case 8:
                processType8(data);
                break;
            case 11:
                //processType11(data);
                break;
            default:
                throw new IOException("Incorrect type number. Type received: " + lastType);
        }
    }


    public void processType5(byte[] data){  //REGISTRY_REPORTS_DEREGISTRATION_STATUS
        ByteBuffer message = ByteBuffer.wrap(data);
        lastType = message.get();
        nodeID = message.getInt();

        //Get Info String length and Info String
        byte infoStringLength = message.get();
        infoString = new byte[infoStringLength];
        message.get(infoString, 0, infoStringLength);
    }


    public void processType6(byte[] data){ //REGISTRY_SENDS_NODE_MANIFEST
        ByteBuffer message = ByteBuffer.wrap(data);
        lastType = message.get();
        byte routingTableSize = message.get();
        overlay = new NodeContainer[routingTableSize];
        for(int j = 0; j < routingTableSize; j++){
            int nodeID = message.getInt();
            byte ipLength = message.get();
            byte[] nodeIP = new byte[ipLength];
            message.get(nodeIP, 0, ipLength);
            int port = message.getInt();
            NodeContainer node = new NodeContainer(nodeID, nodeIP, port, "");
            overlay[j] = node;
        }
        byte numNodes = message.get();
        nodeIDs = new int[numNodes];
        for(int j = 0; j < numNodes; j++){
            nodeIDs[j] = message.getInt();
        }
    }


    public void processType8(byte[] data){  //REGISTRY_REQUESTS_TASK_INITIATE
        ByteBuffer message = ByteBuffer.wrap(data);
        lastType = message.get();
        numMessages = message.getInt();
    }

    /*
    private void processType11(DataInputStream message){  //TODO: REGISTRY_REQUESTS_TRAFFIC_SUMMARY
        try{
            //Get IP length and read IP into variable ip.
            int ipLength = message.read();
            ip = new char[ipLength];
            message.read(ip);

            //Get port number
            port = message.read();
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 2.");
        }
    }
    */

    //=================================FROM MESSENGER TO MESSENGER======================================================

    /*
    private void processType9(DataInputStream message){  //TODO: OVERLAY_NODE_SENDS_DATA
        try{
            //Get IP length and read IP into variable ip.
            int ipLength = message.read();
            ip = new char[ipLength];
            message.read(ip);

            //Get port number
            port = message.read();
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 2.");
        }
    }
    */

    /*
    private byte[] ip;                          X
    private int port;                           X
    private byte lastType;                      X
    private byte[] infoString;                  X
    private int nodeID;                         X
    private DataInputStream incoming;           X
    private NodeContainer[] overlay;            X
    private int[] nodeIDs;                      X
     */

    public int getLastTypeReceived(){
        return lastType;
    }

    public byte[] getIP(){
        return ip;
    }

    public int getPort(){
        return port;
    }

    public String getInfoString(){
        String info = "";
        try{
            info = new String(infoString, "ASCII");
        }catch(UnsupportedEncodingException e){
            System.out.println("Could not get Info String." + e);
        }
        return info;
    }

    public int getNodeID(){
        return nodeID;
    }

    public NodeContainer[] getOverlay() {
        return overlay;
    }

    public int[] getNodeIDs() {
        return nodeIDs;
    }


    public int getNumMessages(){
        return numMessages;
    }
}
