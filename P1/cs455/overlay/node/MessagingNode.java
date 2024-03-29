//Author: Arianna Vacca
//Purpose: Primary thread which generates all other threads in MessagingNode process. Registry interactions occur through this thread.

package cs455.overlay.node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MessagingNode{

    public static void main(String[] argv){
        try {
            //Create the node, which contacts the registry.
            MessagingNode messager = new MessagingNode(argv[0], argv[1]);
            System.out.println("Successfully connected to registry.");

            //Sending first message.
            messager.sendMessage(2);

            //Receive Confirmation
            messager.process.processType3();
            messager.nodeID = messager.process.getNodeID();

            if(messager.nodeID == -1){
                System.out.println("Registration unsuccessful. Reason: " + messager.process.getInfoString() + " Exiting program.");
                messager.flushCloseExit();
            }
            else{
                System.out.println(messager.process.getInfoString() + " Assigned Node ID: " + messager.process.getNodeID());
            }

            Thread foreground = new Thread(new MessagingForegroundThread(messager));    //Taking commands
            foreground.start();
            Thread server = new Thread(new MNServer(messager, messager.sockit));        //Ready to accept connections.
            server.start();

            messager.waitForMessage();
        }
        catch(Exception e){
            System.out.println("Messaging Node: Issue in main-" + e + " Please restart MessagingNode.");
        }
    }

//=========================================== Messaging Node Class Start ===============================================

    private Socket registrySockit;
    private ServerSocket sockit;
    private DataOutputStream outgoing;
    private DataInputStream incoming;
    private MessageCreator creator;
    private int nodeID;
    private MessageType process;
    private boolean inProgress;
    private boolean isFinished;
    private Statistics stats;


    public MessagingNode(String registryHost, String registryPort) throws IOException{

        sockit = createServerSocket();
        int port = 0;

        try{
            InetAddress.getByName(registryHost).isReachable(2000);
            port = Integer.parseInt(registryPort);
        }
        catch(Exception e){
            throw new IOException("Provided IP address and/or port is not reachable. Please provide a valid IP address/port.");
        }

        registrySockit = new Socket(registryHost, port);
        outgoing = new DataOutputStream(registrySockit.getOutputStream());
        incoming = new DataInputStream(registrySockit.getInputStream());

        process =  new MessageType(incoming);
        creator = new MessageCreator(this);
        inProgress = false;
        isFinished = false;
        stats = new Statistics();
    }


    //Opens the initial server socket and finds an acceptable port.
    private ServerSocket createServerSocket() throws IOException{
        for(int i = 2001; i < 10000; i++){
            try{
                ServerSocket temp = new ServerSocket(i);
                return temp;
            }
            catch(Exception e){
                continue;
            }
        }
        throw new IOException("No available ports.");
    }


    //Primary Control Center
    private void waitForMessage(){
        //Could be MT 5, 6, 8, or 11 responding.
        while(true){
            try{
                process.processVariableFromRegistry();
                int type = process.getLastTypeReceived();
                if(type == 5){
                    //Asked to deregister. Hearing back.
                    nodeID = process.getNodeID();
                    if(nodeID == -1){
                        System.out.println("Deregistration unsuccessful. Reason: " + process.getInfoString() + " Exiting program.");
                        flushCloseExit();
                    }
                    else{
                        System.out.println(process.getInfoString());
                    }
                    flushCloseExit();
                }
                else if(type == 6){
                    inProgress = true;
                    initiateConnections();
                    sendMessage(7);
                }
                else if(type == 8){
                    startSendingMessages();
                    isFinished = true;
                    inProgress = false;
                    sendMessage(10);
                }
                else if(type == 11){
                    sendMessage(12);
                    resetCounters();
                    //Done! Congrats!
                }
                else{
                    System.out.println("Wrong message type received. Messaging node resuming operations.");
                }
            }
            catch(Exception e){
                System.err.println("Messaging Node: Issue in control center-" +e);
                break;
                //e.printStackTrace();
            }
        }
    }


    private void initiateConnections(){
        NodeContainer[] overlay = process.getOverlay();
        for(int i = 0; i < overlay.length; i++){
            Thread newThread = new Thread(new MNEndpoint(this, overlay[i]));
            newThread.start();
        }
    }


    public synchronized void endpointIsReady(MNEndpoint mne, int nodeID){
        for(int i = 0; i < process.getOverlay().length; i++){
            if(nodeID == process.getOverlay()[i].getNodeID()){
                process.getOverlay()[i].setEndpoint(mne);
                break;
            }
        }
    }


    private void startSendingMessages(){
        Random randomGenerator  = new Random();
        int nextID;
        for(int i = 0; i < process.getNumMessages(); i++){
            nextID = randomGenerator.nextInt(process.getNodeIDs().length);
            while(process.getNodeIDs()[nextID] == nodeID){
                nextID = randomGenerator.nextInt(process.getNodeIDs().length);
            }

            int payload = randomGenerator.nextInt();
            int[] trace = {};
            sendTo(nodeID, process.getNodeIDs()[nextID], payload, trace);

            addPacketSent();
            addPayload(payload);
        }
    }


    public synchronized void sendTo(int source, int dest, int payload, int[] trace){
        int closest = -1;
        int index = -1;

        for(int i = 0; i < process.getOverlay().length; i++){
            int currNodeID = process.getOverlay()[i].getNodeID();
            if(currNodeID <= dest && currNodeID > closest){
                closest = currNodeID;
                index = i;
                if(currNodeID == dest){ break; }
            }
        }

        if(closest == -1){
            for(int i = 0; i < process.getOverlay().length; i++){
                int currNodeID = process.getOverlay()[i].getNodeID();
                if(currNodeID > closest){
                    closest = currNodeID;
                    index = i;
                }
            }
        }

        process.getOverlay()[index].getEndpoint().addToQueue(creator.createMessageType9(source, dest, payload, trace));
    }


    public void exitOverlay(){
        System.out.print("Deregistering...");
        sendMessage(4);
    }

    //======================================Message Sending Center======================================================
    private void sendMessage(int type){
        byte[] message = new byte[0];
        switch(type){
            case 2:
                message = creator.createMessageType2();
                break;
            case 4:
                message = creator.createMessageType4();
                break;
            case 7:
                message = creator.createMessageType7();
                break;
            case 10:
                message = creator.createMessageType10();
                break;
            case 12:
                message = creator.createMessageType12();
                break;
            default:
                System.out.println("Wrong message type given to send message.");
                flushCloseExit();
        }

        try{
            outgoing.write(message, 0, message.length);
            outgoing.flush();
        }
        catch(IOException e){
            System.out.println("Messaging Node: Problem sending message-" + e);
        }

    }


    //====================================Traffic Summary Information===================================================
    public synchronized void addPacketSent(){
        stats.packetsSent++;
    }

    public int getPacketsSent(){
        return stats.packetsSent;
    }

    public synchronized void addPayload(int payload){
        stats.sumValuesSent+= payload;
    }

    public long getSentPayload(){
        return stats.sumValuesSent;
    }

    public synchronized void addPacketRecvd(){
        stats.packetsRecvd++;
    }

    public int getPacketsRecvd(){
        return stats.packetsRecvd;
    }

    public synchronized void addPacketRelayed(){
        stats.packetsRelayed++;
    }

    public int getPacketsRelayed(){
        return stats.packetsRelayed;
    }

    public synchronized void addSumValuesRecvd(int payload){
        stats.sumValuesRecvd += payload;
    }

    public long getRecvdPayload(){
        return stats.sumValuesRecvd;
    }


    private void resetCounters(){
        stats = new Statistics();
    }


    //=========================================Helper Methods===========================================================
    public String printRoutingTable(){
        String routingTable = "NodeID: " + nodeID + "\nDistance | NodeID \n";
        for(int i = 0; i < process.getOverlay().length; i++){
            routingTable += Math.pow(2, i) + " \t| " +  process.getOverlay()[i].getNodeID() + "\n";
        }
        routingTable += "Size: " + process.getOverlay().length + "\n\n";
        return routingTable;
    }


    public String getDiagnostics(){
        String answer = "\nNode ID\t| Packets Sent\t| Packets Received\t| Packets Relayed\t| Sum Values Sent\t| Sum Values Received\n"
                        + nodeID + "\t| " + stats.packetsSent + "\t\t| " + stats.packetsRecvd + "\t\t\t| " + stats.packetsRelayed
                + "\t\t\t| " + stats.sumValuesSent + "\t\t| " + stats.sumValuesRecvd + "\n";
        return answer;
    }


    public byte[] getIPAddress(){
        return registrySockit.getLocalAddress().getAddress();
    }


    //Gets the port number the server socket is running on.
    public int getPortNumber(){
        return sockit.getLocalPort();
    }


    //Returns the assigned nodeID
    public int getNodeID(){
        return nodeID;
    }


    public boolean isInProgress(){
        return inProgress;
    }


    public boolean isFinished(){
        return isFinished;
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
            //Could mean it is already closed, that is okay, exiting anyways.
        }
        System.exit(0);
    }

}