package cs455.overlay;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MNServer implements Runnable{

    private MessagingNode parent;
    private ServerSocket sockit;
    private int numMNRouters;
    private int numReady;


    public MNServer(MessagingNode parent, ServerSocket sockit){
        this.parent = parent;
        this.sockit = sockit;
        numMNRouters = 0;
        numReady = 0;
    }


    @Override
    public void run(){
        while(true){
            try{
                Socket routerSockit = sockit.accept();
                Thread newThread = new Thread(new MNRouter(routerSockit, parent, this));
                newThread.start();
                numMNRouters++;
            }
            catch(Exception e){
                try{
                    sockit.close();
                }
                catch(IOException ioe){
                    System.out.println("Cannot close sockit. " + ioe + " Received error: " + e);
                }
            }

        }
    }


    public synchronized void routerIsDone(){
        numReady++;
        if(numReady == numMNRouters){
            parent.ready();
        }
    }
}
