//Author: Arianna Vacca

/*This class creates the different message types and
returns a byte array to be sent. The message creator
methods below are ordered 2-12. Either a RegistryNode
or a MessagingNode can instantiate this class.
 */

package cs455.overlay.node;

import java.io.IOException;
import java.nio.ByteBuffer;
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


    public byte[] createMessageType2(){     //OVERLAY NODE SENDS REGISTRATION
        //Type and IP length
        ByteBuffer message = ByteBuffer.allocate(6 + messager.getIPAddress().length);
        message.put((byte)2);
        message.put((byte)messager.getIPAddress().length);
        message.put(messager.getIPAddress());
        message.putInt(messager.getPortNumber());

        return message.array();
    }


    public byte[] createMessageType3(int nodeID, boolean unmatchedIP){      //REGISTRY REPORTS REGISTRATION STATUS
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
        ByteBuffer message = ByteBuffer.allocate(6 + bMessage.length);
        message.put((byte) 3);   //Type
        message.putInt(nodeID);
        message.put((byte)bMessage.length);
        message.put(bMessage);

        return message.array();
    }


    public byte[] createMessageType4(){         //OVERLAY NODE SENDS DEREGISTRATION
        ByteBuffer message = ByteBuffer.allocate(10 + messager.getIPAddress().length);
        message.put((byte)4);
        message.put((byte)messager.getIPAddress().length);

        //Put IP in array
        message.put(messager.getIPAddress());

        //Port number
        message.putInt(messager.getPortNumber());

        //Port NodeID
        message.putInt(messager.getNodeID());
        return message.array();
    }


    public byte[] createMessageType5(int nodeID, boolean unmatchedIP){      //REGISTRY_REPORTS_DEREGISTRATION_STATUS
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
        ByteBuffer message = ByteBuffer.allocate(6 + bMessage.length);
        message.put((byte) 5);   //Type
        message.putInt(nodeID);
        message.put((byte)bMessage.length);
        message.put(bMessage);

        return message.array();
    }


    public byte[] createMessageType6(ArrayList<NodeContainer> overlay, int[] nodeIDs){
        int routerByteSize = 0;
        for(int i = 0; i < overlay.size(); i++){
            routerByteSize += 9 + overlay.get(i).getIPAddress().length;
        }

        ByteBuffer message = ByteBuffer.allocate(3 + routerByteSize + (4*nodeIDs.length));
        message.put((byte)6);               //Type
        message.put((byte)overlay.size());  //Routing table size

        for(int i = 0; i < overlay.size(); i++){
            message.putInt(overlay.get(i).getNodeID());              //Adding node ID
            message.put((byte)overlay.get(i).getIPAddress().length);    //Adding IP address length
            message.put(overlay.get(i).getIPAddress());
            message.putInt(overlay.get(i).getPort());                //Adding port
        }

        message.put((byte)nodeIDs.length);                              //Number of nodeIDs
        for(int i = 0; i < nodeIDs.length; i++){
            message.putInt(nodeIDs[i]);
        }

        return message.array();
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
        ByteBuffer message = ByteBuffer.allocate(6+bMessage.length);
        message.put((byte)7);
        message.putInt(messager.getNodeID());
        message.put((byte)bMessage.length);

        //Put bMessage in array
        message.put(bMessage);

        return message.array();
    }


    public byte[] createMessageType8(int numMessages){
        ByteBuffer message = ByteBuffer.allocate(5);
        message.put((byte)8);
        message.putInt(numMessages);
        return message.array();
    }


    public byte[] createMessageType9(int sourceNodeID, int destNodeID, int payload, int[] trace){
        ByteBuffer message = ByteBuffer.allocate(17 + (4*trace.length));
        message.put((byte)9);
        message.putInt(destNodeID);
        message.putInt(sourceNodeID);
        message.putInt(payload);
        message.putInt(trace.length);
        for(int i = 0; i < trace.length; i++){
            message.putInt(trace[i]);
        }
        return message.array();
    }

    public byte[] createMessageType10(){
        ByteBuffer message = ByteBuffer.allocate(10 + messager.getIPAddress().length);
        message.put((byte) 10);
        message.put((byte) messager.getIPAddress().length);
        message.put(messager.getIPAddress());
        message.putInt(messager.getPortNumber());
        message.putInt(messager.getNodeID());
        return message.array();
    }

    public byte[] createMessageType11(){
        byte[] message = {(byte)11};
        return message;
    }

    public byte[] createMessageType12(){
        ByteBuffer message = ByteBuffer.allocate(33);
        message.put((byte)12);
        message.putInt(messager.getNodeID());
        message.putInt(messager.getPacketsSent());
        message.putInt(messager.getPacketsRelayed());
        message.putLong(messager.getSentPayload());
        message.putInt(messager.getPacketsRecvd());
        message.putLong(messager.getRecvdPayload());
        return message.array();
    }
}
