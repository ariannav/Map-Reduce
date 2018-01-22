package cs455.overlay;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MessagingNode implements Runnable{

    public static void main(String[] argv){

        //Create the node, which contacts the registry.
        MessagingNode messager = new MessagingNode(argv[0], argv[1]);
        System.out.println("Successfully connected to registry.");

        //Sending first message.
        messager.sendMessage(2);
    }

//=========================================== Messaging Node Class Start ===============================================

    private Socket registrySockit;
    private ServerSocket sockit;
    private PrintWriter serverConnection;

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
            System.exit(-1);
        }


        try{
            registrySockit = new Socket(registryHost, port);
            serverConnection = new PrintWriter(registrySockit.getOutputStream(), true);
        }
        catch(IOException e){
            System.out.println("Cannot create messaging node. Could not connect to registry.");
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


    public byte[] getIPAddress(){
        return sockit.getInetAddress().getAddress();
    }


    //Gets the port number the server socket is running on.
    public int getPortNumber(){
        return sockit.getLocalPort();
    }

    private void sendMessage(int type){
        MessageCreator creator = new MessageCreator(this);
        byte[] message = creator.createMessage(2);
        serverConnection.print(message);
        serverConnection.flush();       //TODO: It is my belief that this is wrong. Look into.
    }


    public String getDiagnostics(){
        //TODO
        return "Nothing written yet!";
    }

    public void exitOverlay(){
        //TODO
    }

    @Override
    public void run() {

    }
}