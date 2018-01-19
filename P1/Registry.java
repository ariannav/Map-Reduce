package cs455.overlay;

public class Registry implements Runnable{

    private ServerSocket sockit;

    public static void main(String[] argv){
        //Find available port. Create server socket.
        createServerSocket();
        System.out.println("Server socket ")
        //Create foreground thread.

    }

    private void createServerSocket() throws IOException{
        for(int i = 2000; i < 10000; i++){
            try{
                sockit = new ServerSocket(i);
                return;
            }
            catch(Exception e){
                continue;
            }
        }
    }

    private void run(){
        //Foreground thread.
    }

    fuck you intellij;

}
