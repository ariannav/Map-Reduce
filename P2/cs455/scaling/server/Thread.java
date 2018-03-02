//Author: Arianna Vacca
//Purpose: CS455 P2
/*Class Description: Each thread or 'Worker Thread' uses a wait-notify queue
implemented in the ThreadPoolManager to request a task. Once a task is
assigned, the thread calls the run() method of the task class. Upon task
completion, the thread requests another task. */

package cs455.scaling.server;

public class Thread implements Runnable{

    private Task task;
    private ThreadPoolManager manager;

    //Constructor
    public Thread(ThreadPoolManager manager){
        this.manager = manager;
    }

    //Runs a while(true) loop that repeatedly runs the serve() method.
    public void run(){
        while(true){
            serve();
        }
    }


    //Adds this thread to the TPM queue. When assigned a task, calls the run() method of that task. 
    public synchronized void serve(){
        manager.addToThreadQueue(this);

        try{
            wait();
        }catch(InterruptedException e){}

        task.run();
    }


    //Thread pool manager uses this method to request a task to be completed.
    public synchronized void request(Task task){
        this.task = task;
        notify();
    }
}
