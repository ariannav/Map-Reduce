package cs455.scaling.client;

import com.sun.org.apache.bcel.internal.generic.Select;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Random;

public class Client implements Runnable{

    public static void main(String[] argv){
        try{
            Client client = new Client(argv[0], argv[1], argv[2]);
            client.startLoggingThread();
            client.startSendingMessages();
        }
        catch(Exception e){
            System.out.println("Client/" + e);
        }
    }


    //===============================================Start Client Class=================================================

    private int messagesPerSecond;
    private SocketChannel clientChannel;
    private Selector selector;

    //Constructor
    public Client(String serverHost, String serverPort, String messagesPerSecond) throws IOException{
        try{
            //Confirming that the address/port is reachable.
            InetAddress.getByName(serverHost).isReachable(200);
            int port = Integer.parseInt(serverPort);

            //Setting messages per second.
            this.messagesPerSecond = Integer.parseInt(messagesPerSecond);

            //Opens the selector, opens the channel, sets blocking to false, and registers the channel as OP_CONNECT.
            selector = Selector.open();
            clientChannel = SocketChannel.open();
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_CONNECT);

            //Connects to the server given the provided port and address for the server.
            InetSocketAddress serverAddress = new InetSocketAddress(serverHost, port);
            clientChannel.connect(serverAddress);
        }
        catch(IOException e){
            throw new IOException("Constructor: " + e);
        }
    }


    //Starts the thread that prints logging stats every 20 seconds.
    private void startLoggingThread(){
        Thread loggingThread = new Thread();
        loggingThread.start();
    }


    //TODO: The thread that prints logging stats runs in this method.
    public void run(){
        System.out.println("Ready to print diagnostics!");
    }


    //Select on the client's side. Sees if connectable or writable.
    private void startSendingMessages() throws IOException{
        try{
            while(true){
                Thread.sleep(1000/messagesPerSecond);
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while(keys.hasNext()){
                    SelectionKey key = keys.next();
                    if(key.isConnectable()){
                        connect(key);
                    }
                    else if(key.isWritable()){
                        write(key);
                    }
                    else if(key.isReadable()){
                        read(key);
                    }
                    keys.remove();
                }
            }
        }
        catch(Exception e){
            throw new IOException("StartSendingMessages: " + e);
        }
    }


    //Connects when isConnectable()
    private void connect(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();
        channel.finishConnect();
        key.interestOps(SelectionKey.OP_WRITE);
    }


    //Writes a random byte array when isWritable()
    private void write(SelectionKey key) throws IOException{
        System.out.println("Writing message.");
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.wrap(get8KBData());
        channel.write(buffer);
        key.interestOps(SelectionKey.OP_READ);
    }


    //Generates an 8KB byte array and returns it.
    private byte[] get8KBData(){
        byte[] message = new byte[8000];
        new Random().nextBytes(message);
        //TODO: Add contents of this array to a linked list containing the hashed version.
        return message;
    }


    //Reads from isReadable() channel.
    private void read(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(40);
        int read = 0;

        while(buffer.hasRemaining() && read != -1){
            read = channel.read(buffer);
            //TODO: if this fails close channel and cancel key.
        }
    }
}
