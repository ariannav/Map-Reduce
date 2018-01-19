package cs455.overlay;
import java.net.ServerSocket;
import java.io.IOException;

public class Registry implements Runnable{

    ServerSocket sockit;

    public Registry(){
        try{
            sockit = createServerSocket();
        }
        catch(IOException e){
            System.out.println("Error encountered creating socket." + e + "\n");
        }
    }

    private ServerSocket createServerSocket() throws IOException{
        for(int i = 2000; i < 10000; i++){
            try{
                ServerSocket sockit = new ServerSocket(i);
                return sockit;
            }
            catch(Exception e){
                continue;
            }
        }
        throw new IOException("No available ports.");
    }

    //TODO: Make getPort() s

    public void run(){
        //Foreground thread.
    }

    public static void main(String[] argv){
        //Create registry, opens socket and finds port number.
        Registry registry = new Registry();

        //TODO: System.out.println("Server socket ");
        //Create foreground thread.
    }
}


