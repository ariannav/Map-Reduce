package cs455.overlay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class MNEndpoint implements Runnable{

    private MessagingNode messager;
    private NodeContainer neighbor;
    private Socket sockit;
    private DataOutputStream outgoing;
    private DataInputStream incoming;
    private MessageCreator creator;
    private Random randomGenerator;

    public MNEndpoint(MessagingNode messager, NodeContainer neighbor){
        this.messager = messager;
        this.neighbor = neighbor;

        try{
            sockit = new Socket(InetAddress.getByAddress(neighbor.getIPAddress()), neighbor.getPort());
            outgoing = new DataOutputStream(sockit.getOutputStream());
            incoming = new DataInputStream(sockit.getInputStream());
        }
        catch(IOException e){
            System.out.println("Cannot create messaging node. Could not connect to registry. Please try again.");
            flushCloseExit();
        }
        creator = new MessageCreator(messager);
        randomGenerator = new Random();
    }


    @Override
    public void run() {
        messager.endpointIsReady(this, neighbor.getNodeID());
    }


    public void sendTo(int destNodeID) throws IOException{
        int[] trace = {messager.getNodeID()};
        int payload = randomGenerator.nextInt();
        byte[] message = creator.createMessageType9(destNodeID, payload, trace);

        outgoing.write(message, 0, message.length);
        outgoing.flush();
        messager.addPacketSent();
        messager.addPayload(payload);
    }


    private void flushCloseExit(){
        try{
            //Close the streams.
            outgoing.flush();
            outgoing.close();
            incoming.close();

            //Close the socket.
            sockit.close();
        }
        catch (IOException e){
            System.exit(-1);
        }
        System.exit(0);
    }
}
