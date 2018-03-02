//Author: Arianna Vacca
//Purpose: CS455 P2
/*Class Description: Each Statistics object is attached to a SelectionKey via
the key's attach() method, where it keeps track of the number of messages sent
by the client the key is attached to, and whether the key is currently in use
(for example, a task has been assigned to this key already).*/

package cs455.scaling.server;

public class Statistics {

    private int numSent;
    private boolean isBusy;

    //Constructor
    public Statistics(){
        numSent = 0;
        isBusy = false;
    }

    //Sets the flag used to determine if the current key this object is attached
    //to is busy.
    public synchronized void makeBusy(){
        isBusy = true;
    }

    //Returns a flag which indicates if the selected key is being written to already.
    public synchronized boolean isBusy(){
        return isBusy;
    }

    //Makes the key available by setting the flag to false.
    public synchronized void makeAvailable(){
        isBusy = false;
    }

    //Returns the number of messages sent by this client.
    public synchronized int getNumSent(){
        return numSent;
    }

    //Increments the number of messages sent by this client.
    public synchronized void incrementNumSent(){
        numSent++;
    }

    //Sets the number of messages sent by this client to zero.
    public synchronized void resetValues(){
        numSent = 0;
    }
}
