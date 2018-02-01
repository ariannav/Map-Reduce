package cs455.overlay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MNRouter implements Runnable{

    Socket sockit;
    DataOutputStream outgoing;
    DataInputStream incoming;
    MessageType process;
    MessageCreator creator;

    public MNRouter(Socket sockit, MessagingNode messager) throws IOException{

        this.sockit = sockit;
        try{
            outgoing = new DataOutputStream(sockit.getOutputStream());
            incoming = new DataInputStream(sockit.getInputStream());
        }
        catch(IOException e){
            throw new IOException("Can't make input/output stream. ");
        }
        process = new MessageType(incoming);
        creator = new MessageCreator(messager);
    }


    @Override
    public void run(){
        try{
            process.processType9();
            //TODO Got a type 9, now need to process it and forward/keep. Update messenger variables when necessary.
        }
        catch(IOException e){
            System.out.println("Could not read message from other messenger.");
            System.exit(1);
        }
    }
}
