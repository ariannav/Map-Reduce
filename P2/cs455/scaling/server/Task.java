//Author: Arianna Vacca
//Purpose: CS455 P2
/*Class Description: A task object contains a run() method. This run() method is
run by the thread that is assigned to this task. Each task is created using a
key, which is used to read and write to the socket the client is connected to.*/

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
            System.out.println("Task/Run/" + e);
        }
    }


    //Reads from the key.
    private void read() throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        try{
            //Creating the channel, and reading the 8KB message.
            buffer = ByteBuffer.allocate(8000);
            buffer.clear();
            int read = 0;

            while(buffer.hasRemaining() && read != -1){
                read = channel.read(buffer);
            }

            if(read == -1){
                channel.close();
                key.cancel();
                return;
            }

            //Flips the buffer to later be read from.
            buffer.flip();
            key.interestOps(SelectionKey.OP_WRITE);
        }
        catch(IOException e){
            channel.close();
            key.cancel();
            throw new IOException("Read: " + e);
        }
    }


    //Converts the message to SHA1.
    private String SHA1FromBytes() throws NoSuchAlgorithmException{
        try{
            //Taking the bytes in the ByteBuffer and converts them to SHA1 hash,
            //then returns the created string.
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] hash = digest.digest(buffer.array());
            BigInteger hashInt = new BigInteger(1, hash);
            String returnString = hashInt.toString(16);

            return returnString;
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
            ByteBuffer length = ByteBuffer.allocate(4);
            length.putInt(returnMessage.length());
            length.rewind();
            channel.write(length);
            channel.write(ByteBuffer.wrap(returnMessage.getBytes()));

            //Sets the key back to available using its attached Statistics object. 
            synchronized(key){
                key.interestOps(SelectionKey.OP_READ);
                Statistics keyStats = (Statistics) key.attachment();
                keyStats.incrementNumSent();
                keyStats.makeAvailable();
            }

        }
        catch(IOException e){
            throw new IOException("Write: " + e);
        }
    }
}
