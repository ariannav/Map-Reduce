package cs455.overlay;

import java.net.ServerSocket;
import java.net.Socket;

public class MNServer implements Runnable{

    private MessagingNode parent;
    private ServerSocket sockit;


    public MNServer(MessagingNode parent, ServerSocket sockit){
        this.parent = parent;
        this.sockit = sockit;
    }


    @Override
    public void run(){
        while(true){
            try{
                Socket routerSockit = sockit.accept();
                Thread newThread = new Thread(new MNRouter(routerSockit));
                newThread.start();
            }
            catch(Exception e){
                System.out.println("Problem accepting connection on registry server socket. " + e);
            }

        }
    }
}
