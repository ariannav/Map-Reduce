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

    //====================================RECEIVED BY REGISTRY NODE=====================================================

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
        port = (data[2+ipLength] << 8) | (data[3+ipLength] & 0xFF);
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


    public void processType4(byte[] data) throws IOException{  //OVERLAY_NODE_SENDS_DEREGISTRATION
        if(lastType != 4){
            throw new IOException("Incorrect message type received: " + lastType);
        }

        byte ipLength = data[1];
        ip = Arrays.copyOfRange(data, 2, 2 + ipLength);

        //Get port number
        port = (data[2+ipLength] << 8) | (data[3+ipLength] & 0xFF);
    }


    private void processType7(byte[] data) throws IOException{  //NODE_REPORTS_OVERLAY_SETUP_STATUS
        //Get node ID
        nodeID = (data[1]<<8) | (data[2] & 0xFF);

        byte length = data[3];
        infoString = Arrays.copyOfRange(data, 4, 4 + length);

        if(nodeID == -1 ) {
            throw new IOException("Node was unable to setup overlay. " + infoString);
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
        lastType = data[0];
        nodeID = (data[1]  << 8) | (data[2] & 0xFF);

        //Get Info String length and Info String
        byte infoStringLength = data[3];
        infoString = Arrays.copyOfRange(data, 4, 4 + infoStringLength);

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
                //processType8(data);
                break;
            case 11:
                //processType11(data);
                break;
            default:
                throw new IOException("Incorrect type number. Type received: " + lastType);
        }
    }


    public void processType5(byte[] data){  //REGISTRY_REPORTS_DEREGISTRATION_STATUS

        nodeID = (data[1]  << 8) | (data[2] & 0xFF);

        //Get Info String length and Info String
        byte infoStringLength = data[3];
        infoString = Arrays.copyOfRange(data, 4, 4 + infoStringLength);

    }


    public void processType6(byte[] data){  //TODO: REGISTRY_SENDS_NODE_MANIFEST

        nodeID = (data[1]  << 8) | (data[2] & 0xFF);

        //Get Info String length and Info String
        byte infoStringLength = data[3];
        infoString = Arrays.copyOfRange(data, 4, 4 + infoStringLength);
    }

    /*
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
