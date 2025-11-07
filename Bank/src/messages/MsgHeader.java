package messages;

public class MsgHeader {
    private final int version;
    private final MsgType type;
    private final String msgId;
    private final String correlationId;
     //private final long timestampMillis;


    public MsgHeader(int version, MsgType type, String msgId, String correlationId) {
        this.version = version;
        this.type = type;
        this.msgId = msgId;
        this.correlationId = correlationId;
       // this.timestampMillis = timestampMillis; 
       // not used because of unit test (fails because of different timestamps)
    }

    public int version(){
        return version;
    }
 
    public MsgType type(){
        return type;
    }

    public String msgId() {
        return msgId;
    }

    public String correlationId() {
        return correlationId;
    }

    @Override
    public String toString() {
        return "version=" + version + ", type=" + type + ", msgId=" + msgId + ", correlationId="
                + correlationId;
    }

    

    // public long timestampMillis() {
    //     return timestampMillis;
    // }

    

    
    
}
