package cs455.scaling.client;

import java.sql.Timestamp;

public class ClientLogger implements Runnable{

    private int totalSent;
    private int totalRecieved;

    public ClientLogger(){
        totalSent = 0;
        totalRecieved = 0;
    }

    public void run(){
        while(true){
            synchronized(this){
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                System.out.println("[" + timestamp + "] Total Sent Count: " + totalSent + ", Total Received Count: "+ totalRecieved );
                totalSent = 0;
                totalRecieved = 0;
            }

            try{
                Thread.sleep(20000);
            }
            catch(InterruptedException e){
                System.out.println("Client Logger was interrupted. " + e);
            }
        }
    }

    public synchronized void incrementTotalSent(){
        totalSent++;
    }

    public synchronized void incrementTotalReceived(){
        totalRecieved++;
    }
}
