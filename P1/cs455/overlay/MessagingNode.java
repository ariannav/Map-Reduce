package cs455.overlay;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class MessagingNode implements Runnable{

    public static void main(String[] argv){
        int port = 0;

        try{
            InetAddress.getByName(argv[0]).isReachable(2000);
            port = Integer.parseInt(argv[1]);
        }
        catch(Exception e){
            System.out.println("Provided IP address and/or port is not reachable. Please provide a valid IP address/port.");
            System.exit(-1);
        }

        //Create the node, which contacts the registry.
        MessagingNode messager = new MessagingNode(argv[0], port);
        System.out.println("Successfully connected to registry.");
    }

//=========================================== Messaging Node Class Start ===============================================

    Socket registrySockit;

    public MessagingNode(String registryHost, int registryPort){
        try{
            registrySockit = new Socket(registryHost, registryPort);
        }
        catch(IOException e){
            System.out.println("Cannot create messaging node. Could not connect to registry.");
        }
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