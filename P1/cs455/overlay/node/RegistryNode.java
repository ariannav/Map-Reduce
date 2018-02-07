package cs455.overlay.node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class RegistryNode implements Runnable{

    private Socket sockit;
    private DataOutputStream outgoing;
    private DataInputStream incoming;
    private MessageType processor;
    private MessageCreator creator;
    private int nodeID;
    private NodeContainer thisNode;
    private boolean unmatchedIP;
    private Registry registry;
    private int numMessages;

    public RegistryNode(Socket sockit, Registry registry){
        this.sockit = sockit;
        unmatchedIP = false;
        this.registry = registry;
    }


    public void run(){
        //Registration interchange.
        try{
            //Open input and output stream to socket.
            outgoing = new DataOutputStream(sockit.getOutputStream());
            incoming = new DataInputStream(sockit.getInputStream());

            //Create message processor
            processor = new MessageType(incoming);
            creator = new MessageCreator(this);

            //Receive message type 2.
            processor.processType2();

            //Register and send type 3.
            register(); //Sends message type 3.
        }
        catch(IOException e){
            System.err.println("Error: Rare occurrence of Messenger Node failing after registration request. Node was not registered. Exiting. ");
            flushCloseExit();
        }

        //Could be MT 4, 7, 10, or 12 responding.
        while(true){
            try{
                processor.processVariableFromMessenger();
                int type = processor.getLastTypeReceived();
                if(type == 4){
                    //De-register
                    deregister();
                    sendMessage(5);     //Success!
                    throw new IOException("interrupt");
                }
                else if(type == 7){
                    registry.incrementReady();  //Number of nodes ready increases, now we let the registry send the setup-overlay message.
                }
                else if(type == 10){
                    registry.incrementTaskComplete();
                }
                else if(type == 12){
                    Statistics nodeStats = new Statistics(nodeID, processor.getTotalPacketsSent(), processor.getTotalPacketsRelayed(),
                            processor.getSumDataSent(), processor.getTotalPacketsRecvd(), processor.getSumDataRecvd());
                    registry.submitStats(nodeStats);
                }

            }
            catch(IOException e){
                if(e.toString().equals("java.io.IOException: interrupt")){
                    break;
                }
                else{
                    System.out.println(e );
                    flushCloseExit();
                }
            }
        }
    }


    private void register() throws IOException{
        //Registration
        if(!Arrays.equals(processor.getIP(), sockit.getInetAddress().getAddress())){
            //Do not register
            nodeID = -1;
            unmatchedIP = true;
            sendMessage(3);
            flushCloseExit();
        }
        else if((thisNode = registry.registerNode(processor.getIP(), processor.getPort(),
                sockit.getInetAddress().getHostName().toString(), this)).getNodeID() == -1){
            //Register
            nodeID = thisNode.getNodeID();
            sendMessage(3);
            flushCloseExit();
        }
        else{
            nodeID =  thisNode.getNodeID();
            sendMessage(3);
        }
    }


    private void deregister() throws IOException{
        unmatchedIP = false;
        //See if the IP matches
        if(!Arrays.equals(processor.getIP(), sockit.getInetAddress().getAddress())){
            //Do not deregister
            nodeID = -1;
            unmatchedIP = true;
            sendMessage(5);
            throw new IOException("interrupt");
        }
        else if(registry.deregisterNode(thisNode) == 0){
            //Deregister failed, thisNode.getID() should return -1 now.
            sendMessage(5);
            throw new IOException("interrupt");
        }
    }



    public void setupOverlay(){
        try{
            sendMessage(6);
        }
        catch(IOException e){
            registry.deregisterNode(thisNode);
            flushCloseExit();
        }
    }


    public void tellMessagerToSend(int numMessages){
        try{
            this.numMessages = numMessages;
            sendMessage(8);
        }
        catch(IOException e){
            registry.deregisterNode(thisNode);
            flushCloseExit();
        }
    }


    public void getTrafficSummary(){
        try{
            sendMessage(11);
        }
        catch(IOException e){
            System.out.println("Cannot send traffic summary request! " + e);
            flushCloseExit();
        }
    }


    //==========================================Sending=================================================================
    private void sendMessage(int type) throws IOException{
        byte[] message = new byte[0];
        switch(type){
            case 3:
                message = creator.createMessageType3(nodeID, unmatchedIP);
                break;
            case 5:
                message = creator.createMessageType5(nodeID, unmatchedIP);
                break;
            case 6:
                message = creator.createMessageType6(thisNode.getOverlay(), registry.getNodeIDs());
                break;
            case 8:
                message = creator.createMessageType8(numMessages);
                break;
            case 11:
                message = creator.createMessageType11();
                break;
            default:
                System.out.println("Wrong message type given to send message.");
                flushCloseExit();
        }

        outgoing.write(message, 0, message.length);
        outgoing.flush();
    }

    //========================================Helper Methods============================================================
    public int getNumNodes(){
        return registry.getNumNodes();
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
        catch (Exception e){
            Thread.currentThread().interrupt();
        }
        Thread.currentThread().interrupt();
    }
}
