package cs455.overlay;

public class MessageCreator {

    private MessagingNode messager;
    private RegistryNode registry;

    public MessageCreator(MessagingNode messager){
        this.messager = messager;
    }

    public MessageCreator(RegistryNode registry){
        this.registry = registry;
    }

    public byte[] createMessageType2(){
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

    public byte[] createMessageType3(int nodeID){
        byte type = 3;
        //TODO: If nodeID is -1, send unsuccessful message.
        return new byte[0];
    }

    public byte[] createMessageType4(){
        return new byte[0];
    }

    public byte[] createMessageType5(){
        return new byte[0];
    }

    public byte[] createMessageType6(){
        return new byte[0];
    }

    public byte[] createMessageType7(){
        return new byte[0];
    }

    public byte[] createMessageType8(){
        return new byte[0];
    }

    public byte[] createMessageType9(){
        return new byte[0];
    }

    public byte[] createMessageType10(){
        return new byte[0];
    }

    public byte[] createMessageType11(){
        return new byte[0];
    }

    public byte[] createMessageType12(){
        return new byte[0];
    }
}
