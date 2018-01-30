package cs455.overlay;

import java.io.IOException;
import java.util.ArrayList;

public class MessageCreator {

    private MessagingNode messager;
    private RegistryNode registry;

    public MessageCreator(MessagingNode messager){
        this.messager = messager;
    }

    public MessageCreator(RegistryNode registry){
        this.registry = registry;
    }

    public byte[] createMessageType2(){
        //Type and IP length
        byte type = 2;
        byte length = (byte) messager.getIPAddress().length;

        int arrayLength = 1 + 1 + length + 2;
        byte[] message = new byte[arrayLength];
        message[0] = type;
        message[1] = length;

        //Put IP in array
        for(int i = 2; i < length+2; i++){
            message[i] = messager.getIPAddress()[i-2];
        }

        //Port number
        message[2 + length] = (byte) (messager.getPortNumber() >> 8);
        message[3 + length] = (byte) (messager.getPortNumber());

        return message;
    }

    public byte[] createMessageType3(int nodeID, boolean unmatchedIP){
        String infoString;
        if(nodeID == -1){
            if(unmatchedIP){
                infoString = "Registry unsuccessful. Given IP does not match the socket IP.";
            }
            else{
                infoString = "Registry unsuccessful. This node has been previously registered.";
            }
        }
        else{
            infoString = "Registry successful. There are currently " + registry.getNumNodes() + " node(s) in the system.";
        }

        byte[] bMessage = new byte[0];
        try{
            bMessage = infoString.getBytes("ASCII");
        }
        catch(IOException e){
            System.out.println("Cannot create message type 3. " + e);
            System.exit(-1);
        }

        //Create message
        byte[] message = new byte[4 + bMessage.length];
        message[0] = 3; //Type
        message[1] = (byte)(nodeID>>8);
        message[2] = (byte)(nodeID);
        message[3] = (byte) bMessage.length;

        //Put bMessage in array
        for(int i = 4; i < bMessage.length+4; i++){
            message[i] = bMessage[i-4];
        }

        return message;
    }

    public byte[] createMessageType4(){
        byte length = (byte) messager.getIPAddress().length;
        byte[] message = new byte[6 + length];
        message[0] = 4;
        message[1] = length;

        //Put IP in array
        for(int i = 2; i < length+2; i++){
            message[i] = messager.getIPAddress()[i-2];
        }

        //Port number
        message[2 + length] = (byte) (messager.getPortNumber() >> 8);
        message[3 + length] = (byte) (messager.getPortNumber());

        //Port number
        message[4 + length] = (byte) (messager.getNodeID() >> 8);
        message[5 + length] = (byte) (messager.getNodeID());

        return message;
    }


    public byte[] createMessageType5(int nodeID, boolean unmatchedIP){
        String infoString;
        if(nodeID == -1){
            if(unmatchedIP){
                infoString = "Deregistration unsuccessful. Given IP does not match the socket IP.";
            }
            else{
                infoString = "Deregistration unsuccessful. This node has not been previously registered.";
            }
        }
        else{
            infoString = "Deregistration successful. Goodbye!";
        }

        byte[] bMessage = new byte[0];
        try{
            bMessage = infoString.getBytes("ASCII");
        }
        catch(IOException e){
            System.out.println("Cannot create message type 5. " + e);
            System.exit(-1);
        }

        //Create message
        byte[] message = new byte[4 + bMessage.length];
        message[0] = 5; //Type
        message[1] = (byte)(nodeID>>8);
        message[2] = (byte)(nodeID);
        message[3] = (byte) bMessage.length;

        //Put bMessage in array
        for(int i = 4; i < bMessage.length+4; i++){
            message[i] = bMessage[i-4];
        }

        return message;
    }

    public byte[] createMessageType6(ArrayList<NodeContainer> overlay, int[] nodeIDs){
        ArrayList<Byte> message = new ArrayList<>();
        message.add((byte)6);               //Type
        message.add((byte)overlay.size());  //Routing table size

        for(int i = 0; i < overlay.size(); i++){
            message.add((byte)(overlay.get(i).getNodeID() >> 8));
            message.add((byte)overlay.get(i).getNodeID());              //Adding node ID
            message.add((byte)overlay.get(i).getIPAddress().length);    //Adding IP address length
            for(int j = 0; j < overlay.get(i).getIPAddress().length; j++){  //Adding IP address
                message.add(overlay.get(i).getIPAddress()[j]);
            }
            message.add((byte)(overlay.get(i).getPort() >> 8));
            message.add((byte)overlay.get(i).getPort());                //Adding port
        }

        message.add((byte)nodeIDs.length);                              //Number of nodeIDs
        for(int k = 0; k < nodeIDs.length; k++){
            message.add((byte)(nodeIDs[k] >> 8));                       //Adding each node ID
            message.add((byte)nodeIDs[k]);
        }

        byte[] bm = new byte[message.size()];
        for(int l = 0; l < message.size(); l++){
            bm[l] = message.get(l).byteValue();
        }
        return bm;
    }

    public byte[] createMessageType7(){
        String infoString = "Overlay setup success.";

        byte[] bMessage = new byte[0];
        try{
            bMessage = infoString.getBytes("ASCII");
        }
        catch(IOException e){
            System.out.println("Cannot create message type 7. " + e);
            System.exit(-1);
        }

        //Create message
        byte[] message = new byte[4 + bMessage.length];
        message[0] = 7; //Type
        message[1] = (byte)(messager.getNodeID()>>8);
        message[2] = (byte)(messager.getNodeID());
        message[3] = (byte) bMessage.length;

        //Put bMessage in array
        for(int i = 4; i < bMessage.length+4; i++){
            message[i] = bMessage[i-4];
        }

        return message;
    }

    public byte[] createMessageType8(){
        return new byte[0];
    }

    public byte[] createMessageType9(){
        return new byte[0];
    }

    public byte[] createMessageType10(){
        return new byte[0];
    }

    public byte[] createMessageType11(){
        return new byte[0];
    }

    public byte[] createMessageType12(){
        return new byte[0];
    }
}
