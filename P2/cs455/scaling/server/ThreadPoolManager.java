package cs455.scaling.server;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ThreadPoolManager implements Runnable{

    private List<Task> taskQueue;
    private List<Thread> threadQueue;

    public ThreadPoolManager(){
        taskQueue = Collections.synchronizedList(new LinkedList<Task>());
        threadQueue = Collections.synchronizedList(new LinkedList<Thread>());
    }


    public void run(){
        while(true){
            Task nextTask = getNextTask();
            Thread nextThread = getNextThread();
            nextThread.request(nextTask);
        }
    }


    public void addToThreadQueue(Thread readyThread){
        synchronized(threadQueue){
            threadQueue.add(readyThread);
            threadQueue.notify();
        }
    }


    public void addToTaskQueue(Task task){
        synchronized(taskQueue){
            taskQueue.add(task);
            taskQueue.notify();
        }
    }


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
