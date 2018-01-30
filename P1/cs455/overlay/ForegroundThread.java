package cs455.overlay;

import java.util.Scanner;

public class ForegroundThread implements Runnable{

    private Registry registry;
    private boolean overlaySetup;

    public ForegroundThread(Registry registry){
        this.registry = registry;
        overlaySetup = false;
    }

    public void run(){
        Scanner console = new Scanner(System.in);

        while(true){
            System.out.print("Command: ");
            String command = console.nextLine();

            String[] word = command.split(" ");

            switch(word[0]){
                case "list-messaging-nodes":
                    System.out.println(registry.getMessagingNodeInfo());
                    break;
                case "setup-overlay":
                    try{
                        overlaySetup = true;
                        int numEntries = Integer.parseInt(word[1]);
                        registry.setupOverlay(numEntries);
                    }
                    catch(Exception e){
                        registry.setupOverlay(3);       //3 is the default value
                    }
                    break;
                case "list-routing-tables":
                    if(overlaySetup){
                        System.out.print(registry.getRoutingTables());
                    }
                    else{
                        System.out.println("Overlay has not been set up. Please set up overlay before listing routing tables. ");
                    }
                    break;
                case "start":
                    try{
                        int numMessages = Integer.parseInt(word[1]);
                        registry.startSendingMessages(numMessages);
                    }
                    catch(Exception e){
                        System.out.println("Improperly formatted command. Please try again. Ex:start 100");
                    }
                    break;
                default:
                    System.out.println("Improperly formatted command. Please try again. Ex:list-messaging-nodes");
                    break;
            }
        }
    }
}
