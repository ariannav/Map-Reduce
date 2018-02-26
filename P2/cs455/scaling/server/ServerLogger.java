package cs455.scaling.server;

import java.lang.Thread;
import java.sql.Timestamp;

public class ServerLogger implements Runnable{

    private int totalRecieved;

    public ServerLogger(){
        totalRecieved = 0;
    }

    public void run(){
        while(true){
            synchronized(this){
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                System.out.println("[" + timestamp + "] Total Received Count: "+ totalRecieved);
                totalRecieved = 0;
            }

            try{
                Thread.sleep(10000);    //TODO: Change to 20000
            }
            catch(InterruptedException e){
                System.out.println("Client Logger was interrupted. " + e);
            }
        }
    }


    public synchronized void incrementTotalReceived(){
        totalRecieved++;
    }
}
