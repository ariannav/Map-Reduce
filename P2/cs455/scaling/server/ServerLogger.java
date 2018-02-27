package cs455.scaling.server;

import java.lang.Thread;
import java.nio.channels.SelectionKey;
import java.sql.Timestamp;
import java.util.Set;

public class ServerLogger implements Runnable{

    private int totalSent;
    public Set<SelectionKey> clients;

    public ServerLogger(){
        totalSent = 0;
        clients = null;
    }

    public void run(){
        while(true){
            synchronized(this){
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                System.out.println("[" + timestamp + "] Server Throughput: " + totalSent/20 + " messages/s"
                        + ", Active Client Connections: " + getNumClients()
                        + ", Mean Per-client Throughput: " + getMeanThroughput()/20 + " messages/s"
                        + ", Std. Dev. of Per-client Throughput: " + getStdDeviation()/20 + " messages/s");
                totalSent = 0;
                resetClientCounters();
            }

            try{
                Thread.sleep(20000);
            }
            catch(InterruptedException e){
                System.out.println("Client Logger was interrupted. " + e);
            }
        }
    }

    //Not synchronized for getNumClients, getMeanThroughput, getStdDeviation, and resetClientCounters.
    //They are always run from inside of a synchronized block.

    private int getNumClients(){
        if(clients == null){
            return 0;
        }

        return clients.size()-1;
    }


    private double getMeanThroughput(){
        if(clients == null){
            return 0;
        }

        SelectionKey[] keys = new SelectionKey[clients.size()];
        clients.toArray(keys);

        int sumSent = 0;
        for(int i = 0; i < keys.length; i++){
            if(keys[i].attachment() == null){
                continue;
            }
            Statistics stats = (Statistics) keys[i].attachment();
            sumSent += stats.getNumSent();
        }

        return (sumSent/getNumClients());
    }


    private double getStdDeviation(){
        if(clients == null){
            return 0;
        }

        SelectionKey[] keys = new SelectionKey[clients.size()];
        clients.toArray(keys);

        double temp = 0;
        double mean = getMeanThroughput();
        for(int i = 0; i < keys.length; i++){
            if(keys[i].attachment() == null){
                continue;
            }
            Statistics stats = (Statistics) keys[i].attachment();
            temp += (stats.getNumSent() - mean) * (stats.getNumSent() - mean);
        }

        temp = temp / (getNumClients() - 1);

        return Math.sqrt(temp);
    }


    private void resetClientCounters(){
        if(clients == null){
            return;
        }

        SelectionKey[] keys = new SelectionKey[clients.size()];
        clients.toArray(keys);

        for (int i = 0; i < keys.length; i++) {
            if (keys[i].attachment() == null) {
                continue;
            }
            Statistics stats = (Statistics) keys[i].attachment();
            stats.resetValues();
        }
    }


    public synchronized void setClients(Set<SelectionKey> clients){
        this.clients = clients;
    }


    public synchronized void incrementTotalSent(){
        totalSent++;
    }
}
