package cs455.scaling.server;

public class Statistics {

    private int numSent;
    private boolean isBusy;

    public Statistics(){
        numSent = 0;
        isBusy = false;
    }

    public synchronized void makeBusy(){
        isBusy = true;
    }

    public synchronized boolean isBusy(){
        return isBusy;
    }

    public synchronized void makeAvailable(){
        isBusy = false;
    }

    public synchronized int getNumSent(){
        return numSent;
    }

    public synchronized void incrementNumSent(){
        numSent++;
    }

    public synchronized void resetValues(){
        numSent = 0;
    }
}
