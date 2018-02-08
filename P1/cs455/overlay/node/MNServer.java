//Author: Arianna Vacca
//Purpose: Accepts incoming connections on the MessagingNode from other MessagingNodes. Creates MNRouters.

package cs455.overlay.node;

import java.io.IOException;
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
                Thread newThread = new Thread(new MNRouter(routerSockit, parent, this));
                newThread.start();
            }
            catch(Exception e){
                try{
                    sockit.close();
                }
                catch(IOException ioe){
                    //That's fine, socket can close when done. If fails, it was already closed.
                }
            }

        }
    }

}
