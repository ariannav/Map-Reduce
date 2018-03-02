//Author: Arianna Vacca
//Purpose: CS455 P2
/*Class Description: The ThreadPoolManager contains two wait/notify FIFO queues
implemented using synchronized LinkedLists. One list contains tasks waiting to
be run, the other contains ready threads. The TPM delegates tasks to available
threads.*/

package cs455.scaling.server;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ThreadPoolManager implements Runnable{

    private List<Task> taskQueue;
    private List<Thread> threadQueue;

    //Constructor
    public ThreadPoolManager(){
        taskQueue = Collections.synchronizedList(new LinkedList<Task>());
        threadQueue = Collections.synchronizedList(new LinkedList<Thread>());
    }


    //Always waits for a ready task, then sends it to a ready thread.
    public void run(){

        //Grabs a task, grabs a ready thread, then asks the selected thread to
        //complete the task. 
        while(true){
            Task nextTask = getNextTask();
            Thread nextThread = getNextThread();
            nextThread.request(nextTask);
        }
    }


    //Adds a thread to the thread ready queue.
    public void addToThreadQueue(Thread readyThread){
        synchronized(threadQueue){
            threadQueue.add(readyThread);
            threadQueue.notify();
        }
    }


    //Adds a task to the task ready queue.
    public void addToTaskQueue(Task task){
        synchronized(taskQueue){
            taskQueue.add(task);
            taskQueue.notify();
        }
    }


    //Grabs and removes the first task in the queue.
    public Task getNextTask(){
        Task nextTask;
        synchronized(taskQueue){
            while(taskQueue.size() == 0){
                try{
                    taskQueue.wait();
                }
                catch(InterruptedException e){}
            }
            nextTask = taskQueue.remove(0);
        }
        return nextTask;
    }


    //Grabs and removes the first thread in the thread ready queue.
    public Thread getNextThread(){
        Thread nextThread;
        synchronized(threadQueue){
            while(threadQueue.size() == 0){
                try{
                    threadQueue.wait();
                }
                catch(InterruptedException e){}
            }
            nextThread = threadQueue.remove(0);
        }
        return nextThread;
    }
}
