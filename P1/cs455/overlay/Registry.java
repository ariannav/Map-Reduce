package cs455.overlay;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Registry implements Runnable{

    private ServerSocket sockit;
    private boolean overlayInitiated;
    private ArrayList messengerSockits;

    public Registry(){
        try{
            sockit = createServerSocket();
            overlayInitiated = false;
            messengerSockits = new ArrayList<Socket>();
        }
        catch(IOException e){
            System.out.println("Error encountered creating socket." + e + "\n");
        }
    }

    private ServerSocket createServerSocket() throws IOException{
        for(int i = 2000; i < 10000; i++){
            try{
                ServerSocket temp = new ServerSocket(i);
                return temp;
            }
            catch(Exception e){
                continue;
            }
        }
        throw new IOException("No available ports.");
    }

    public int getPortNumber(){
        return sockit.getLocalPort();
    }

    public InetAddress getInetAddress(){
        return sockit.getInetAddress();
    }

    public void listen(){
        while(!overlayInitiated){
            try{
                Socket messengerSockit = sockit.accept();

            }
            catch(Exception e){
                System.out.println("Problem accepting connection on registry server socket. " + e);
            }

        }
    }

    public void run(){
        //Foreground thread.
        //TODO: Change overlay initiated when overlay initiated.
    }

    public static void main(String[] argv){
        //Create registry, opens socket and finds port number.
        Registry registry = new Registry();
        System.out.println("Registry is now listening at " + registry.getInetAddress() + " on port " + registry.getPortNumber() + ".");

        //Create foreground thread.
        //TODO

        //Registry listen for incoming connections.
        registry.listen();
    }
}


