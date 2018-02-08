package cs455.overlay.node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;

public class MNEndpoint implements Runnable{

    private MessagingNode messager;
    private NodeContainer neighbor;
    private Socket sockit;
    private DataOutputStream outgoing;
    private DataInputStream incoming;
    private MessageCreator creator;
    private ConcurrentLinkedQueue<byte[]> queue;

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
        queue = new ConcurrentLinkedQueue<>();
    }


    @Override
    public void run() {
        messager.endpointIsReady(this, neighbor.getNodeID());

        while(true){
            byte[] message = queue.poll();
            if(message != null){
                try{
                    outgoing.write(message, 0, message.length);
                    outgoing.flush();
                }
                catch(IOException e){
                    System.out.println("Problem reading from queue: " + e);
                    e.printStackTrace();
                }
            }
        }
    }


    public void addToQueue(byte[] message){
        queue.add(message);
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
