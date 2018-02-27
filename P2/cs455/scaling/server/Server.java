package cs455.scaling.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {

    public static void main(String[] argv){
        try{
            Server server = new Server(argv[0], argv[1]);
            server.startThreads();
            System.out.println("Server now active and listening on " + server.getHostName());
            server.startServer();
        }
        catch(Exception e){
            System.out.println("Server/" + e);
            e.printStackTrace();
        }

    }

    //===============================================Start Server Class=================================================

    private int numThreads;
    private ServerSocketChannel serverSocket;
    private Selector selector;
    private ThreadPoolManager threadPoolManager;
    private ServerLogger log;


    //Constructor
    public Server(String port, String numThreads) throws IOException{
        try{
            //Creating a Selector
            selector = Selector.open();

            // Setting up server socket by opening the channel, setting blocking to false,
            // and binding to the local address/provided port number.
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            bindSocket(port);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            //Setting the number of threads.
            this.numThreads = Integer.parseInt(numThreads);
        }
        catch(IOException e){
            throw new IOException("Constructor: Error encountered creating socket." + e);
        }
    }


    //Binds the socket to the proper port and address. Finds a port if the selected isn't available.
    private void bindSocket(String port) throws IOException{
        //Binding to given port number.
        try{
            serverSocket.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), Integer.parseInt(port)));
        }
        //If given port number is not valid, we cycle through the ports in an attempt to find a free one.
        catch(Exception e){
            System.out.println("Server: Provided port is not available. Binding to another available port.");
            for(int i = 2000; i < 10000; i++){
                try{
                    serverSocket.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), i));
                }
                catch(Exception f){
                    continue;
                }
            }
            //No ports are available (extremely rare).
            throw new IOException("BindSocket: No available ports.");
        }
    }


    //Starts the threads for the message processing, ThreadPoolManager, and the LoggingThread.
    private void startThreads(){
        log = new ServerLogger();
        java.lang.Thread logger = new java.lang.Thread(log);
        logger.start();

        threadPoolManager = new ThreadPoolManager();
        java.lang.Thread tpManager = new java.lang.Thread(threadPoolManager);
        tpManager.start();

        for(int i = 0; i < numThreads; i++){
            java.lang.Thread workerThread = new java.lang.Thread(new Thread(threadPoolManager));
            workerThread.start();
        }
    }


    //Starts the server, at this point it should be bound to the correct address/port.
    private void startServer() throws IOException{
        try{
            //Running the selector, this is where the current process will spend the rest of its days...
            while(true){
                //Selecting based on the options I set.
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                //Iterating through the selected keys.
                while(keys.hasNext()){
                    SelectionKey key = keys.next();
                    keys.remove();

                    if(!key.isValid()){
                        continue;
                    }
                    else if(key.isAcceptable()){
                        acceptKey(key);
                    }
                    else if(key.isReadable()){
                        createReadTask(key);
                    }
                }
            }
        }
        catch(IOException e){
            throw new IOException("StartServer:" + e);
        }
    }


    //Accepts keys
    private void acceptKey(SelectionKey key) throws IOException{
        try{
            //Accepting key
            ServerSocketChannel tempChannel = (ServerSocketChannel) key.channel();
            SocketChannel channel = tempChannel.accept();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
        }
        catch(IOException e){
            throw new IOException("AcceptKey: " + e);
        }

        log.setClients(selector.keys());
        System.out.println("Accepting incoming connection.");
    }


    //Creates and adds read task to the task queue in the ThreadPoolManager.
    private void createReadTask(SelectionKey key){
        if(key.attachment() == null){
            key.attach(new Statistics());
        }

        //Take care of key statistics, this keeps track of whether or not a key is in use.
        synchronized(key) {
            Statistics stats = (Statistics) key.attachment();
            if (!stats.isBusy()) {
                stats.makeBusy();
            }
            else{   //If the key is busy, just return.
                return;
            }
        }

        //Adding a read task to the queue managed in the ThreadPoolManager thread.
        Task readTask = new Task(key);
        threadPoolManager.addToTaskQueue(readTask);

        //Inform log the server has sent another message
        log.incrementTotalSent();
    }


    //Returns the host name of the machine the server is running on.
    public String getHostName(){
        try{
            return serverSocket.getLocalAddress().toString();
        }
        catch(Exception e){
            System.out.println("Cannot get local host address.");
        }
        return null;
    }

}
