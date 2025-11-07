package messages;

public class Card implements Message{

    private final MsgHeader h;
    private final int cardNo;
    public Card(MsgHeader h, int cardNo) {
        this.h = h;
        this.cardNo = cardNo;
    }
    public MsgHeader header() {
        return h;
    }
    public int cardNo() {
        return cardNo;
    }
    
    
}
