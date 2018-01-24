package cs455.overlay;

import java.io.IOException;
import java.util.Arrays;

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
        byte length = (byte) messager.getIPAddress().length; //TODO could be causing trouble if there are issues with the type 2 message.

        int arrayLength = 1 + 1 + length + 2;
        byte[] message = new byte[arrayLength];
        message[0] = type;
        message[1] = length;

        //Put IP in array
        for(int i = 2; i < length; i++){
            message[i] = messager.getIPAddress()[i-2];
        }

        System.out.println("IP Address for sending type 2:" + Arrays.toString(messager.getIPAddress()));

        //Port number
        message[2 + length] = (byte) (messager.getPortNumber() >> 8);
        message[3 + length] = (byte) (messager.getPortNumber());

        //Sanity check
        if(arrayLength - 1 != 3 + length){
            System.out.println("Your math is wrong Ari!");
        }

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
            infoString = "Registry successful. " + registry.getNumNodes();
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

        //Put bMessage in array
        for(int i = 3; i < bMessage.length; i++){
            message[i] = bMessage[i-3];
        }

        return message;
    }

    public byte[] createMessageType4(){
        return new byte[0];
    }

    public byte[] createMessageType5(){
        return new byte[0];
    }

    public byte[] createMessageType6(){
        return new byte[0];
    }

    public byte[] createMessageType7(){
        return new byte[0];
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