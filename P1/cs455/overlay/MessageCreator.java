package cs455.overlay;

public class MessageCreator {

    private MessagingNode messager;

    public MessageCreator(MessagingNode messager){
        this.messager = messager;
    }

    public byte[] createMessage(int type){
        switch(type){
            case 2:
                return createMessageType2();
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            case 11:
                break;
            case 12:
                break;
            default:
                String error = "Incorrect type given. Message creator.";
                return error.getBytes();
        }
        return null;
    }

    private byte[] createMessageType2(){
        //Type and IP length
        byte type = 2;
        byte length = (byte) messager.getIPAddress().length; //TODO could be causing trouble if there are issues with the type 2 message.

        int arrayLength = 1 + 1 + length + 2;
        byte[] message = new byte[arrayLength];
        message[0] = type;
        message[1] = length;

        //Put IP in array
        for(int i = 2; i < length; i++){
            message[i] = messager.getIPAddress()[i-2];
        }

        //Port number
        message[2 + length] = (byte) (messager.getPortNumber() >> 8);
        message[3 + length] = (byte) (messager.getPortNumber());

        //Sanity check
        if(arrayLength - 1 != 3 + length){
            System.out.println("Your math is wrong Ari!");
        }

        return message;
    }


}
