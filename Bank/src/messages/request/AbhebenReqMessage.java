package messages.request;

import messages.Message;
import messages.MsgHeader;

public class AbhebenReqMessage implements Message {

    private final MsgHeader header;
    private final String card;
    private final double amount;

    public AbhebenReqMessage(MsgHeader header, String card, double amount){
        this.header=header;
        this.card=card;
        this.amount=amount;
    }

    @Override
    public MsgHeader header() {
        return header;
    }

    public String card(){
        return card;
    }
    public double amount(){
        return amount;
    }


    @Override
    public String toString() {
        return "ABHEBEN_REQ " + card + " "/*SP*/ + amount + "\r\n"; // according to ABNF abheben_req = "ABHEBEN_REQ" SP card SP amount CRLF
    }

}
