package cs455.scaling.server;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Task implements Runnable{

    private SelectionKey key;
    ByteBuffer buffer;

    //Constructor
    public Task(SelectionKey key){
        this.key = key;
    }


    //Runnable target run by the thread assigned to this task.
    public void run(){
        try{
            read();
            String returnMessage = SHA1FromBytes();
            write(returnMessage);
        }
        catch(Exception e){
            //TODO: Cancel the key and close the socket channel.
            System.out.println("Task/Run/" + e);
        }
    }


    //Reads from the key.
    private void read() throws IOException {
        try{
            //Creating the channel, and reading the 8KB message.
            SocketChannel channel = (SocketChannel) key.channel();
            buffer = ByteBuffer.allocate(8000);
            int read = 0;

            while(buffer.hasRemaining() && read != -1){
                read = channel.read(buffer);
            }
            //TODO: Catch you may want to cancel the key and close the socket.
            //TODO: Need to flip buffer?

            key.interestOps(SelectionKey.OP_WRITE);
        }
        catch(IOException e){
            throw new IOException("Read: " + e);
        }
    }


    //Converts the message to SHA1.
    private String SHA1FromBytes() throws NoSuchAlgorithmException{
        try{
            //Taking the bytes in the ByteBuffer and converts them to SHA1 hash, returns the created string.
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] hash = digest.digest(buffer.array());
            BigInteger hashInt = new BigInteger(1, hash);
            return hashInt.toString(16);
        }
        catch (NoSuchAlgorithmException n){
            throw new NoSuchAlgorithmException("SHA1FromBytes:" + n);
        }
    }


    //Writes the response to the assigned key.
    private void write(String returnMessage) throws IOException{
        try {
            //Writing to the channel
            SocketChannel channel = (SocketChannel) key.channel();

            if (returnMessage.length() != 40) {
                throw new IOException("Write: Message length is not 40 characters!");
            }

            buffer = ByteBuffer.wrap(returnMessage.getBytes());
            buffer.flip();
            channel.write(buffer);
            key.interestOps(SelectionKey.OP_READ);
        }
        catch(IOException e){
            throw new IOException("Write: " + e);
        }
    }
}
