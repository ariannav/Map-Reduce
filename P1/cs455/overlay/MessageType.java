package cs455.overlay;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MessageType {

    private byte[] ip;
    private int port;
    private byte lastType;
    private byte[] infoString;
    private int nodeID;
    private DataInputStream incoming;

    public MessageType(DataInputStream incoming){
            this.incoming = incoming;
            lastType = 0;
    }

    public void processType2() throws IOException{ //OVERLAY_NODE_SENDS_REGISTRATION
        //Get entire message
        int  dataLength = incoming.readInt();
        byte[] data = new byte[dataLength];
        incoming.readFully(data, 0, dataLength);

        //Process
        lastType = data[0];
        if(lastType != 2){
            throw new IOException("Incorrect message type received: " + lastType);
        }

        byte ipLength = data[1];
        ip = Arrays.copyOfRange(data, 2, 2 + ipLength);

        //Get port number
        port = (data[2+ipLength] << 8) + (data[3+ipLength]);
    }


    public void processType3() throws IOException{ //REGISTRY_REPORTS_REGISTRATION_STATUS
        //Get entire message
        int  dataLength = incoming.readInt();
        byte[] data = new byte[dataLength];
        incoming.readFully(data, 0, dataLength);

        //Process
        lastType = data[0];
        nodeID = (data[1] << 8) + data[2];

        if(nodeID == -1){
            throw new IOException("Message Node was not successfully registered. Status -1.");
        }

        //Get Info String length and Info String
        byte infoStringLength = data[3];
        infoString = Arrays.copyOfRange(data, 4, 4 + infoStringLength);
    }

//TODO: HAVEN'T DONE THESE =========================================================================================
/*
    private void processType4(DataInputStream message){  //OVERLAY_NODE_SENDS_DEREGISTRATION
        try{
            //Get IP length and read IP into variable ip.
            int ipLength = message.read();
            ip = new char[ipLength];
            message.read(ip, 0, ipLength);

            //Get port number
            port = message.read();

            //Get NodeID
            nodeID = message.read();
            successStatus = 1;
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 4.");
        }
    }

    private void processType5(DataInputStream message){  //REGISTRY_REPORTS_DEREGISTRATION_STATUS
        try{
            //Get successStatus
            successStatus = message.read();

            //Get Info String length and Info String
            int infoStringLength = message.read();
            infoString = new char[infoStringLength];
            message.read(infoString, 0, infoStringLength);
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 5.");
        }
    }

    private void processType6(DataInputStream message){  //TODO: REGISTRY_SENDS_NODE_MANIFEST
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

    private void processType7(DataInputStream message){  //TODO: NODE_REPORTS_OVERLAY_SETUP_STATUS
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

    private void processType8(DataInputStream message){  //TODO: REGISTRY_REQUESTS_TASK_INITIATE
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

    private void processType10(DataInputStream message){  //TODO: OVERLAY_NODE_REPORTS_TASK_FINISHED
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

    private void processType12(DataInputStream message){  //TODO: OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY
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
    //TODO: HAVEN'T DONE ABOVE==========================================================================================

    /*
    private int typeNumber;
    private byte[] ip;
    private int port;
    private int successStatus;
    private byte[] infoString;
    private int nodeID;
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
}
