package cs455.scaling.server;

public class Thread {

    private Task task;
    private ThreadPoolManager manager;

    public Thread(ThreadPoolManager manager){
        this.manager = manager;
    }

    public void run(){
        while(true){
            serve();
        }
    }

    public synchronized void serve(){
        //TODO: Add to thread pool manager list

        try{
            wait();
        }catch(InterruptedException e){}

        task.run();
    }

    public synchronized void request(Task task){
        this.task = task;
        notify();
    }
}
