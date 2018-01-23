package cs455.overlay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RegistryNode implements Runnable{

    private Socket sockit;
    private DataOutputStream outgoing;
    private DataInputStream incoming;
    private MessageType processor;
    private MessageCreator creator;
    private int nodeID;

    public RegistryNode(Socket sockit, int nodeID){
        this.sockit = sockit;
        this.nodeID = nodeID;
    }

    public void run(){
        try{
            //Open input and output stream to socket.
            outgoing = new DataOutputStream(sockit.getOutputStream());
            incoming = new DataInputStream(sockit.getInputStream());

            //Create message processor & Receive message type 2
            processor = new MessageType(incoming);
            processor.processType2();

            //Register Node
            //TODO if processor.getIP() does not equal sockit IP, set nodeID to -1. Exit thread after sending 3. (I think)

            //Create message creator & send message type 3
            creator = new MessageCreator(this);
            sendMessage(3);

            //Close the streams.
            outgoing.flush();
            outgoing.close();
            incoming.close();

            //Close the socket.
            sockit.close();
        }
        catch(IOException e){
            System.out.println("Error encountered in Registry Node. " + e);
        }
    }

    private void sendMessage(int type){
        byte[] message = new byte[0];
        switch(type){
            case 3:
                message = creator.createMessageType3(nodeID);
                break;
            case 5:
                message = creator.createMessageType5();
                break;
            case 6:
                message = creator.createMessageType6();
                break;
            case 8:
                message = creator.createMessageType8();
                break;
            case 11:
                message = creator.createMessageType11();
                break;
            default:
                System.out.println("Wrong message type given to send message.");
                System.exit(-1);
        }

        try{
            outgoing.writeInt(message.length);
            outgoing.write(message, 0, message.length);
            outgoing.flush();
        }
        catch(IOException e){
            System.out.println("Problem sending message." + e);
        }

    }
}
