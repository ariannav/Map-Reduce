package cs455.scaling.server;

public class Thread implements Runnable{

    private Task task;
    private ThreadPoolManager manager;

    //Constructor
    public Thread(ThreadPoolManager manager){
        this.manager = manager;
    }

    public void run(){
        while(true){
            serve();
        }
    }


    //Always does tasks, then waits for another. Works through wait/notify scheme.
    public synchronized void serve(){
        manager.addToThreadQueue(this);

        try{
            wait();
        }catch(InterruptedException e){}

        task.run();
        manager.log.incrementTotalReceived();
    }


    //Thread pool manager uses this method to request a task to be completed.
    public synchronized void request(Task task){
        this.task = task;
        notify();
    }
}
