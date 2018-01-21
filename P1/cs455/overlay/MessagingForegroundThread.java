package cs455.overlay;

import java.util.Scanner;

public class MessagingForegroundThread implements Runnable{

    MessagingNode messager;
    boolean done;

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
                    System.out.println(messager.getDiagnostics());
                    break;
                case "exit-overlay":
                    messager.exitOverlay();
                    System.out.println("Successfully exited overlay. Exiting program.");
                    done = true;
                    break;
                default:
                    System.out.println("Improperly formatted command. Please try again. Ex:print-counters-and-diagnostics");
                    break;
            }
        }
    }
}
