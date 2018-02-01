package cs455.overlay;

public class Statistics {

    public int packetsSent;
    public long sumValuesSent;
    public int packetsRecvd;
    public int packetsRelayed;
    public long sumValuesRecvd;


    public Statistics(){
        packetsSent = 0;
        sumValuesSent = 0;
        packetsRecvd = 0;
        packetsRelayed = 0;
        sumValuesRecvd = 0;
    }
}
