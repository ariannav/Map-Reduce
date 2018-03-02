//Author: Arianna Vacca
//Purpose: CS455 P2
/*Class Description: Client statistics printer class. Prints client output
(number sent, number received) every 20 seconds.*/

package cs455.scaling.client;

import java.sql.Timestamp;

public class ClientLogger implements Runnable{

    private int totalSent;
    private int totalRecieved;

    //Constructor
    public ClientLogger(){
        totalSent = 0;
        totalRecieved = 0;
    }

    //Every 20 seconds, prints the total sent count and the total received count.
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

    //Safely increments the total sent. 
    public synchronized void incrementTotalSent(){
        totalSent++;
    }

    //Safely increments the total received.
    public synchronized void incrementTotalReceived(){
        totalRecieved++;
    }
}
