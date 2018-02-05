package cs455.overlay.node;

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
                        int numEntries = Integer.parseInt(word[1]);
                        if(numEntries*2 >= registry.getNumNodes()){
                            System.out.println("Number of nodes in the overlay: " + registry.getNumNodes() + ". Please give an overlay size < " + registry.getNumNodes()/2);
                            break;
                        }
                        registry.setupOverlay(numEntries);
                        overlaySetup = true;
                    }
                    catch(Exception e){
                        if(6 >= registry.getNumNodes()){
                            System.out.println("Number of nodes in the overlay: " + registry.getNumNodes() + ". Please give an overlay size < " + registry.getNumNodes()/2);
                            break;
                        }
                        registry.setupOverlay(3);       //3 is the default value
                        overlaySetup = true;
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
                        if(overlaySetup){
                            registry.startSendingMessages(numMessages);
                        }
                        else{
                            System.out.println("Cannot send messages until the overlay is constructed. Please setup-overlay first.");
                        }
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
