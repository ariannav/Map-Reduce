package cs455.overlay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class MessagingNode{

    public static void main(String[] argv){
        try {
            //Create the node, which contacts the registry.
            MessagingNode messager = new MessagingNode(argv[0], argv[1]);
            System.out.println("Successfully connected to registry.");

            //Sending first message.
            messager.sendMessage(2);

            //Receive Confirmation
            MessageType process = new MessageType(messager.incoming);
            process.processType3();
            messager.nodeID = process.getNodeID();

            if(messager.nodeID == -1){
                System.out.println("Registration unsuccessful. Reason: " + process.getInfoString() + " Exiting program.");
                messager.flushCloseExit();
            }
            else{
                System.out.println(process.getInfoString() + " Assigned Node ID: " + process.getNodeID());
            }

            //TODO: Add foreground thread, which should be able to call deregister
            Thread server = new Thread(new MNServer(messager, messager.sockit));
            server.start();

        }
        catch(Exception e){
            System.out.println("Error encountered in main: " + e);
        }
    }

//=========================================== Messaging Node Class Start ===============================================

    private Socket registrySockit;
    private ServerSocket sockit;
    private DataOutputStream outgoing;
    private DataInputStream incoming;
    private MessageCreator creator;
    private int nodeID;


    public MessagingNode(String registryHost, String registryPort){

        try{
            sockit = createServerSocket();
        }
        catch(IOException e){
            System.out.println("Error creating server socket. " + e);
            System.exit(-1);
        }

        int port = 0;

        try{
            InetAddress.getByName(registryHost).isReachable(2000);
            port = Integer.parseInt(registryPort);
        }
        catch(Exception e){
            System.out.println("Provided IP address and/or port is not reachable. Please provide a valid IP address/port.");
            flushCloseExit();
        }

        try{
            registrySockit = new Socket(registryHost, port);
            outgoing = new DataOutputStream(registrySockit.getOutputStream());
            incoming = new DataInputStream(registrySockit.getInputStream());
        }
        catch(IOException e){
            System.out.println("Cannot create messaging node. Could not connect to registry.");
        }

         creator = new MessageCreator(this);
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
            case 9:
                message = creator.createMessageType9();
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
            outgoing.writeInt(message.length);
            outgoing.write(message, 0, message.length);
            outgoing.flush();
        }
        catch(IOException e){
            System.out.println("Problem sending message." + e);
        }

    }


    private void deregister(MessageType process){
        System.out.print("Deregistering...");
        sendMessage(4);
        try{
            process.processType5();
        }
        catch(IOException e){
            System.out.println("Problem deregistering. Unable to process deregistration confirmation.");
            flushCloseExit();
        }

        nodeID = process.getNodeID();

        if(nodeID == -1){
            System.out.println("Deregistration unsuccessful. Reason: " + process.getInfoString() + " Exiting program.");
            flushCloseExit();
        }
        else{
            System.out.println(process.getInfoString());
        }

    }


    public String getDiagnostics(){
        //TODO
        return "Nothing written yet!";
    }


    public void exitOverlay(){
        //TODO
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
}