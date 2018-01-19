package cs455.overlay;

import java.io.IOException;
import java.io.InputStream;

public class MessageType {

    private int typeNumber;
    private byte[] ip;
    private int port;
    private int successStatus;
    private byte[] infoString;

    public MessageType(InputStream message) throws IOException{
        try{
            typeNumber = message.read();
        }
        catch(IOException e){
            throw e;
        }

        switch(typeNumber){
            case 2: //OVERLAY_NODE_SENDS_REGISTRATION
                processType2(message);
                break;
            case 3: //REGISTRY_REPORTS_REGISTRATION_STATUS
                processType3(message);
                break;
            case 4: //OVERLAY_NODE_SENDS_DEREGISTRATION
                processType4(message);
                break;
            case 5: //REGISTRY_REPORTS_DEREGISTRATION_STATUS
                processType5(message);
                break;
            case 6: //REGISTRY_SENDS_NODE_MANIFEST
                processType6(message);
                break;
            case 7: //NODE_REPORTS_OVERLAY_SETUP_STATUS
                processType7(message);
                break;
            case 8: //REGISTRY_REQUESTS_TASK_INITIATE
                processType8(message);
                break;
            case 9: //OVERLAY_NODE_SENDS_DATA
                processType9(message);
                break;
            case 10: //OVERLAY_NODE_REPORTS_TASK_FINISHED
                processType10(message);
                break;
            case 11: //REGISTRY_REQUESTS_TRAFFIC_SUMMARY
                processType11(message);
                break;
            case 12: //OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY
                processType12(message);
                break;
            default:
                throw new IOException("Incorrect message type received.");
        }
    }

    private void processType2(InputStream message){ //OVERLAY_NODE_SENDS_REGISTRATION
        try{
            //Get IP length and read IP into variable ip.
            int ipLength = message.read();
            ip = new byte[ipLength];
            message.read(ip);

            //Get port number
            port = message.read();
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 2.");
        }

        successStatus = 1;
    }

    private void processType3(InputStream message){ //REGISTRY_REPORTS_REGISTRATION_STATUS
        try{
            //Get successStatus
            successStatus = message.read();

            //Get Info String length and Info String
            int infoStringLength = message.read();
            infoString = new byte[infoStringLength];
            message.read(infoString);
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 3.");
        }
    }

    private void processType4(InputStream message){  //TODO: OVERLAY_NODE_SENDS_DEREGISTRATION
        try{
            //Get IP length and read IP into variable ip.
            int ipLength = message.read();
            ip = new byte[ipLength];
            message.read(ip);

            //Get port number
            port = message.read();
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 2.");
        }
    }

    private void processType5(InputStream message){  //TODO: REGISTRY_REPORTS_DEREGISTRATION_STATUS
        try{
            //Get IP length and read IP into variable ip.
            int ipLength = message.read();
            ip = new byte[ipLength];
            message.read(ip);

            //Get port number
            port = message.read();
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 2.");
        }
    }

    private void processType6(InputStream message){  //TODO: REGISTRY_SENDS_NODE_MANIFEST
        try{
            //Get IP length and read IP into variable ip.
            int ipLength = message.read();
            ip = new byte[ipLength];
            message.read(ip);

            //Get port number
            port = message.read();
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 2.");
        }
    }

    private void processType7(InputStream message){  //TODO: NODE_REPORTS_OVERLAY_SETUP_STATUS
        try{
            //Get IP length and read IP into variable ip.
            int ipLength = message.read();
            ip = new byte[ipLength];
            message.read(ip);

            //Get port number
            port = message.read();
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 2.");
        }
    }

    private void processType8(InputStream message){  //TODO: REGISTRY_REQUESTS_TASK_INITIATE
        try{
            //Get IP length and read IP into variable ip.
            int ipLength = message.read();
            ip = new byte[ipLength];
            message.read(ip);

            //Get port number
            port = message.read();
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 2.");
        }
    }

    private void processType9(InputStream message){  //TODO: OVERLAY_NODE_SENDS_DATA
        try{
            //Get IP length and read IP into variable ip.
            int ipLength = message.read();
            ip = new byte[ipLength];
            message.read(ip);

            //Get port number
            port = message.read();
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 2.");
        }
    }

    private void processType10(InputStream message){  //TODO: OVERLAY_NODE_REPORTS_TASK_FINISHED
        try{
            //Get IP length and read IP into variable ip.
            int ipLength = message.read();
            ip = new byte[ipLength];
            message.read(ip);

            //Get port number
            port = message.read();
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 2.");
        }
    }

    private void processType11(InputStream message){  //TODO: REGISTRY_REQUESTS_TRAFFIC_SUMMARY
        try{
            //Get IP length and read IP into variable ip.
            int ipLength = message.read();
            ip = new byte[ipLength];
            message.read(ip);

            //Get port number
            port = message.read();
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 2.");
        }
    }

    private void processType12(InputStream message){  //TODO: OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY
        try{
            //Get IP length and read IP into variable ip.
            int ipLength = message.read();
            ip = new byte[ipLength];
            message.read(ip);

            //Get port number
            port = message.read();
        }
        catch(IOException e){
            System.out.println("Incorrect message format. Message type 2.");
        }
    }

    public int getTypeNumber(){
        return typeNumber;
    }

    public byte[] getIP(){
        return ip;
    }

    public int getPort(){
        return port;
    }

    public int getSuccessStatus(){
        return successStatus;
    }
}
