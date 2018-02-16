package cs455.scaling.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class Server {

    public static void main(String[] argv){
        Server server = new Server(argv[0], argv[1]);
        System.out.println("Server is now listening at" + server.getInetAddress() + " on port " + server.getPortNumber() + ".");


    }

    //===============================================Start Server Class=================================================

    private int numThreads;
    private ServerSocket serverSocket;


    //Constructor
    public Server(String port, String numThreads){
        try{
            serverSocket = createServerSocket(port);
            this.numThreads = Integer.parseInt(numThreads);
        }
        catch(IOException e){
            System.out.println("Registry: Error encountered creating socket." + e + "\n");
        }
    }


    //Opens the initial server socket and finds an acceptable port.
    private ServerSocket createServerSocket(String port) throws IOException{
        try{
            return new ServerSocket(Integer.parseInt(port));
        }
        catch(Exception e){
            for(int i = 2000; i < 10000; i++){
                try{
                    ServerSocket temp = new ServerSocket(i);
                    return temp;
                }
                catch(Exception f){
                    continue;
                }
            }
            throw new IOException("No available ports.");
        }
    }


    //Gets the InetAddress associated with the server socket.
    public InetAddress getInetAddress(){
        try{
            return serverSocket.getInetAddress().getLocalHost();
        }
        catch(Exception e){
            System.out.println("Cannot get local host address.");
        }
        return null;
    }


    //Gets the port number associated with the server socket.
    public int getPortNumber(){
        return serverSocket.getLocalPort();
    }


}
