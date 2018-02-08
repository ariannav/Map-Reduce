//Author: Arianna Vacca
//Purpose: This class connects to each MessagingNode within the parent thread's routing table.

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
            System.out.println("MessagingNode Endpoint: Could not create endpoint. Connection to " + neighbor.getNodeID() + " failed.");
            flushCloseExit();
        }
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
                    System.out.println("MessagingNode Endpoint: Message sending for connection " + neighbor.getNodeID() + " failed-" + e + ". Retrying.");
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
            //Connection could be closed already.
        }
        System.exit(0);
    }
}
