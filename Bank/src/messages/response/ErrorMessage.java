package messages.response;

import messages.Message;
import messages.MsgHeader;

public class ErrorMessage implements Message {
    private final MsgHeader header;
    private final String reason ;

    public ErrorMessage(MsgHeader header, String reason) {
        this.header = header;
        this.reason=reason;
    }// maybe constant using generalized bye-message instead of constructing it

    @Override
    public MsgHeader header() {
        return header;
    }

    public String reason() {
        return reason;
    }

    @Override
    public String toString() {
        return "ERROR" + reason + "\r\n"; // according to ABNF abheben_req = "ABHEBEN_REQ" SP card SP amount CRLF
    }

}
