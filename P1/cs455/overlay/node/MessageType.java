package cs455.overlay.node;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MessageType {
    //General Messaging Info
    private byte[] ip;
    private int port;
    private int nodeID;
    private byte lastType;
    private byte[] infoString;

    //Input Stream
    private DataInputStream incoming;

    //Overlay Construction
    private NodeContainer[] overlay;
    private int[] nodeIDs;
    private int numMessages;
    private int payload;
    private int sourceID;
    private int destID;
    private int[] trace;

    //Statistics
    private int totalPacketsSent;
    private int totalPacketsRelayed;
    private long sumDataSent;
    private int totalPacketsRecvd;
    private long sumDataRecvd;


    public MessageType(DataInputStream incoming){
            this.incoming = incoming;
            lastType = 0;
    }

    //====================================RECEIVED BY REGISTRY NODE=====================================================

    public void processType2() throws IOException{ //OVERLAY_NODE_SENDS_REGISTRATION
        lastType = incoming.readByte();
        if(lastType != 2){
            throw new IOException("Incorrect message type received: " + lastType);
        }

        byte ipLength = incoming.readByte();
        ip = new byte[ipLength];
        incoming.read(ip, 0, ipLength);

        //Get port number
        port = incoming.readInt();
    }


    public void processVariableFromMessenger() throws IOException{
        //Get entire message
        lastType = incoming.readByte();

        switch(lastType){
            case 4:
                processType4();
                break;
            case 7:
                processType7();
                break;
            case 10:
                processType10();
                break;
            case 12:
                processType12();
                break;
            default:
                throw new IOException("Incorrect type number. Type received: " + lastType);
        }
    }


    public void processType4() throws IOException{  //OVERLAY_NODE_SENDS_DEREGISTRATION
        byte length = incoming.readByte();

        //Put IP in array
        ip = new byte[length];
        incoming.read(ip, 0, length);

        //Port number
        port = incoming.readInt();

        //Port NodeID
        nodeID = incoming.readInt();

    }


    private void processType7() throws IOException{  //NODE_REPORTS_OVERLAY_SETUP_STATUS
        nodeID = incoming.readInt();

        byte length = incoming.readByte();
        infoString = new byte[length];
        incoming.read(infoString, 0, length);

        if(nodeID == -1 ) {
            throw new IOException("Node was unable to setup overlay. " + getInfoString());
        }
    }


    private void processType10() throws IOException{  //OVERLAY_NODE_REPORTS_TASK_FINISHED
       byte length = incoming.readByte();
       ip = new byte[length];
       incoming.read(ip, 0, length);
       port = incoming.readInt();
       nodeID = incoming.readInt();
    }


    private void processType12() throws IOException{  //OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY
        nodeID = incoming.readInt();
        totalPacketsSent = incoming.readInt();
        totalPacketsRelayed = incoming.readInt();
        sumDataSent = incoming.readLong();
        totalPacketsRecvd = incoming.readInt();
        sumDataRecvd = incoming.readLong();
    }

    //=============================RECEIVED BY THE MESSENGER NODE=======================================================

    public void processType3() throws IOException{ //REGISTRY_REPORTS_REGISTRATION_STATUS
        lastType = incoming.readByte();
        nodeID = incoming.readInt();

        //Get Info String length and Info String
        byte infoStringLength = incoming.readByte();
        infoString = new byte[infoStringLength];
        incoming.read(infoString, 0, infoStringLength);

        if(nodeID == -1){
            throw new IOException("Message Node was not successfully registered. NodeID -1. " + getInfoString());
        }
    }


    public void processVariableFromRegistry() throws IOException{
        lastType = incoming.readByte();

        switch(lastType){
            case 5:
                processType5();
                break;
            case 6:
                processType6();
                break;
            case 8:
                processType8();
                break;
            case 11:
                //processType11();  <= Not needed, Type 11 only contains the message type.
                break;
            default:
                throw new IOException("Incorrect type number. Type received: " + lastType);
        }
    }


    public void processType5() throws IOException{  //REGISTRY_REPORTS_DEREGISTRATION_STATUS
        nodeID = incoming.readInt();

        //Get Info String length and Info String
        byte infoStringLength = incoming.readByte();
        infoString = new byte[infoStringLength];
        incoming.read(infoString, 0, infoStringLength);
    }


    public void processType6() throws IOException{ //REGISTRY_SENDS_NODE_MANIFEST
        byte routingTableSize = incoming.readByte();
        overlay = new NodeContainer[routingTableSize];
        for(int j = 0; j < routingTableSize; j++){
            int nodeID = incoming.readInt();
            byte ipLength = incoming.readByte();
            byte[] nodeIP = new byte[ipLength];
            incoming.read(nodeIP, 0, ipLength);
            int port = incoming.readInt();
            NodeContainer node = new NodeContainer(nodeID, nodeIP, port, "");
            overlay[j] = node;
        }
        byte numNodes = incoming.readByte();
        nodeIDs = new int[numNodes];
        for(int j = 0; j < numNodes; j++){
            nodeIDs[j] = incoming.readInt();
        }
    }


    public void processType8() throws IOException{  //REGISTRY_REQUESTS_TASK_INITIATE
        numMessages = incoming.readInt();
    }


    //private void processType11(){  //REGISTRY_REQUESTS_TRAFFIC_SUMMARY
        //Dummy method for readability's sake. This message only contains the type bit- already been processed!
    //}

    //=================================FROM MESSENGER TO MESSENGER======================================================

    public void processType9() throws IOException{  //OVERLAY_NODE_SENDS_DATA
        lastType = incoming.readByte();
        destID = incoming.readInt();
        sourceID = incoming.readInt();
        payload = incoming.readInt();
        int length = incoming.readInt();
        trace = new int[length];
        for(int i = 0; i < length; i++){
            trace[i] = incoming.readInt();
        }
    }

    //==============================================GETTERS=============================================================

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

    public int getPayload(){
        return payload;
    }

    public int getSourceID(){
        return sourceID;
    }

    public int getDestID(){
        return destID;
    }

    public int[] getTrace(){
        return trace;
    }

    public int getTotalPacketsSent(){
        return totalPacketsSent;
    }

    public int getTotalPacketsRelayed(){
        return totalPacketsRelayed;
    }

    public long getSumDataSent(){
        return sumDataSent;
    }

    public int getTotalPacketsRecvd(){
        return totalPacketsRecvd;
    }

    public long getSumDataRecvd(){
        return sumDataRecvd;
    }
}

