package cs455.overlay;

public class Statistics {

    public int packetsSent;
    public long sumValuesSent;
    public int packetsRecvd;
    public int packetsRelayed;
    public long sumValuesRecvd;
    public int nodeID;


    public Statistics(){
        packetsSent = 0;
        sumValuesSent = 0;
        packetsRecvd = 0;
        packetsRelayed = 0;
        sumValuesRecvd = 0;
    }

    public Statistics(int nodeID, int packetsSent, int packetsRelayed, long sumValuesSent, int packetsRecvd, long sumValuesRecvd){
        this.nodeID = nodeID;
        this.packetsSent = packetsSent;
        this.packetsRelayed = packetsRelayed;
        this.sumValuesSent = sumValuesSent;
        this.packetsRecvd = packetsRecvd;
        this.sumValuesRecvd = sumValuesRecvd;
    }
}
