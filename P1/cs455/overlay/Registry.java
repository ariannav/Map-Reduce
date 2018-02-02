package cs455.overlay;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Registry implements Comparator<NodeContainer>{

    public static void main(String[] argv){
        //Create registry, opens socket and finds port number.
        Registry registry = new Registry();
        System.out.println("Registry is now listening at " + registry.getInetAddress() + " on port " + registry.getPortNumber() + ".");

        //Create foreground thread.
        Thread foreground = new Thread(new ForegroundThread(registry));
        foreground.start();

        //Registry listen for incoming connections.
        registry.listen();

        System.out.println("Closing server socket, exiting program.");
        registry.closeServSocket();
        foreground.interrupt();
    }


    //==============================================Start Registry Class================================================

    private ServerSocket sockit;
    private ArrayList<NodeContainer> nodes;
    private Random randomGenerator;
    private ArrayList<RegistryNode> registries;
    private int ready;
    private int finished;
    private ArrayList<Statistics> allStatistics;


    //Registry Constructor
    public Registry(){
        try{
            sockit = createServerSocket();
            nodes = new ArrayList<>();
            randomGenerator = new Random();
            registries = new ArrayList<>();
            ready = 0;
            finished = 0;
        }
        catch(IOException e){
            System.out.println("Error encountered creating socket." + e + "\n");
        }
    }


    //Opens the initial server socket and finds an acceptable port.
    private ServerSocket createServerSocket() throws IOException{
        for(int i = 2000; i < 10000; i++){
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


    //Listens for incoming connections, this should be going on throughout the entire program.
    private void listen(){
        while(true){
            try{
                Socket messengerSockit = sockit.accept();
                Thread newThread = new Thread(new RegistryNode(messengerSockit, this));
                newThread.start();
            }
            catch(Exception e){
                System.out.println("Problem accepting connection on registry server socket. " + e);
            }

        }
    }


    public synchronized NodeContainer registerNode(byte[] ip, int port, String hostname, RegistryNode registry){
        //If node previously registered, return -1. (IP is already listed in node list, and port is the same)
        int nodeID = randomGenerator.nextInt(128);
        for(int i = 0; i < nodes.size(); i++){
            if(Arrays.equals(nodes.get(i).getIPAddress(), ip) && nodes.get(i).getPort() == port){
                return new NodeContainer(-1, ip, port, hostname);
            }
            if(nodeID == nodes.get(i).getNodeID()){
                nodeID = -1;
            }
        }

        while(nodeID == -1){
            nodeID = randomGenerator.nextInt(128);
            for(int i = 0; i < nodes.size(); i++){
                if(nodeID == nodes.get(i).getNodeID()){
                    nodeID = -1;
                }
            }
        }

        NodeContainer newNode = new NodeContainer(nodeID, ip, port, hostname);
        nodes.add(newNode);
        registries.add(registry);
        return newNode;
    }


    public synchronized int deregisterNode(NodeContainer node){
        //If node previously not previously registered, return -1.
        for(int i = 0; i < nodes.size(); i++){
            if(nodes.get(i).getNodeID() == node.getNodeID()){
                nodes.remove(i);
                return 1; //Success!
            }
        }

        node.setNodeID(-1);
        return 0; //Fail!
    }


    public void setupOverlay(int numEntries){
        nodes.sort(this::compare);  //Sorting the nodes by NodeID

        for(int i = 0; i < nodes.size(); i++){  //For each node
            ArrayList<NodeContainer> overlay = new ArrayList<>(numEntries);
            for(int nodesAway = 1; nodesAway < Math.pow(2, numEntries); nodesAway *= 2){
                int index = (i + nodesAway) % nodes.size();
                if(index == i){ index++; index %= nodes.size(); }
                overlay.add(nodes.get(index));
            }

            nodes.get(i).setOverlay(overlay);
        }

        for(int j = 0; j < registries.size(); j++){
            registries.get(j).setupOverlay();
        }
    }


    public void startSendingMessages(int numMessages){
        if(ready != nodes.size()){
            System.out.println("Not all messenger nodes are ready. Please try again.");
        }
        for(int j = 0; j < registries.size(); j++){
            registries.get(j).tellMessagerToSend(numMessages);
        }
    }


    public void requestTrafficSummary(){
        for(int i = 0; i < registries.size(); i++){
            registries.get(i).getTrafficSummary();
        }
    }


    //===========================================Foreground Requests====================================================

    public String getMessagingNodeInfo(){
        if(nodes.size() == 0){
            return "There are currently no registered nodes.\n";
        }
        String infoString = "";
        for(int i = 0; i < nodes.size(); i++){
            infoString += "Node: " + nodes.get(i).getNodeID() +
                    " \tHostname: " + nodes.get(i).getHostname() +
                    " \tPort: " + nodes.get(i).getPort() + "\n";
        }
        return infoString + "Total number: " + nodes.size() + "\n";
    }


    public String getRoutingTables(){
        String routingTables = "";
        for(int i = 0; i < nodes.size(); i++){
            routingTables += "Routing Table of Node: " + nodes.get(i).getNodeID() + "\n"
                    +  nodes.get(i).getRoutingTableString() + "\n\n";
        }
        return routingTables;
    }


    //===========================================Registry Node Increments===============================================

    public synchronized void incrementReady(){
        ready++;
    }


    public synchronized void incrementTaskComplete(){
        finished++;
        if(finished == nodes.size()){
            requestTrafficSummary();
        }
    }

    public synchronized void submitStats(Statistics nodeStats){
        allStatistics = new ArrayList<>(nodes.size());
        allStatistics.add(nodeStats);
        if(allStatistics.size() == nodes.size()){
            printStatistics();
        }
    }

    //================================================Helper Methods====================================================
    @Override
    public int compare(NodeContainer n1, NodeContainer n2){
        Integer n1ID = n1.getNodeID();
        Integer n2ID = n2.getNodeID();
        return n1ID.compareTo(n2ID);
    }

    public int[] getNodeIDs(){
        int[] nodeIDs = new int[nodes.size()];
        for(int i = 0; i < nodes.size(); i++){
            nodeIDs[i] = nodes.get(i).getNodeID();
        }
        return nodeIDs;
    }

    public int getNumNodes(){
        return nodes.size();
    }

    //Gets the port number the server socket is running on.
    public int getPortNumber(){
        return sockit.getLocalPort();
    }

    //Returns the host name of the machine the server is running on.
    public InetAddress getInetAddress(){
        try{
            return sockit.getInetAddress().getLocalHost();
        }
        catch(Exception e){
            System.out.println("Cannot get local host address.");
        }
        return null;
    }

    private void printStatistics(){
        System.out.println("Node ID\t| Packets Sent\t| Packets Received\t| Packets Relayed\t| Sum Values Sent\t| Sum Values Received");
        int totalSent = 0, totalReceived = 0, totalRelayed = 0;
        long totalPayloadSent = 0, totalPayloadRecieved = 0;
        for(int i = 0; i < allStatistics.size(); i++){
            Statistics thisNode = allStatistics.get(i);
            System.out.println(thisNode.nodeID + "\t| " + thisNode.packetsSent + "\t| " + thisNode.packetsRecvd + "\t| " + thisNode.packetsRelayed
                    + "\t| " + thisNode.sumValuesSent + "\t| " + thisNode.sumValuesRecvd);
            totalSent += thisNode.packetsSent;
            totalReceived += thisNode.packetsRecvd;
            totalRelayed += thisNode.packetsRelayed;
            totalPayloadSent += thisNode.sumValuesSent;
            totalPayloadRecieved += thisNode.sumValuesRecvd;
        }
        System.out.println("TOTAL\t| " + totalSent + "\t| " + totalReceived + "\t| " + totalRelayed + "\t| " + totalPayloadSent + "\t| " + totalPayloadRecieved);
    }

    //Closes the server socket.
    private void closeServSocket(){
        try{
            sockit.close();
        }
        catch(IOException e){
            System.out.println("Error closing server socket. " + e);
        }
    }
}


