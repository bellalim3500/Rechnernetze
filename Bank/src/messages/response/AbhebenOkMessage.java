package messages.response;

import messages.Message;
import messages.MsgHeader;

public class AbhebenOkMessage implements Message {
    private final MsgHeader header;
    private final double amount;
    

    public AbhebenOkMessage(MsgHeader header, double amount) {
        this.header = header;
        this.amount=amount;
    }// maybe constant using generalized bye-message instead of constructing it

    @Override
    public MsgHeader header() {
        return header;
    }

    public double amount() {
        return amount;
    }

    @Override
    public String toString() {
        return "ABHEBEN_OK DISPENSED " + amount + "\r\n"; // according to ABNF abheben_req = "ABHEBEN_REQ" SP card SP amount CRLF
    }

}
