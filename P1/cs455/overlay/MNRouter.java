package cs455.overlay;

import java.net.Socket;

public class MNRouter implements Runnable{

    Socket sockit;

    public MNRouter(Socket sockit){
        this.sockit = sockit;
    }

    @Override
    public void run(){
        //TODO: Should be able to take messages from the connection. Forward/keep as necessary.
    }
}
