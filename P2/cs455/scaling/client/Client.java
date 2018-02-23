package cs455.scaling.client;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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

    private final int messagesPerSecond;
    private final SocketChannel clientChannel;
    private final Selector selector;
    private ClientLogger log;
    private SelectionKey writingKey;
    private List<String> calculatedHashValues;

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

            calculatedHashValues = Collections.synchronizedList(new LinkedList<String>());
        }
        catch(IOException e){
            throw new IOException("Constructor: " + e);
        }
    }


    //Starts the thread that prints logging stats every 20 seconds.
    private void startLoggingThread(){
        log = new ClientLogger();
        Thread loggingThread = new Thread(log);
        loggingThread.start();
    }


    //Select on the client's side. Sees if connectable or writable.
    private void startSendingMessages() throws IOException{
        try{
            while(true){
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while(keys.hasNext()){
                    SelectionKey key = keys.next();
                    keys.remove();

                    if(key.isConnectable()){
                        connect(key);
                    }
                    else if(key.isWritable() && writingKey == null){
                        System.out.println("Creating writer thread.");
                        Thread writer = new Thread(this);
                        writingKey = key;
                        writer.start();
                    }
                    else if(key.isReadable()){
                        read(key);
                    }
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


    //The writing thread sends messagesPerSecond messages to the server. Calls the write() method.
    public void run(){
        try{
            while(true){
                System.out.println("Writing...");
                write(writingKey);
                Thread.sleep(1000/messagesPerSecond);
            }
        }
        catch(Exception e){
            System.out.println("Client/Run: " + e);
        }
    }


    //Writes a random byte array when isWritable()
    private void write(SelectionKey key) throws IOException{
        System.out.println("Writing message.");
        SocketChannel channel = (SocketChannel) key.channel();
        channel.write(ByteBuffer.wrap(get8KBData()));
        key.interestOps(SelectionKey.OP_READ);
    }


    //Generates an 8KB byte array and returns it.
    private byte[] get8KBData() throws IOException{
        byte[] message = new byte[8000];
        new Random().nextBytes(message);
        calculatedHashValues.add(SHA1FromBytes(message));
        return message;
    }


    //Converts the message to SHA1.
    private String SHA1FromBytes(byte[] message) throws IOException{
        try{
            //Taking the bytes in the ByteBuffer and converts them to SHA1 hash, returns the created string.
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] hash = digest.digest(message);
            BigInteger hashInt = new BigInteger(1, hash);
            return hashInt.toString(16);
        }
        catch (NoSuchAlgorithmException n){
            throw new IOException("SHA1FromBytes:" + n);
        }
    }


    //Reads from isReadable() channel.
    private void read(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(40);
        buffer.clear();
        int read = 0;

        try{
            while(buffer.hasRemaining() && read != -1){
                read = channel.read(buffer);
                System.out.println("Read value: " + read);
            }

            if(read == -1){
                channel.close();
                key.cancel();
                return;
            }

            synchronized(calculatedHashValues){
                String returnedHash = SHA1FromBytes(buffer.array());
                if(calculatedHashValues.contains(returnedHash)){
                    calculatedHashValues.remove(returnedHash);
                    System.out.println("Got correct hash from server!");
                }
                else{
                    throw new IOException("Incorrect hash from server!");
                }
            }
        }
        catch(IOException e){
            channel.close();
            key.cancel();
            throw new IOException("Read: " + e);
        }

        buffer.flip();
        key.interestOps(SelectionKey.OP_WRITE);
    }
}
