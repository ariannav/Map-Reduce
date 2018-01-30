package cs455.overlay;

import java.util.Scanner;

public class MessagingForegroundThread implements Runnable{

    private MessagingNode messager;
    private boolean done;

    public MessagingForegroundThread(MessagingNode messager){
        this.messager = messager;
        done = false;
    }

    @Override
    public void run() {
        Scanner console = new Scanner(System.in);

        while(!done) {
            System.out.print("Command: ");
            String command = console.nextLine();

            String[] word = command.split(" ");

            switch (word[0]) {
                case "print-counters-and-diagnostics":
                    if(messager.isInProgress() || !messager.isFinished()){
                        System.out.println("Cannot print diagnostics until message sending is complete.");
                        break;
                    }
                    System.out.println(messager.getDiagnostics());
                    break;
                case "exit-overlay":
                    if(messager.isInProgress()){
                        System.out.println("The overlay has been constructed. This messaging node cannot exit the overlay until all tasks are finished.");
                        break;
                    }
                    messager.exitOverlay();
                    done = true;
                    break;
                case "print-routing-table":
                    if(!messager.isInProgress() && !messager.isFinished()){
                        System.out.println("Cannot print routing table until setup-overlay is complete.");
                        break;
                    }
                    System.out.print(messager.printRoutingTable());
                    break;
                default:
                    System.out.println("Improperly formatted command. Please try again. Ex:print-counters-and-diagnostics");
                    break;
            }
        }
        console.close();
    }
}
