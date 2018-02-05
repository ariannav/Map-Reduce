package cs455.overlay.node;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class MNRouter implements Runnable{

    private MessageType process;
    private MessagingNode messager;
    private MNServer server;

    public MNRouter(Socket sockit, MessagingNode messager, MNServer server) throws IOException{

        this.messager = messager;
        this.server = server;
        try{
            DataInputStream incoming = new DataInputStream(sockit.getInputStream());
            process = new MessageType(incoming);
        }
        catch(IOException e){
            throw new IOException("Can't make input/output stream. ");
        }
    }


    @Override
    public void run(){
        try{
            while(true){
                process.processType9();
                if(process.getDestID() == messager.getNodeID()){
                    messager.addPacketRecvd();
                    messager.addSumValuesRecvd(process.getPayload());
                }
                else{
                    int[] trace = Arrays.copyOf(process.getTrace(), process.getTrace().length + 1);
                    trace[trace.length - 1] = messager.getNodeID();
                    messager.sendTo(process.getSourceID(), process.getDestID(), process.getPayload(), trace);
                    messager.addPacketRelayed();
                }
            }
        }
        catch(IOException e){
            //Socket was closed, that's okay :)
        }
    }
}
