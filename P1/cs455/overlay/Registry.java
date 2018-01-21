package cs455.overlay;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Registry implements Runnable{

    public static void main(String[] argv){
        //Create registry, opens socket and finds port number.
        Registry registry = new Registry();
        System.out.println("Registry is now listening at " + registry.getInetAddress() + " on port " + registry.getPortNumber() + ".");

        //Create foreground thread.
        Thread foreground = new Thread(new ForegroundThread(registry));
        foreground.start();

        //Registry listen for incoming connections.
        registry.listen();

        System.out.println("Closing server socket, exiting program.");
        registry.closeServSocket();
        foreground.interrupt();
    }


    //==============================================Start Registry Class================================================

    private ServerSocket sockit;
    private boolean overlayInitiated;

    //Registry Constructor
    public Registry(){
        try{
            sockit = createServerSocket();
            overlayInitiated = false;
        }
        catch(IOException e){
            System.out.println("Error encountered creating socket." + e + "\n");
        }
    }


    //Opens the initial server socket and finds an acceptable port.
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


    //Gets the port number the server socket is running on.
    public int getPortNumber(){
        return sockit.getLocalPort();
    }


    //Returns the host name of the machine the server is running on.
    public InetAddress getInetAddress(){
        try{
            return sockit.getInetAddress().getLocalHost();
        }
        catch(Exception e){
            System.out.println("Cannot get local host address.");
        }
        return null;
    }


    //Listens for incoming connections, this should be going on throughout the entire program.
    public void listen(){
        while(!overlayInitiated){
            try{
                Socket messengerSockit = sockit.accept();
                MessageType message = new MessageType(messengerSockit.getInputStream());

                if(message.getTypeNumber() != 2 || message.getSuccessStatus() != 1){
                    throw new IOException("Incoming message type was not correct, or message format was incorrect.");
                }

                //TODO Using select
            }
            catch(Exception e){
                System.out.println("Problem accepting connection on registry server socket. " + e);
            }

        }
    }


    //Closes the sockets that are open.
    private void closeServSocket(){
        try{
            sockit.close();
        }
        catch(IOException e){
            System.out.println("Error closing server socket. " + e);
        }
    }


    public String getMessagingNodeInfo(){
        //TODO
        return "Nothing yet! Hi Colton!";
    }


    public void setupOverlay(int numEntries){
        //TODO
        return;
    }


    public String getRoutingTables(){
        //TODO
        return null;
    }


    public void startSendingMessages(int numMessages){
        //TODO
        return;
    }


    public void run(){
        //Foreground thread.
        //TODO: This is a thread that is connected to a new messenger node
    }

}


